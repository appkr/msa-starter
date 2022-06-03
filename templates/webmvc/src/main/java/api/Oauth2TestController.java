package {{packageName}}.api;

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
 * Then: Use API resources with the given ACCESS_TOKEN
 *  ```bash
 *  $ curl -sSf -H "Authorization: bearer ${ACCESS_TOKEN}" http://localhost:{{portNumber}}/user
 *  {
 *    "authorities": [
 *      {
 *        "authority": "ROLE_USER"
 *      }
 *    ],
 *    "details": {
 *      "remoteAddress": "0:0:0:0:0:0:0:1",
 *      "sessionId": null,
 *      "tokenValue": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1c2VyIiwic2NvcGUiOlsib3BlbmlkIl0sImV4cCI6MTYwNzMzMjYzNiwiaWF0IjoxNjA3MzMyMzM2LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoibEI0NUlrNDlDcHBDc2wtbkdscGx0dm50QlJrIiwiY2xpZW50X2lkIjoid2ViX2FwcCJ9.K2cg88xQ9SJe-ewhl_zFf9hE_XevAZcXq-JEmoTP5-Z31Y93R8pB8nyueIGQ6mefEmJRaqI4Tr8MUi1drADbGgQhej-9FXEP-rf0ENaNuPxRJKlJb3wXZFRuLBxmuiihIG0-h14aSlbnckpUGjnbVU_m0jsIlNpyGxWVh05wW1AodbqWTYf6hzEqdpSvXhoCYWt3VwAlWlyko4NHyQEWWjRo0f4dOHzS0AAr3cKjSLPDmkSylWGSh3p4ynq13K2pRw-gPGrAx1yWki_YF5c4PWx0IAwuY2f6vnWmrAvI1Oam-YKgYhjRv3tZvHvNsn38rVU8scPnD04W3Cio9JBthQ",
 *      "tokenType": "bearer",
 *      "decodedDetails": null
 *    },
 *    "authenticated": true,
 *    "userAuthentication": {
 *      "authorities": [
 *        {
 *          "authority": "ROLE_USER"
 *        }
 *      ],
 *      "details": null,
 *      "authenticated": true,
 *      "principal": "user",
 *      "credentials": "N/A",
 *      "name": "user"
 *    },
 *    "credentials": "",
 *    "principal": "user",
 *    "clientOnly": false,
 *    "oauth2Request": {
 *      "clientId": "web_app",
 *      "scope": [
 *        "openid"
 *      ],
 *      "requestParameters": {
 *        "client_id": "web_app"
 *      },
 *      "resourceIds": [],
 *      "authorities": [],
 *      "approved": true,
 *      "refresh": false,
 *      "redirectUri": null,
 *      "responseTypes": [],
 *      "extensions": {},
 *      "grantType": null,
 *      "refreshTokenRequest": null
 *    },
 *    "name": "user"
 *  }
 *  ```
 */
@RestController
public class Oauth2TestController {

  /**
   * @return {@link org.springframework.security.oauth2.provider.OAuth2Authentication}
   */
  @GetMapping("/user")
  public Authentication user() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
