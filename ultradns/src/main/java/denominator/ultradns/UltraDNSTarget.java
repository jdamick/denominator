package denominator.ultradns;

import denominator.Provider;
import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import java.net.URI;
import java.util.Map;
import javax.inject.Inject;

class UltraDNSTarget implements Target<UltraDNS> {


  private final Provider provider;
  private final InvalidatableAuthenticationProvider lazyAuthHeaders;

  @Inject
  UltraDNSTarget(Provider provider, InvalidatableAuthenticationProvider lazyAuthHeaders) {
    this.provider = provider;
    this.lazyAuthHeaders = lazyAuthHeaders;
  }

  @Override
  public Class<UltraDNS> type() {
    return UltraDNS.class;
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
  public Request apply(RequestTemplate in) {
    in.insert(0, url());

    Map<String, String> headers = lazyAuthHeaders.get();
    for (Map.Entry<String, String> header : headers.entrySet()) {
      in.header(header.getKey(), header.getValue());
    }

//    in.body(format(SOAP_TEMPLATE, username, password, new String(in.body(), UTF_8)));
    in.header("Host", URI.create(in.url()).getHost());
    return in.request();
  }
}
