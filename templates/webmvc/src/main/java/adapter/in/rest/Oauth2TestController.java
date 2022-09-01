package {{packageName}}.adapter.in.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Given:
 *  UAA is working at port 9999
 *
 *  When: Login to UAA and save access_token to ACCESS_TOKEN variable
 *  ```bash
 *  $ RESPONSE=$(curl -s -X POST --data "username=user&password=user&grant_type=password&scope=openid" http://web_app:changeit@localhost:9999/oauth/token)
 *  $ ACCESS_TOKEN=$(echo $RESPONSE | jq .access_token | xargs)
 *
 * Then: Respond JwtAuthenticationToken
 *  ```bash
 *  $ curl -sSf -H "Authorization: bearer ${ACCESS_TOKEN}" http://localhost:{{portNumber}}/user
 *  {
 *     "authorities": [
 *         {
 *             "authority": "SCOPE_web-app"
 *         }
 *     ],
 *     "details": {
 *         "remoteAddress": "0:0:0:0:0:0:0:1",
 *         "sessionId": null
 *     },
 *     "authenticated": true,
 *     "principal": {
 *         "tokenValue": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJ3ZWItYXBwIl0sImV4cCI6MTY2MjAzMjE3OSwiaWF0IjoxNjYyMDMwMzc5LCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl0sImp0aSI6IllodUZ3UDJ0T0RqeEtNSGZ3dm5CMW5aWDZNQSIsImNsaWVudF9pZCI6ImludGVybmFsIn0.BAToa_7j_NhntJikpfovVY6jRDNCSVP9hWN2c14zl1RGAOFVpL_PdLuRQAx0hapjWRgRUbgVbESFCwlV-W0UNlgSeEkh4WIT_BKujaviGGCl0gurxt1O_PsjXUiU-T6HOFQMz_70mHwWNwCBWcUQ4aK-LTVdarp-YVPUuIH18dllH3KNW2-mnS1VC0nIOtiQRP4dh8J5opaQc1njQXuF5FNn9KAfZdw9HudIdgm8QjP71zEu0mF7b6vvqNJnDX7Fi70lyw-9SReJotvfFXFrt7k5xM8eQ_gI4XPPZkCG0kuUdtDRw844ke0PlQOiipIZm9G7hEAj-JOwscx76BorEg",
 *         "issuedAt": "2022-09-01T11:06:19Z",
 *         "expiresAt": "2022-09-01T11:36:19Z",
 *         "headers": {
 *             "typ": "JWT",
 *             "alg": "RS256"
 *         },
 *         "claims": {
 *             "scope": [
 *                 "web-app"
 *             ],
 *             "exp": "2022-09-01T11:36:19Z",
 *             "iat": "2022-09-01T11:06:19Z",
 *             "authorities": [
 *                 "ROLE_ADMIN"
 *             ],
 *             "jti": "YhuFwP2tODjxKMHfwvnB1nZX6MA",
 *             "client_id": "internal"
 *         },
 *         "id": "YhuFwP2tODjxKMHfwvnB1nZX6MA",
 *         "audience": null,
 *         "issuer": null,
 *         "subject": null,
 *         "notBefore": null
 *     },
 *     "credentials": {
 *         "tokenValue": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJ3ZWItYXBwIl0sImV4cCI6MTY2MjAzMjE3OSwiaWF0IjoxNjYyMDMwMzc5LCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl0sImp0aSI6IllodUZ3UDJ0T0RqeEtNSGZ3dm5CMW5aWDZNQSIsImNsaWVudF9pZCI6ImludGVybmFsIn0.BAToa_7j_NhntJikpfovVY6jRDNCSVP9hWN2c14zl1RGAOFVpL_PdLuRQAx0hapjWRgRUbgVbESFCwlV-W0UNlgSeEkh4WIT_BKujaviGGCl0gurxt1O_PsjXUiU-T6HOFQMz_70mHwWNwCBWcUQ4aK-LTVdarp-YVPUuIH18dllH3KNW2-mnS1VC0nIOtiQRP4dh8J5opaQc1njQXuF5FNn9KAfZdw9HudIdgm8QjP71zEu0mF7b6vvqNJnDX7Fi70lyw-9SReJotvfFXFrt7k5xM8eQ_gI4XPPZkCG0kuUdtDRw844ke0PlQOiipIZm9G7hEAj-JOwscx76BorEg",
 *         "issuedAt": "2022-09-01T11:06:19Z",
 *         "expiresAt": "2022-09-01T11:36:19Z",
 *         "headers": {
 *             "typ": "JWT",
 *             "alg": "RS256"
 *         },
 *         "claims": {
 *             "scope": [
 *                 "web-app"
 *             ],
 *             "exp": "2022-09-01T11:36:19Z",
 *             "iat": "2022-09-01T11:06:19Z",
 *             "authorities": [
 *                 "ROLE_ADMIN"
 *             ],
 *             "jti": "YhuFwP2tODjxKMHfwvnB1nZX6MA",
 *             "client_id": "internal"
 *         },
 *         "id": "YhuFwP2tODjxKMHfwvnB1nZX6MA",
 *         "audience": null,
 *         "issuer": null,
 *         "subject": null,
 *         "notBefore": null
 *     },
 *     "token": {
 *         "tokenValue": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJ3ZWItYXBwIl0sImV4cCI6MTY2MjAzMjE3OSwiaWF0IjoxNjYyMDMwMzc5LCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl0sImp0aSI6IllodUZ3UDJ0T0RqeEtNSGZ3dm5CMW5aWDZNQSIsImNsaWVudF9pZCI6ImludGVybmFsIn0.BAToa_7j_NhntJikpfovVY6jRDNCSVP9hWN2c14zl1RGAOFVpL_PdLuRQAx0hapjWRgRUbgVbESFCwlV-W0UNlgSeEkh4WIT_BKujaviGGCl0gurxt1O_PsjXUiU-T6HOFQMz_70mHwWNwCBWcUQ4aK-LTVdarp-YVPUuIH18dllH3KNW2-mnS1VC0nIOtiQRP4dh8J5opaQc1njQXuF5FNn9KAfZdw9HudIdgm8QjP71zEu0mF7b6vvqNJnDX7Fi70lyw-9SReJotvfFXFrt7k5xM8eQ_gI4XPPZkCG0kuUdtDRw844ke0PlQOiipIZm9G7hEAj-JOwscx76BorEg",
 *         "issuedAt": "2022-09-01T11:06:19Z",
 *         "expiresAt": "2022-09-01T11:36:19Z",
 *         "headers": {
 *             "typ": "JWT",
 *             "alg": "RS256"
 *         },
 *         "claims": {
 *             "scope": [
 *                 "web-app"
 *             ],
 *             "exp": "2022-09-01T11:36:19Z",
 *             "iat": "2022-09-01T11:06:19Z",
 *             "authorities": [
 *                 "ROLE_ADMIN"
 *             ],
 *             "jti": "YhuFwP2tODjxKMHfwvnB1nZX6MA",
 *             "client_id": "internal"
 *         },
 *         "id": "YhuFwP2tODjxKMHfwvnB1nZX6MA",
 *         "audience": null,
 *         "issuer": null,
 *         "subject": null,
 *         "notBefore": null
 *     },
 *     "name": null,
 *     "tokenAttributes": {
 *         "scope": [
 *             "web-app"
 *         ],
 *         "exp": "2022-09-01T11:36:19Z",
 *         "iat": "2022-09-01T11:06:19Z",
 *         "authorities": [
 *             "ROLE_ADMIN"
 *         ],
 *         "jti": "YhuFwP2tODjxKMHfwvnB1nZX6MA",
 *         "client_id": "internal"
 *     }
 * }
 *  ```
 */
@RestController
public class Oauth2TestController {

  @GetMapping("/user")
  public Authentication user() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
