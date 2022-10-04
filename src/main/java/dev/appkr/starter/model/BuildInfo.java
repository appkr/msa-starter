package dev.appkr.starter.model;

import java.util.List;

public class BuildInfo implements Bindable {

  boolean isArm = System.getProperty("os.arch").equals("aarch64");
  boolean isReactiveProject = false;
  boolean isVroongProject = false;
  String javaVersion = "17";
  String projectName = "example";
  String groupName = "dev.appkr";
  String packageName = "dev.appkr.example";
  String portNumber = "8080";
  String mediaType = "application/json";
  String dockerImage = "amazoncorretto:17-alpine-jdk";
  List<String> skipTokens = List.of(
      ".DS_Store",
      "jmx-exporter",
      "newrelic",
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
