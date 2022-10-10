package dev.appkr.starter.commands;

import dev.appkr.starter.model.ExitCode;
import dev.appkr.starter.services.CommandUtils;
import dev.appkr.starter.services.FileUtils;
import dev.appkr.starter.services.GitUtils;
import dev.appkr.starter.services.GlobalConstants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

@Command(
    name = "publish",
    description = "Publish the project to a new directory",
    mixinStandardHelpOptions = true,
    scope = ScopeType.INHERIT,
    optionListHeading = "%nOptions:%n",
    footerHeading = "%n",
    footer = "appkr<juwonkim@me.com>"
)
public class PublishCommand implements Callable<Integer> {

  final String sourceDir = String.format("%s/.msa-starter", GlobalConstants.USER_HOME);
  String targetDir = sourceDir;

  @Option(names = {"--useDefault"}, description = "Publish a project with all default values")
  boolean useDefault = false;

  @Override
  public Integer call() throws Exception {
    if (!Files.exists(Paths.get(sourceDir))) {
      CommandUtils.fail("'generate' first !!!", new RuntimeException("Directory empty: " + sourceDir));
      return ExitCode.SUCCESS;
    }

    try {
      if (!useDefault) {
        getTargetDir();
        CommandUtils.confirm("Proceed ('Enter' to continue OR 'n' to quit)?", "targetDir: " + targetDir);

        FileUtils.copyDir(sourceDir, targetDir);
      }

      GitUtils.init(targetDir, "new project created from msa-starter");
    } catch (Exception e) {
      CommandUtils.fail("Publishing failed", e);
      return ExitCode.FAILURE;
    }

    return ExitCode.SUCCESS;
  }

  private void getTargetDir() throws IOException {
    String answer = "";
    boolean incorrect = true;
    while (incorrect) {
      answer = CommandUtils.ask("What is the dir you want to publish?", "");
      if (answer == null || answer.length() == 0) {
        CommandUtils.warn("Please provide a dir!");
        continue;
      }

      if (answer.startsWith("~")) {
        answer = answer.replace("~", GlobalConstants.USER_HOME);
      }
      if (answer.startsWith(".")) {
        answer = answer.replace(".", GlobalConstants.PWD);
      }

      if (Files.exists(Paths.get(answer))) {
        CommandUtils.warn("The dir exists!");
        continue;
      }

      incorrect = false;
    }

    this.targetDir = answer;
  }
}
