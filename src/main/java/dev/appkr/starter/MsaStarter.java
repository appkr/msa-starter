package dev.appkr.starter;

import dev.appkr.starter.commands.GenerateCommand;
import dev.appkr.starter.commands.PublishCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.Ansi.Style;
import picocli.CommandLine.Help.ColorScheme;
import picocli.CommandLine.Help.Column;
import picocli.CommandLine.Help.Column.Overflow;
import picocli.CommandLine.Help.TextTable;
import picocli.CommandLine.IHelpSectionRenderer;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.UsageMessageSpec;

import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST;

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
        .addSubcommand(new GenerateCommand())
        .addSubcommand(new PublishCommand())
        .setColorScheme(colorScheme);

    if (args.length == 0) {
      cli.getHelpSectionMap().put(SECTION_KEY_COMMAND_LIST, new MyCommandListRenderer());
      cli.usage(System.out);
    } else {
      final int exitCode = cli.execute(args);
      System.exit(exitCode);
    }
  }

  // Reference: https://github.com/remkop/picocli/blob/main/picocli-examples/src/main/java/picocli/examples/customhelp/ShowCommandHierarchy.java
  static class MyCommandListRenderer implements IHelpSectionRenderer {
    @Override
    public String render(Help help) {
      CommandSpec spec = help.commandSpec();
      if (spec.subcommands().isEmpty()) {
        return "";
      }

      // prepare layout: two columns
      // the left column overflows, the right column wraps if text is too long
      TextTable textTable = TextTable.forColumns(help.colorScheme(),
          new Column(15, 2, Overflow.SPAN),
          new Column(spec.usageMessage().width() - 15, 2, Overflow.WRAP));
      textTable.setAdjustLineBreaksForWideCJKCharacters(spec.usageMessage().adjustLineBreaksForWideCJKCharacters());

      for (CommandLine subcommand : spec.subcommands().values()) {
        addHierarchy(subcommand, textTable, "");
      }

      return textTable.toString();
    }

    private void addHierarchy(CommandLine cmd, TextTable textTable, String indent) {
      // create comma-separated list of command name and aliases
      String names = cmd.getCommandSpec().names().toString();
      // remove leading '[' and trailing ']'
      names = names.substring(1, names.length() - 1);

      // command description is taken from header or description
      final String description = description(cmd.getCommandSpec().usageMessage());

      // add a line for this command to the layout
      textTable.addRowValues(indent + names, description);

      // add its subcommands (if any)
      for (CommandLine sub : cmd.getSubcommands().values()) {
        addHierarchy(sub, textTable, indent + "  ");
      }
    }

    private String description(UsageMessageSpec usageMessage) {
      if (usageMessage.header().length > 0) {
        return usageMessage.header()[0];
      }

      if (usageMessage.description().length > 0) {
        return usageMessage.description()[0];
      }

      return "";
    }
  }
}
