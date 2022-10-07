package dev.appkr.starter;

import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST;

import dev.appkr.starter.commands.GenerateCommand;
import dev.appkr.starter.commands.GenerateCommandForGradle;
import dev.appkr.starter.commands.GenerateCommandForJar;
import dev.appkr.starter.commands.PublishCommand;
import dev.appkr.starter.services.GlobalConstants;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi.Style;
import picocli.CommandLine.Help.ColorScheme;

@Command(
    name = "msa-starter",
    version = "3.0.0",
    mixinStandardHelpOptions = true,
    description = "Command that generates a Spring-boot microservice skeleton",
    footer = "Developed by appkr<juwonkim@me.com>"
)
public class MsaStarter {

  final static ColorScheme colorScheme = new ColorScheme.Builder()
      .commands(Style.bold)       // combine multiple styles
      .options(Style.fg_yellow)   // yellow foreground color
      .parameters(Style.fg_yellow)
      .optionParams(Style.italic)
      .errors(Style.fg_red, Style.bold)
      .stackTraces(Style.italic)
      .applySystemProperties()
      .build();

  public static void main(String[] args) {
    final MsaStarter command = new MsaStarter();
    final CommandLine cli = new CommandLine(command)
        .addSubcommand(createGenerateCommand())
        .addSubcommand(new PublishCommand())
        .setColorScheme(colorScheme);

    if (args.length == 0) {
      cli.getHelpSectionMap().put(SECTION_KEY_COMMAND_LIST, new CommandListRenderer());
      cli.usage(System.out);
    } else {
      final int exitCode = cli.execute(args);
      System.exit(exitCode);
    }
  }

  static GenerateCommand createGenerateCommand() {
    return isRunningInJar()
        ? new GenerateCommandForJar()
        : new GenerateCommandForGradle();
  }

  static boolean isRunningInJar() {
    // Reference: https://mkyong.com/java/java-read-a-file-from-resources-folder/
    try {
      final String fqcn = MsaStarter.class.getName().replace(".", GlobalConstants.DIR_SEPARATOR);
      final String classJar = MsaStarter.class.getResource(GlobalConstants.DIR_SEPARATOR + fqcn + ".class").toString();
      return classJar.startsWith("jar:");
    } catch (Exception e) {
      return false;
    }
  }
}
