package {{packageName}}.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

@Configuration
@Profile("!test")
public class Oauth2ClientConfiguration {

  @Bean
  @ConfigurationProperties("security.oauth2.client")
  public OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails() {
    return new ClientCredentialsResourceDetails();
  }

  @Bean
  public OAuth2ClientContext clientContext() {
    return new DefaultOAuth2ClientContext();
  }
}
