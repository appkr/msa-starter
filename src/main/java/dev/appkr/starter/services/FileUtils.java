package dev.appkr.starter.services;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardOpenOption.CREATE;

import dev.appkr.starter.MsaStarter;
import dev.appkr.starter.model.GlobalConstants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

  // // Reference: https://mkyong.com/java/java-read-a-file-from-resources-folder/
  final static Class<?> MAIN = MsaStarter.class;

  public static boolean isRunningInJar() {
    try {
      final String fqcn = TemplateUtils.class.getName().replace(".", GlobalConstants.DIR_SEPARATOR);
      final String classJar = TemplateUtils.class.getResource(GlobalConstants.DIR_SEPARATOR + fqcn + ".class").toString();
      return classJar.startsWith("jar:");
    } catch (Exception e) {
      return false;
    }
  }

  public static List<Path> getJarPaths(String path) throws URISyntaxException, IOException {
    final String jarPath = MAIN.getProtectionDomain()
        .getCodeSource()
        .getLocation()
        .toURI()
        .getPath();

    final URI uri = URI.create("jar:file:" + jarPath);
    final FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());

    return Files.walk(fs.getPath(path))
        .toList();
  }

  public static void createDir(String toCreate) throws IOException {
    createDir(Paths.get(toCreate));
  }

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
          try {
            copy(src, dest);
          } catch (IOException e) {
            CommandUtils.fail(dest.toString(), e);
          }
        });
  }

  public static Stream<Path> listDir(String path) throws URISyntaxException, IOException {
    return listDir(Paths.get(path));
  }

  public static Stream<Path> listDir(Path path) throws URISyntaxException, IOException {
    return (FileUtils.isRunningInJar())
        ? FileUtils.getJarPaths(path.toString()).stream()
        : Files.walk(path, FileVisitOption.FOLLOW_LINKS);
  }

  public static void copy(String from, String into) throws IOException {
    copy(Paths.get(from), Paths.get(into));
  }

  public static void copy(Path from, Path into) throws IOException {
    try {
      // Try first: e.g. when from={PWD}/src/main/resources/templates/gradle.properties
      Files.copy(from, into, COPY_ATTRIBUTES);
    } catch (NoSuchFileException e) {
      // Try again: e.g. when from=templates/gradle.properties
      Files.copy(getFileContent(from.toString()), into);
    }

    CommandUtils.success("copy: " + from + " -> " + into);
  }

  private static InputStream getFileContent(String filename) throws NoSuchFileException {
    final ClassLoader classLoader = MAIN.getClassLoader();
    final InputStream inputStream = classLoader.getResourceAsStream(filename);
    if (inputStream == null) {
      throw new NoSuchFileException(filename);
    }

    return inputStream;
  }

  public static String read(String path) throws IOException {
    return read(Paths.get(path));
  }

  public static String read(Path path) throws IOException {
    return Files.readString(path);
  }

  public static void write(String path, String content) throws IOException {
    write(Paths.get(path), content);
  }

  public static void write(Path path, String content) throws IOException {
    Files.write(path, content.getBytes(StandardCharsets.UTF_8), CREATE);
  }

  public static void makeExecutable(String path) {
    new File(path).setExecutable(true, false);
  }

  public static void makeExecutable(Path path) {
    path.toFile().setExecutable(true, false);
  }

  public static boolean isBinary(String path) throws IOException {
    return isBinary(Paths.get(path));
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
