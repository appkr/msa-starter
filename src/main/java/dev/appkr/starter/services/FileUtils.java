package dev.appkr.starter.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class FileUtils {

  public static void createDir(Path toCreate) throws IOException {
    if (!Files.exists(toCreate)) {
      Files.createDirectories(toCreate);
      CommandUtils.success("createDir: " + toCreate);
    }
  }

  public static void resetDir(String toReset) throws IOException {
    resetDir(Paths.get(toReset));
  }

  public static void resetDir(Path toReset) throws IOException {
    if (Files.exists(toReset)) {
      Files.walk(toReset)
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }

    Files.createDirectories(toReset);

    CommandUtils.success("resetDir: " + toReset);
  }

  public static void copyDir(String from, String into) throws IOException {
    copyDir(Paths.get(from), Paths.get(into));
  }

  public static void copyDir(Path from, Path into) throws IOException {
    createDir(into);

    Files.walk(from)
        .forEach(src -> {
          final Path dest = Paths.get(into.toString(), src.toString().substring(from.toString().length()));
          if (!Files.exists(dest)) {
            try {
              Files.copy(src, dest);
              CommandUtils.success(src + " -> " + dest);
            } catch (IOException e) {
              CommandUtils.fail(dest.toString(), e);
            }
          }
        });
  }

  public static void makeExecutable(String path) {
    new File(path).setExecutable(true, false);
  }

  public static boolean isBinary(Path path) throws IOException {
    // Reference: http://guruble.com/how-to-check-binary-or-ascii-from-java-inputstream/
    final byte[] bytes = Files.readAllBytes(path);
    int len = 1024, count = 0;
    for (byte b : bytes) {
      if (b == 0 && count < len - 1) {
        return true;
      }
      count++;
    }

    return false;
  }
}
