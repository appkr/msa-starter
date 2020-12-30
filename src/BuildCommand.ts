import {BuildInfo} from "./model/BuildInfo";
import {ProjectType} from "./model/ProjectType";
import {JavaVersion} from "./model/JavaVersion";
import {ControlUtils} from "./libs/ControlUtils";
import {FileUtils} from "./libs/FileUtils";
import Handlebars from "handlebars";

export class BuildCommand {

  private buildInfo: BuildInfo = {
    projectType: ProjectType.NON_VROONG,
    projectName: 'example',
    groupName: 'dev.appkr',
    packageName: 'dev.appkr.example',
    portNumber: 8080,
    mediaType: 'application/vnd.appkr.private.v1+json',
    javaVersion: JavaVersion.JAVA_8,
    dockerImage: 'openjdk:8-jre-alpine',
    skipTokens: [
      '.DS_Store',
      'jmx-exporter',
      'newrelic',
      'Jenkinsfile',
      'clients/.gitignore',
      'clients/build.gradle',
      'kafka/kafka_server_jaas.conf',
    ]
  };

  constructor(
    private readonly sourceDir:string,
    private readonly targetDir:string
  ) {
    this.sourceDir = sourceDir;
    this.targetDir = targetDir;
    this.registerHandlebarsHelper();
  }

  build(): BuildInfo {
    this.getUserInput();

    console.log("Build a project with ", this.buildInfo);
    ControlUtils.continue();

    FileUtils.files(this.sourceDir).forEach(srcFilename => {
      if (ControlUtils.shouldSkip(srcFilename, this.buildInfo)) {
        console.error(srcFilename + " skipped");
        return;
      }

      const targetFilename: string = this.calculateTargetFileName(srcFilename);
      this.compileAndWriteFile(srcFilename, targetFilename);
      console.log("%s created", targetFilename);
    });

    const gradlewPath = `${this.targetDir}/${this.buildInfo.projectName}/gradlew`;
    FileUtils.chmod(gradlewPath, '0755');

    return this.buildInfo;
  }

  getUserInput(): void {
    const projectType: string = ControlUtils.ask(
      'Non-vroong project(n)? Or vroong project(v) (default:n)? ',
      'n'
    );
    this.buildInfo = (projectType == 'n')
      ? this.buildInfo
      : {
        dockerImage: 'openjdk:8-jre-alpine',
        groupName: 'com.vroong',
        javaVersion: JavaVersion.JAVA_8,
        mediaType: 'application/vnd.vroong.private.v1+json',
        packageName: 'com.vroong.example',
        portNumber: 8080,
        projectName: 'example',
        projectType: ProjectType.VROONG,
        skipTokens: ['.DS_Store'],
      };

    let incorrect = true;
    while (incorrect) {
      const input = ControlUtils.ask('Which Java version do you want to use, 8 or 11? ');
      if (input == 8 || input == 11) {
        this.buildInfo.javaVersion = (input == 8) ? JavaVersion.JAVA_8 : JavaVersion.JAVA_11;
        this.buildInfo.dockerImage = (input == 8) ? 'openjdk:8-jre-alpine' : 'amazoncorretto:11-alpine-jdk';
        incorrect = false;
      }
    }

    this.buildInfo.projectName = ControlUtils.ask(
      'What is the project name (default:{})? ',
      this.buildInfo.projectName
    );

    this.buildInfo.groupName = ControlUtils.ask(
      'What is the group name (default:{})? ',
      this.buildInfo.groupName
    );

    this.buildInfo.portNumber = ControlUtils.ask(
      'What is the web server port (default:{})? ',
      this.buildInfo.portNumber
    );

    this.buildInfo.mediaType = ControlUtils.ask(
      'What is the media type for api request & response (default:{})? ',
      this.buildInfo.mediaType
    );

    this.buildInfo.packageName = `${this.buildInfo.groupName}.${this.buildInfo.projectName}`;
  }

  calculateTargetFileName(srcFilename: string): string {
    let targetFilename: string = srcFilename.replace(this.sourceDir, this.buildInfo.projectName);
    if (srcFilename.indexOf("src/main/java") !== -1) {
      targetFilename = targetFilename
        .replace("src/main/java", `src/main/java/${this.buildInfo.packageName
          .replace(/\./g, "/")}`);
    }
    if (srcFilename.indexOf("src/test/java") !== -1) {
      targetFilename = targetFilename
        .replace("src/test/java", `src/test/java/${this.buildInfo.packageName
            .replace(/\./g, "/")}`);
    }
    targetFilename = `${this.targetDir}/${targetFilename}`;

    return targetFilename;
  }

  compileAndWriteFile(srcFilename: string, targetFilename: string): void {
    FileUtils.mkdir(targetFilename);

    const fileContent = FileUtils.read(srcFilename);
    if (FileUtils.isBinary(srcFilename)) {
      FileUtils.copy(srcFilename, targetFilename);
    } else {
      const template = Handlebars.compile(fileContent);
      FileUtils.write(targetFilename, template(this.buildInfo));
    }
  }

  private registerHandlebarsHelper() {
    Handlebars.registerHelper('ifJava11', (input, options) => {
      if(input == JavaVersion.JAVA_11) {
        return options.fn(this);
      }
      return options.inverse(this);
    });

    Handlebars.registerHelper('ifVroongProject', (input, options) => {
      if(input == ProjectType.VROONG) {
        return options.fn(this);
      }
      return options.inverse(this);
    });
  }

}
