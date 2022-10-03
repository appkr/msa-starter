package dev.appkr.starter.model;

import lombok.Data;

import java.util.List;

@Data
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

  private BuildInfo() {
  }
}
