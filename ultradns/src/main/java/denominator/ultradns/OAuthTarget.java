package denominator.ultradns;

import denominator.Provider;
import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import javax.inject.Inject;

public class OAuthTarget implements Target<OAuth> {
  private final Provider provider;

  @Inject
  OAuthTarget(Provider provider) {
    this.provider = provider;
  }

  @Override
  public Class<OAuth> type() {
    return OAuth.class;
  }

  @Override
  public String name() {
    return provider.name();
  }

  @Override
  public String url() {
    return provider.url();
  }

  @Override
  public Request apply(RequestTemplate input) {
    input.insert(0, url());
    return input.request();
  }
}
