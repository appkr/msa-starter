package {{packageName}};

import {{packageName}}.config.ApplicationProperties;
import java.net.InetAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties({ApplicationProperties.class, OAuth2ClientProperties.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		ApplicationContext ctx = app.run(args);

		final String profiles = StringUtils.join(ctx.getEnvironment().getActiveProfiles(), ",");
		final String version = ctx.getBean(ApplicationProperties.class).getVersion();
		final GitProperties gitInfo = ctx.getBean(GitProperties.class);
		final ServerProperties serverInfo = ctx.getBean(ServerProperties.class);

		// @formatter:off
		String textBanner = String.format("\n----------------------------------------------------------\n"
						+ "Spring profiles \u001B[31m%s\u001B[0m\n"
						+ "Application version \u001B[31m%s\u001B[0m\n"
						+ "Git branch \u001B[31m%s\u001B[0m; commit \u001B[31m%s\u001B[0m\n"
						+ "\n"
						+ "Application is running!\n"
						+ "%s"
						+ "\n----------------------------------------------------------\n",
				profiles, version, gitInfo.getBranch(), gitInfo.getShortCommitId(), getHealthEndpoint(serverInfo));
		// @formatter:on

		System.out.println(textBanner);
	}

	static String getHealthEndpoint(ServerProperties serverInfo) {
		final String scheme = (serverInfo.getSsl() != null && serverInfo.getSsl().isEnabled()) ? "https" : "http";
		final InetAddress addr = serverInfo.getAddress();
		final String host = (addr == null) ? "localhost" : addr.getHostName();

		return String.format("%s://%s:%s/management/health", scheme, host, serverInfo.getPort());
	}
}
