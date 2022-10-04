package dev.appkr.starter.services;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import dev.appkr.starter.model.Bindable;
import dev.appkr.starter.model.GlobalConstants;
import java.io.FileWriter;
import java.io.IOException;

public class TemplateUtils {

  static final MustacheFactory mf = new DefaultMustacheFactory();

  public static void write(String templatePath, String outPath, Bindable bindable) throws IOException {
    final Mustache mustache = mf.compile(templatePath);
    mustache.execute(new FileWriter(outPath), bindable).flush();
  }

  public static String getTemplatePath(String classpath) {
    if (!FileUtils.isRunningInJar()) {
      return String.format("%s/src/main/resources/%s", GlobalConstants.PWD, classpath);
    }

    return classpath;
  }
}
