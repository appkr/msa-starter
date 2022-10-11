package dev.appkr.starter.commands;

import com.github.mustachejava.Mustache;
import dev.appkr.starter.MsaStarter;
import dev.appkr.starter.services.CommandUtils;
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

public class GenerateCommandForNative extends GenerateCommand {

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
    final String path = MAIN.getClassLoader().getResource(templateDir).toURI().getPath();

    final URI uri = URI.create("resource:" + path);
    final FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());

    return Files
        .walk(fs.getPath(templateDir))
        .filter(p -> !Files.isDirectory(p));
  }

  @Override
  public void copyFile(Path srcPath, Path destPath) throws IOException {
    Files.copy(srcPath, destPath);
    CommandUtils.success(srcPath + " -> " + destPath);
  }

  @Override
  public void renderTemplate(Path aTemplatePath, String writePath) throws IOException {
    final String readPath = aTemplatePath.toString();
    final Mustache mustache = mf.compile(readPath);
    mustache.execute(new FileWriter(writePath), buildInfo).flush();

    CommandUtils.success(readPath + " -> " + writePath);
  }
}
