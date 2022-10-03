package dev.appkr.starter.services;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.StringTemplateSource;
import dev.appkr.starter.model.Bindable;

import java.io.IOException;

public class TemplateRenderer {

  final static Handlebars handlebars = new Handlebars()
      .registerHelper("ifArmArch", (context, options) ->
          (context == "arm") ? options.fn(context) : options.inverse(context))
      .registerHelper("ifJava17", (context, options) ->
          (context == "17") ? options.fn(context) : options.inverse(context))
      .registerHelper("ifJava11", (context, options) ->
          (context == "11") ? options.fn(context) : options.inverse(context))
      .registerHelper("ifJava8", (context, options) ->
          (context == "1.8") ? options.fn(context) : options.inverse(context))
      .registerHelper("ifVroongProject", (context, options) ->
          (context == "v") ? options.fn(context) : options.inverse(context));

  public static String render(String filename, String content, Bindable bindable) throws IOException {
    final StringTemplateSource templateSource = new StringTemplateSource(filename, content);
    final Template template = handlebars.compile(templateSource);

    return template.apply(bindable);
  }
}
