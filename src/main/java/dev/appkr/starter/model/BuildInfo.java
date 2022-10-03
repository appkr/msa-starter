package dev.appkr.starter.model;

import java.util.List;

public class BuildInfo implements Bindable {

  String osArch = System.getProperty("os.arch").equals("aarch64") ? "arm" : "intel";
  String projectType = "n";
  String projectName = "example";
  String groupName = "dev.appkr";
  String packageName = "dev.appkr.example";
  String portNumber = "8080";
  String mediaType = "application/json";
  String javaVersion = "17";
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

  public String getOsArch() {
    return osArch;
  }

  public void setOsArch(String osArch) {
    this.osArch = osArch;
  }

  public String getProjectType() {
    return projectType;
  }

  public void setProjectType(String projectType) {
    this.projectType = projectType;
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

  public String getJavaVersion() {
    return javaVersion;
  }

  public void setJavaVersion(String javaVersion) {
    this.javaVersion = javaVersion;
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

  private BuildInfo() {
  }

  @Override
  public String toString() {
    return "BuildInfo{" +
        "osArch='" + osArch + '\'' +
        ", projectType='" + projectType + '\'' +
        ", projectName='" + projectName + '\'' +
        ", groupName='" + groupName + '\'' +
        ", packageName='" + packageName + '\'' +
        ", portNumber='" + portNumber + '\'' +
        ", mediaType='" + mediaType + '\'' +
        ", javaVersion='" + javaVersion + '\'' +
        ", dockerImage='" + dockerImage + '\'' +
        ", skipTokens=" + skipTokens +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BuildInfo buildInfo = (BuildInfo) o;

    if (osArch != null ? !osArch.equals(buildInfo.osArch) : buildInfo.osArch != null) {
      return false;
    }
    if (projectType != null ? !projectType.equals(buildInfo.projectType) : buildInfo.projectType != null) {
      return false;
    }
    if (projectName != null ? !projectName.equals(buildInfo.projectName) : buildInfo.projectName != null) {
      return false;
    }
    if (groupName != null ? !groupName.equals(buildInfo.groupName) : buildInfo.groupName != null) {
      return false;
    }
    if (packageName != null ? !packageName.equals(buildInfo.packageName) : buildInfo.packageName != null) {
      return false;
    }
    if (portNumber != null ? !portNumber.equals(buildInfo.portNumber) : buildInfo.portNumber != null) {
      return false;
    }
    if (mediaType != null ? !mediaType.equals(buildInfo.mediaType) : buildInfo.mediaType != null) {
      return false;
    }
    if (javaVersion != null ? !javaVersion.equals(buildInfo.javaVersion) : buildInfo.javaVersion != null) {
      return false;
    }
    if (dockerImage != null ? !dockerImage.equals(buildInfo.dockerImage) : buildInfo.dockerImage != null) {
      return false;
    }
    return skipTokens != null ? skipTokens.equals(buildInfo.skipTokens) : buildInfo.skipTokens == null;
  }

  @Override
  public int hashCode() {
    int result = osArch != null ? osArch.hashCode() : 0;
    result = 31 * result + (projectType != null ? projectType.hashCode() : 0);
    result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
    result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
    result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
    result = 31 * result + (portNumber != null ? portNumber.hashCode() : 0);
    result = 31 * result + (mediaType != null ? mediaType.hashCode() : 0);
    result = 31 * result + (javaVersion != null ? javaVersion.hashCode() : 0);
    result = 31 * result + (dockerImage != null ? dockerImage.hashCode() : 0);
    result = 31 * result + (skipTokens != null ? skipTokens.hashCode() : 0);
    return result;
  }
}
