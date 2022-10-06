package {{packageName}}.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.netty.LogbookClientHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

  public ReactorClientHttpConnector clientHttpConnector(@Qualifier("outboundLogbook") Logbook logbook) {
    // Reference: https://github.com/zalando/logbook#netty
    final HttpClient httpClient = HttpClient
        .create()
        .doOnConnected(conn -> conn.addHandlerLast(new LogbookClientHandler(logbook)));

    return new ReactorClientHttpConnector(httpClient);
  }
}
