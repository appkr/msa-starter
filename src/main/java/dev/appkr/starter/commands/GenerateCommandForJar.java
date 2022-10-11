package dev.appkr.starter.commands;

import com.github.mustachejava.Mustache;
import dev.appkr.starter.MsaStarter;
import dev.appkr.starter.services.CommandUtils;
import dev.appkr.starter.services.GlobalConstants;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

public class GenerateCommandForJar extends GenerateCommand {

  final static Class<?> MAIN = MsaStarter.class;

  String templateDir = TEMPLATE_WEBMVC_DIR;

  @Override
  public String getTemplateDir() {
    return templateDir;
  }

  @Override
  public void setTemplateDir(String seedDir) {
    this.templateDir = seedDir;
  }

  @Override
  public Stream<Path> listDir() throws IOException, URISyntaxException {
    // Reference: https://stackoverflow.com/a/320595
    final String path = MAIN.getProtectionDomain()
        .getCodeSource()
        .getLocation()
        .toURI()
        .getPath();

    final URI uri = URI.create("jar:file:" + path);
    final FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());

    return Files
        .walk(fs.getPath(templateDir))
        .filter(p -> !Files.isDirectory(p));
  }

  @Override
  public void copyFile(Path srcPath, Path destPath) throws IOException {
    Files.copy(srcPath, destPath);
    CommandUtils.success(normalizePath(srcPath.toString()) + " -> " + destPath);
  }

  @Override
  public void renderTemplate(Path aTemplatePath, String writePath) throws IOException {
    String readPath = normalizePath(aTemplatePath.toString());
    writePath = normalizePath(writePath);

    final Mustache mustache = mf.compile(readPath);
    mustache.execute(new FileWriter(writePath), buildInfo).flush();

    CommandUtils.success(readPath + " -> " + writePath);
  }

  private String normalizePath(String path) {
    if (isRunningInJava8()) {
      // NOTE. aTemplatePath differs in Java8 and Java11
      // Java 11: templates/webmvc/src/main/java/domain/Example.java
      // Java 8 : /templates/webmvc/src/main/java/domain/Example.java
      return path.substring(GlobalConstants.DIR_SEPARATOR.length());
    }

    return path;
  }

  private static boolean isRunningInJava8() {
    return getJavaVersion() == 8;
  }

  private static int getJavaVersion() {
    final String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      return Integer.parseInt(version.substring(2, 3));
    }

    int dot = version.indexOf(".");
    if (dot != -1) {
      return Integer.parseInt(version.substring(0, dot));
    }

    return -1;
  }
}
