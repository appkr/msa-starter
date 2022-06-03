package {{packageName}}.config;

import {{packageName}}.repository.converter.InstantConverter;
import {{packageName}}.repository.converter.PersistentEventStatusConverter;
import {{packageName}}.repository.converter.UUIDConverter;
import {{packageName}}.support.SecurityUtils;
import io.r2dbc.spi.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.r2dbc.query.UpdateMapper;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.relational.core.dialect.RenderContextFactory;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableR2dbcRepositories(basePackages = { "{{packageName}}.repository" })
@EnableTransactionManagement
@EnableR2dbcAuditing(auditorAwareRef = "myAuditorProvider")
public class DatabaseConfiguration {

  @Bean
  public ReactiveAuditorAware<UUID> myAuditorProvider() {
    return () -> SecurityUtils.getCurrentUserId();
  }

  @Bean
  public R2dbcDialect dialect(ConnectionFactory connectionFactory) {
    return DialectResolver.getDialect(connectionFactory);
  }

  @Bean
  public UpdateMapper updateMapper(R2dbcDialect dialect, MappingR2dbcConverter mappingR2dbcConverter) {
    return new UpdateMapper(dialect, mappingR2dbcConverter);
  }

  @Bean
  public SqlRenderer sqlRenderer(R2dbcDialect dialect) {
    RenderContextFactory factory = new RenderContextFactory(dialect);
    return SqlRenderer.create(factory.createRenderContext());
  }

  @Bean
  public R2dbcCustomConversions r2dbcCustomConversions(R2dbcDialect dialect) {

    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(new InstantConverter.ReadConverter());
    converters.add(new InstantConverter.WriteConverter());
    converters.add(new UUIDConverter.ReadConverter());
    converters.add(new UUIDConverter.WriteConverter());
    converters.add(new PersistentEventStatusConverter.ReadConverter());
    converters.add(new PersistentEventStatusConverter.WriteConverter());

    return R2dbcCustomConversions.of(dialect, converters);
  }

  @Bean
  public NamingStrategy customNamingStrategy() {
    return new NamingStrategy() {
      @Override
      public String getColumnName(RelationalPersistentProperty property) {
        return apply(property.getName());
      }

      private String apply(String original) {
        if (original == null) {
          return null;
        }

        StringBuilder builder = new StringBuilder(original.replace('.', '_'));
        for (int i = 1; i < builder.length() - 1; i++) {
          if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
            builder.insert(i++, '_');
          }
        }

        return builder.toString();
      }

      private boolean isUnderscoreRequired(char before, char current, char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
      }
    };
  }
}
