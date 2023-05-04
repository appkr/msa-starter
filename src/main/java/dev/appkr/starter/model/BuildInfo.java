package dev.appkr.starter.model;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildInfo {

  static final List<String> EXAMPLE_FILES = new ArrayList<>();
  static {
    // src/main/resources/templates/webmvc
    EXAMPLE_FILES.add("ExampleMapper.java");
    EXAMPLE_FILES.add("ExampleApiDelegateImpl.java");
    EXAMPLE_FILES.add("Oauth2TestController.java");
    EXAMPLE_FILES.add("ExampleRepository.java");
    EXAMPLE_FILES.add("ExampleRepositoryCustom.java");
    EXAMPLE_FILES.add("ExampleRepositoryImpl.java");
    EXAMPLE_FILES.add("ExampleService.java");
    EXAMPLE_FILES.add("Example.java");
    EXAMPLE_FILES.add("data.sql");
    EXAMPLE_FILES.add("ExampleMapperTest.java");
    EXAMPLE_FILES.add("ExampleApiDelegateImplTest.java");
    EXAMPLE_FILES.add("ExampleRepositoryTest.java");
  }

  public static final String JAVA_VERSION_17 = "17";
  public static final String PROJECT_NAME_DEFAULT = "example";
  public static final String GROUP_NAME_DEFAULT = "dev.appkr";
  public static final String GROUP_NAME_VROONG = "com.vroong";
  public static final String PACKAGE_NAME_DEFAULT = "dev.appkr.example";
  public static final String PORT_NUMBER_DEFAULT = "8080";
  public static final String MEDIA_TYPE_DEFAULT = "application/json";
  public static final String MEDIA_TYPE_VROONG = "application/vnd.vroong.private.v1+json";
  public static final String DOCKER_IMAGE_JAVA17 = "amazoncorretto:17-alpine-jdk";

  boolean isArm = System.getProperty("os.arch").equals("aarch64");
  boolean isVroongProject = false;
  boolean includeExample = true;
  String javaVersion = JAVA_VERSION_17;
  String projectName = PROJECT_NAME_DEFAULT;
  String groupName = GROUP_NAME_DEFAULT;
  String packageName = PACKAGE_NAME_DEFAULT;
  String portNumber = PORT_NUMBER_DEFAULT;
  String mediaType = MEDIA_TYPE_DEFAULT;
  String dockerImage = DOCKER_IMAGE_JAVA17;
  List<String> skipTokens = asList(
      ".DS_Store",
      "jmx-exporter",
      "Jenkinsfile",
      "clients/.gitignore",
      "clients/build.gradle",
      "kafka/kafka_server_jaas.conf"
  );

  public static BuildInfo getInstance() {
    return new BuildInfo();
  }

  public void setVroongProject(boolean vroongProject) {
    isVroongProject = vroongProject;
  }

  public void setIncludeExample(boolean includeExample) {
    this.includeExample = includeExample;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getPortNumber() {
    return portNumber;
  }

  public void setPortNumber(String portNumber) {
    this.portNumber = portNumber;
  }

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  public List<String> getSkipTokens() {
    return skipTokens;
  }

  public void setSkipTokens(List<String> skipTokens) {
    this.skipTokens = skipTokens;
  }

  public void mergeSkipTokensWithExamples() {
    this.skipTokens = Stream.of(skipTokens, EXAMPLE_FILES)
        .flatMap(x -> x.stream())
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "BuildInfo{" +
        "isArm=" + isArm +
        ", isVroongProject=" + isVroongProject +
        ", javaVersion='" + javaVersion + '\'' +
        ", projectName='" + projectName + '\'' +
        ", groupName='" + groupName + '\'' +
        ", packageName='" + packageName + '\'' +
        ", portNumber='" + portNumber + '\'' +
        ", mediaType='" + mediaType + '\'' +
        ", dockerImage='" + dockerImage + '\'' +
        ", skipTokens=" + skipTokens +
        '}';
  }

  private BuildInfo() {
  }
}
