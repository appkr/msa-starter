package dev.appkr.starter.commands;

import static java.util.Arrays.asList;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import dev.appkr.starter.model.BuildInfo;
import dev.appkr.starter.model.ExitCode;
import dev.appkr.starter.services.CommandUtils;
import dev.appkr.starter.services.FileUtils;
import dev.appkr.starter.services.GlobalConstants;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

@Command(
    name = "generate",
    description = "Create a new project",
    mixinStandardHelpOptions = true,
    scope = ScopeType.INHERIT,
    optionListHeading = "%nOptions:%n",
    footerHeading = "%n",
    footer = "appkr<juwonkim@me.com>"
)
public abstract class GenerateCommand implements Callable<Integer> {

  static final String TEMPLATE_WEBMVC_DIR = "templates/webmvc";
  static final String TEMPLATE_WEBFLUX_DIR = "templates/webflux";
  static final String MAIN_MODULE_DIR = "src/main/java";
  static final String TEST_MODULE_DIR = "src/test/java";
  static final String RESOURCE_DIR = "src/main/resources";
  static final String BINARY_FILE_SUFFIX = ".binary";

  static final MustacheFactory mf = new DefaultMustacheFactory();

  final String targetDir = String.format("%s/.msa-starter", GlobalConstants.USER_HOME);
  final BuildInfo buildInfo = BuildInfo.getInstance();

  @Option(names = {"--useDefault"}, description = "Create a project with all default values")
  boolean useDefault = false;

  /**
   * Get template directory
   *
   * @return string value of the template dir
   * number of cases: 2^3 = 8
   *  - when running in jar(relative path) or gradle(absolute path)
   *  - when running with java 8 or higher (should be normalized)
   *  - webmvc or webflux
   */
  public abstract String getTemplateDir();

  public abstract void setTemplateDir(String seedDir);

  /**
   * Get the string value list of path recursively
   *
   * @return number cases are the same as getSourceDir
   * @throws IOException
   */
  public abstract Stream<Path> listDir() throws IOException, URISyntaxException;

  public abstract void copyFile(Path srcPath, Path destPath) throws IOException;

  /**
   * Read and compile the given template and write the compiled content to outPath
   *
   * @param aTemplatePath
   * @param writePath
   * @throws IOException
   */
  public abstract void renderTemplate(Path aTemplatePath, String writePath) throws IOException;

  @Override
  public Integer call() throws Exception {
    // delete the targetDir if it exists and recreates the targetDir
    FileUtils.resetDir(targetDir);

    if (!useDefault) {
      // get project attributes from a user
      getBuildInfo();
      CommandUtils.confirm("'Enter' to continue OR 'n' to quit)?", buildInfo.toString());
    }

    // bind BuildInfo to the templates and create files
    listDir()
        .filter(aTemplate -> !shouldSkip(aTemplate.toString()))
        .forEach(aTemplate -> {
          final String targetFilename = calculateTargetFilenameFrom(aTemplate.toString());
          try {
            FileUtils.createDir(Paths.get(targetFilename).getParent());

            if (FileUtils.isBinary(aTemplate)) {
              copyFile(aTemplate, Paths.get(targetFilename));
            } else {
              renderTemplate(aTemplate, targetFilename);
            }
          } catch (IOException e) {
            CommandUtils.fail(aTemplate + " -> " + targetFilename, e);
          }
        });

    // make gradlew be executable
    FileUtils.makeExecutable(String.format("%s/%s", targetDir, "gradlew"));

    return ExitCode.SUCCESS;
  }

  protected void getBuildInfo() throws IOException {
    final String templates = CommandUtils.ask("A WebMVC/JPA project(m)? Or a WebFlux/R2DBC project(f) (default: {})?",
        "m");
    if (templates.equalsIgnoreCase("f")) {
      buildInfo.setReactiveProject(true);
      setTemplateDir(TEMPLATE_WEBFLUX_DIR);
    }

    final String isVroongProject = CommandUtils.ask("Is vroong project(y/n, default: {})?", "n");
    if (isVroongProject.equalsIgnoreCase("y")) {
      buildInfo.setVroongProject(true);
      buildInfo.setGroupName(BuildInfo.GROUP_NAME_VROONG);
      buildInfo.setMediaType(BuildInfo.MEDIA_TYPE_VROONG);
      buildInfo.setSkipTokens(asList(".DS_Store"));
    }

    boolean incorrect = true;
    while (incorrect) {
      final String javaVersion = CommandUtils.ask("Which java version will you choose(1.8/11/17, default: {})?",
          buildInfo.getJavaVersion());
      switch (javaVersion) {
        case BuildInfo.JAVA_VERSION_8:
          buildInfo.setJavaVersion(BuildInfo.JAVA_VERSION_8);
          buildInfo.setDockerImage(BuildInfo.DOCKER_IMAGE_JAVA8);
          incorrect = false;
          break;
        case BuildInfo.JAVA_VERSION_11:
          buildInfo.setJavaVersion(BuildInfo.JAVA_VERSION_11);
          buildInfo.setDockerImage(BuildInfo.DOCKER_IMAGE_JAVA11);
          incorrect = false;
          break;
        case BuildInfo.JAVA_VERSION_17:
          buildInfo.setJavaVersion(BuildInfo.JAVA_VERSION_17);
          buildInfo.setDockerImage(BuildInfo.DOCKER_IMAGE_JAVA17);
          incorrect = false;
          break;
        default:
          CommandUtils.warn("Must be one of '1.8', '11', or '17'!");
      }
    }

    buildInfo.setProjectName(CommandUtils.ask("What is the project name(default: {})?", buildInfo.getProjectName()));
    buildInfo.setGroupName(CommandUtils.ask("What is the group name(default: {})?", buildInfo.getGroupName()));
    buildInfo.setPortNumber(CommandUtils.ask("What is the web server port(default: {})?", buildInfo.getPortNumber()));
    buildInfo.setMediaType(
        CommandUtils.ask("What is the media type for request and response(default: {})?", buildInfo.getMediaType()));
    buildInfo.setPackageName(buildInfo.getGroupName() + "." + buildInfo.getProjectName());

    final String includeExample = CommandUtils.ask("Include example codes(y/n, default: {})?", "y");
    if (includeExample.equalsIgnoreCase("n")) {
      buildInfo.setIncludeExample(false);
      buildInfo.mergeSkipTokensWithExamples();
    }
  }

  protected boolean shouldSkip(String path) {
    boolean skip = false;
    for (String token : buildInfo.getSkipTokens()) {
      if (path.indexOf(token) != -1) {
        skip = true;
        break;
      }
    }

    return skip;
  }

  protected String calculateTargetFilenameFrom(String aTemplatePath) {
    String targetFilename = aTemplatePath.replace(getTemplateDir(), targetDir);

    // Calculate destination path
    //   - {sourceDir}/clients/build.gradle -> {targetDir}/clients/build.gradle
    //   - {sourceDir}/src/main/java/Application.java -> {targetDir}/src/main/java/dev/appkr/example/Application.java
    if (targetFilename.contains(MAIN_MODULE_DIR)) {
      final String replacement = String.format("%s/%s",
          MAIN_MODULE_DIR, buildInfo.getPackageName().replace(".", GlobalConstants.DIR_SEPARATOR));
      targetFilename = targetFilename.replace(MAIN_MODULE_DIR, replacement);
    }
    if (targetFilename.contains(TEST_MODULE_DIR)) {
      final String replacement = String.format("%s/%s",
          TEST_MODULE_DIR, buildInfo.getPackageName().replace(".", GlobalConstants.DIR_SEPARATOR));
      targetFilename = targetFilename.replace(TEST_MODULE_DIR, replacement);
    }

    // NOTE. Handling '.jar' file in templates
    // https://imperceptiblethoughts.com/shadow/configuration/dependencies/#embedding-jar-files-inside-your-shadow-jar
    // Because of the way that Gradle handles dependency configuration, from a plugin perspective,
    // shadow is unable to distinguish between a jar file configured as a dependency and a jar file included in the resource folder.
    // This means that any jar found in a resource directory will be merged into the shadow jar the same as any other dependency.
    // If your intention is to embed the jar inside, you must rename the jar as to not end with .jar before the shadow task begins.
    if (targetFilename.endsWith(BINARY_FILE_SUFFIX)) {
      targetFilename = targetFilename.substring(0, targetFilename.length() - BINARY_FILE_SUFFIX.length());
    }

    return targetFilename;
  }
}
