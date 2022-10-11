package dev.appkr.starter.commands;

import com.github.mustachejava.Mustache;
import dev.appkr.starter.services.CommandUtils;
import dev.appkr.starter.services.GlobalConstants;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class GenerateCommandForGradle extends GenerateCommand {

  String templateDir = String.format("%s/%s/%s", GlobalConstants.PWD, RESOURCE_DIR, TEMPLATE_WEBMVC_DIR);

  @Override
  public String getTemplateDir() {
    return templateDir;
  }

  @Override
  public void setTemplateDir(String seedDir) {
    this.templateDir = String.format("%s/%s/%s", GlobalConstants.PWD, RESOURCE_DIR, seedDir);
  }

  @Override
  public Stream<Path> listDir() throws IOException {
    return Files
        .walk(Paths.get(templateDir), FileVisitOption.FOLLOW_LINKS)
        .filter(Files::isRegularFile);
  }

  @Override
  public void copyFile(Path srcPath, Path destPath) throws IOException {
    Files.copy(srcPath, destPath);
    CommandUtils.success(srcPath + " -> " + destPath);
  }

  @Override
  public void renderTemplate(Path aTemplatePath, String writePath) throws IOException {
    final Mustache mustache = mf.compile(aTemplatePath.toString());
    mustache.execute(new FileWriter(writePath), buildInfo).flush();

    CommandUtils.success(aTemplatePath + " -> " + writePath);
  }
}
