package dev.appkr.starter.model;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildInfo {

  static final List<String> EXAMPLE_FILES = new ArrayList<>();
  static {
    // src/main/resources/templates/webflux
    EXAMPLE_FILES.add("AlbumMapper.java");
    EXAMPLE_FILES.add("SongMapper.java");
    EXAMPLE_FILES.add("AlbumApiDelegateImpl.java");
    EXAMPLE_FILES.add("SongApiDelegateImpl.java");
    EXAMPLE_FILES.add("SongService.java");
    EXAMPLE_FILES.add("AlbumRepository.java");
    EXAMPLE_FILES.add("AlbumRepositoryCustom.java");
    EXAMPLE_FILES.add("AlbumRepositoryImpl.java");
    EXAMPLE_FILES.add("AlbumRowMapper.java");
    EXAMPLE_FILES.add("AlbumSqlHelper.java");
    EXAMPLE_FILES.add("SongRepository.java");
    EXAMPLE_FILES.add("SongRepositoryCustom.java");
    EXAMPLE_FILES.add("SongRepositoryImpl.java");
    EXAMPLE_FILES.add("SongRowMapper.java");
    EXAMPLE_FILES.add("SongSqlHelper.java");
    EXAMPLE_FILES.add("Album.java");
    EXAMPLE_FILES.add("Song.java");
    EXAMPLE_FILES.add("SongApiTest.java");
    EXAMPLE_FILES.add("SongRepositoryTest.java");
    EXAMPLE_FILES.add("SongServiceTest.java");
    EXAMPLE_FILES.add("Fixtures.java");

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

  public static final String JAVA_VERSION_8 = "8";
  public static final String JAVA_VERSION_11 = "11";
  public static final String JAVA_VERSION_17 = "17";
  public static final String PROJECT_NAME_DEFAULT = "example";
  public static final String GROUP_NAME_DEFAULT = "dev.appkr";
  public static final String GROUP_NAME_VROONG = "com.vroong";
  public static final String PACKAGE_NAME_DEFAULT = "dev.appkr.example";
  public static final String PORT_NUMBER_DEFAULT = "8080";
  public static final String MEDIA_TYPE_DEFAULT = "application/json";
  public static final String MEDIA_TYPE_VROONG = "application/vnd.vroong.private.v1+json";
  public static final String DOCKER_IMAGE_JAVA8 = "openjdk:8-jre-alpine";
  public static final String DOCKER_IMAGE_JAVA11 = "amazoncorretto:11-alpine-jdk";
  public static final String DOCKER_IMAGE_JAVA17 = "amazoncorretto:17-alpine-jdk";

  boolean isArm = System.getProperty("os.arch").equals("aarch64");
  boolean isReactiveProject = false;
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

  public boolean isArm() {
    return isArm;
  }

  public void setArm(boolean arm) {
    isArm = arm;
  }

  public boolean isReactiveProject() {
    return isReactiveProject;
  }

  public void setReactiveProject(boolean reactiveProject) {
    isReactiveProject = reactiveProject;
  }

  public boolean isVroongProject() {
    return isVroongProject;
  }

  public void setVroongProject(boolean vroongProject) {
    isVroongProject = vroongProject;
  }

  public boolean isIncludeExample() {
    return includeExample;
  }

  public void setIncludeExample(boolean includeExample) {
    this.includeExample = includeExample;
  }

  public String getJavaVersion() {
    return javaVersion;
  }

  public void setJavaVersion(String javaVersion) {
    this.javaVersion = javaVersion;
  }

  public boolean isJava17() {
    return javaVersion.equals("17");
  }

  public boolean isJava11() {
    return javaVersion.equals("11");
  }

  public boolean isJava8() {
    return javaVersion.equals("1.8");
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

  public String getDockerImage() {
    return dockerImage;
  }

  public void setDockerImage(String dockerImage) {
    this.dockerImage = dockerImage;
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
