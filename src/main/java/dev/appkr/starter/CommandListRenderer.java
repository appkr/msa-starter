package dev.appkr.starter;

import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.Column;
import picocli.CommandLine.Help.Column.Overflow;
import picocli.CommandLine.Help.TextTable;
import picocli.CommandLine.IHelpSectionRenderer;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.UsageMessageSpec;

// Reference: https://github.com/remkop/picocli/blob/main/picocli-examples/src/main/java/picocli/examples/customhelp/ShowCommandHierarchy.java
public class CommandListRenderer implements IHelpSectionRenderer {

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

  void addHierarchy(CommandLine cmd, TextTable textTable, String indent) {
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

  String description(UsageMessageSpec usageMessage) {
    if (usageMessage.header().length > 0) {
      return usageMessage.header()[0];
    }

    if (usageMessage.description().length > 0) {
      return usageMessage.description()[0];
    }

    return "";
  }
}
