package dev.appkr.starter.services;

import dev.appkr.starter.model.ExitCode;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandUtils {

  public static void success(String message) {
    System.out.println(String.format(
        CommandLine.Help.Ansi.AUTO.string("@|bold,fg(green) ✔ %s|@"),
        message));
  }

  public static void fail(String message, Exception e) {
    System.out.println(String.format(
        CommandLine.Help.Ansi.AUTO.string("@|bold,fg(red) ✘ %s: %s|@"),
        message, e.getMessage()));
  }

  public static String ask(String question, String defaultValue) throws IOException {
    System.out.printf(CommandLine.Help.Ansi.AUTO.string("@|fg(green) %s|@"),
        question.replaceAll("\\{\\}", defaultValue));

    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    final String answer = reader.readLine();

    return (answer == null || answer.isBlank()) ? defaultValue : answer;
  }

  public static void confirm(String question, String toConfirm) throws IOException {
    System.out.printf(
        CommandLine.Help.Ansi.AUTO.string("@|bold,fg(red) %s%n|@%s%n"),
        question, toConfirm);

    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    final String answer = reader.readLine();

    if (answer != null && answer.equalsIgnoreCase("n")) {
      System.exit(ExitCode.SUCCESS);
    }
  }
}
