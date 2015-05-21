package denominator.ultradns;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.rule.MockWebServerRule;
import denominator.Credentials;
import denominator.CredentialsConfiguration;
import denominator.DNSApiManager;
import denominator.Denominator;
import denominator.assertj.RecordedRequestAssert;
import java.io.IOException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static denominator.assertj.MockWebServerAssertions.assertThat;
import static java.lang.String.format;

public class MockUltraDNSServer extends UltraDNSProvider implements TestRule {

  private final MockWebServerRule delegate = new MockWebServerRule();
  private final String accessToken = "e2858e0c249a459ab9a5b78ace";
  private String customer = "jclouds";
  private String username = "joe";
  private String password = "letmein";
  private String oauthResponse;

  MockUltraDNSServer() {
    credentials(customer, username, password);
  }

  /*String token() {
    return token;
  }*/

  @Override
  public String url() {
    return "http://localhost:" + delegate.getPort();
  }

  DNSApiManager connect() {
    return Denominator.create(this, CredentialsConfiguration.credentials(credentials()));
  }

  String accessToken() {
    return accessToken;
  }

  Credentials credentials() {
    return Credentials.ListCredentials.from(username, password);
  }

  MockUltraDNSServer credentials(String customer, String username, String password) {
    this.customer = customer;
    this.username = username;
    this.password = password;
    this.oauthResponse = "{\"tokenType\":\"Bearer\"," +
        "\"refreshToken\":\"1v3afb78356c42a91335e726c112cdc0b148629e97g\"," +
        "\"accessToken\":\"e2858e0c249a459ab9a5b78ace\"," +
        "\"expiresIn\":\"3600\",\"expires_in\":\"3600\"," +
        "\"token_type\":\"Bearer\"," +
        "\"refresh_token\":\"1v3afb78356c42a91335e726c112cdc0b148629e97g\"," +
        "\"access_token\":\"" + accessToken + "\"}";
    return this;
  }

  void enqueueValidOAuthResponse() {
    delegate.enqueue(new MockResponse().setBody(oauthResponse));
  }

  void enqueue(MockResponse mockResponse) {
    delegate.enqueue(mockResponse);
  }

  RecordedRequestAssert assertRequest() throws InterruptedException {
    return assertThat(delegate.takeRequest());
  }

  RecordedRequestAssert assertOAuthRequest() throws InterruptedException {
    return assertThat(delegate.takeRequest())
        .hasMethod("POST")
        .hasPath("/authorization/token")
        .hasHeaderContaining("Content-Type", "application/x-www-form-urlencoded")
        .hasBody(format("grant_type=password&username=%s&password=%s",
            username, password));
  }


  void shutdown() throws IOException {
    delegate.get().shutdown();
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return delegate.apply(base, description);
  }

  @dagger.Module(injects = DNSApiManager.class, complete = false, includes =
      UltraDNSProvider.Module.class)
  static final class Module {

  }
}
