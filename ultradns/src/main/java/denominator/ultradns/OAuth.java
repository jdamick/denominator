package denominator.ultradns;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

interface OAuth {
  @Headers("Content-Type: application/x-www-form-urlencoded")
  @RequestLine("POST /authorization/token")
  @Body("grant_type=password&username={username}&password={password}")
  OAuthResponse authorization(@Param("username") String username,
                              @Param("password") String password);

  class OAuthResponse {
    String accessToken;
    String refreshToken;
    String expiresIn;
    String tokenType;
  }
}
