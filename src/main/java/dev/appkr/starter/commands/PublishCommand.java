package dev.appkr.starter.commands;

import dev.appkr.starter.model.ExitCode;
import picocli.CommandLine.Command;
import picocli.CommandLine.ScopeType;

import java.util.concurrent.Callable;

@Command(
    name = "publish",
    description = "Publish the project to a new directory",
    mixinStandardHelpOptions = true,
    scope = ScopeType.INHERIT
)
public class PublishCommand implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    return ExitCode.SUCCESS;
  }
}
