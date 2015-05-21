package denominator.ultradns;

import com.google.common.base.Charsets;
import denominator.Credentials;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import javax.inject.Provider;

import static denominator.common.Preconditions.checkNotNull;

class InvalidatableAuthenticationProvider {

  private final AtomicReference<Boolean> sessionValid = new AtomicReference<Boolean>(false);
  private final Provider<Credentials> credentials;
  private final OAuth oauth;
  private volatile OAuth.OAuthResponse cachedOAuthResponse;

  @Inject
  InvalidatableAuthenticationProvider(Provider<Credentials> credentials, OAuth oath) {
    this.credentials = credentials;
    this.oauth = oath;
  }

  Map<String, String> get() {
    return auth(credentials.get());
  }

  Map<String, String> auth(Credentials creds) {
    String username;
    String password;
    if (creds instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> listCreds = (List<Object>) creds;
      username = listCreds.get(0).toString();
      password = listCreds.get(1).toString();
    } else if (creds instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> mapCreds = (Map<String, Object>) creds;
      username = checkNotNull(mapCreds.get("username"), "username").toString();
      password = checkNotNull(mapCreds.get("password"), "password").toString();
    } else {
      throw new IllegalArgumentException("Unsupported credential type: "+ creds);
    }

    try {
      username = URLEncoder.encode(username, Charsets.UTF_8.name());
      password = URLEncoder.encode(password, Charsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Error encoding authentication credentials", e);
    }

    Map<String, String> headers = new HashMap<String, String>();
    synchronized (this) {
      cachedOAuthResponse = oauth.authorization(username, password);

      headers.put("Authorization",
          cachedOAuthResponse.tokenType + " " + cachedOAuthResponse.accessToken);
      sessionValid.set(true);
    }

    return headers;
  }
}
