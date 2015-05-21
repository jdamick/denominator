package denominator.ultradns;

import com.squareup.okhttp.mockwebserver.MockResponse;
import denominator.DNSApiManager;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InvalidatableAuthenticationProviderTest {
  @Rule
  public MockUltraDNSServer server = new MockUltraDNSServer();

  @Test
  public void verifyUsernameAndPassword() throws Exception {
    server.enqueueValidOAuthResponse();
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"Good\"}"));

    DNSApiManager api = server.connect();

    assertTrue(api.checkConnection());
    server.assertOAuthRequest();
    server.assertRequest().hasHeaderContaining("Authorization", "Bearer " + server.accessToken());
  }
}
