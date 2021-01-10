"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.BuildCommand = void 0;
var ProjectType_1 = require("./model/ProjectType");
var JavaVersion_1 = require("./model/JavaVersion");
var ControlUtils_1 = require("./libs/ControlUtils");
var FileUtils_1 = require("./libs/FileUtils");
var handlebars_1 = __importDefault(require("handlebars"));
var BuildCommand = /** @class */ (function () {
    function BuildCommand(sourceDir, targetDir, useDefault) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        this.useDefault = useDefault;
        this.buildInfo = {
            projectType: ProjectType_1.ProjectType.NON_VROONG,
            projectName: 'example',
            groupName: 'dev.appkr',
            packageName: 'dev.appkr.example',
            portNumber: 8080,
            mediaType: 'application/vnd.appkr.private.v1+json',
            javaVersion: JavaVersion_1.JavaVersion.JAVA_8,
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
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        this.useDefault = useDefault;
        this.registerHandlebarsHelper();
    }
    BuildCommand.prototype.build = function () {
        var _this = this;
        this.getUserInput();
        if (!this.useDefault) {
            console.log("Build a project with ", this.buildInfo);
            ControlUtils_1.ControlUtils.continue();
        }
        FileUtils_1.FileUtils.files(this.sourceDir).forEach(function (srcFilename) {
            if (ControlUtils_1.ControlUtils.shouldSkip(srcFilename, _this.buildInfo)) {
                console.error(srcFilename + " skipped");
                return;
            }
            var targetFilename = _this.calculateTargetFileName(srcFilename);
            _this.compileAndWriteFile(srcFilename, targetFilename);
            console.log("%s created", targetFilename);
        });
        var gradlewPath = this.targetDir + "/" + this.buildInfo.projectName + "/gradlew";
        FileUtils_1.FileUtils.chmod(gradlewPath, '0755');
        return this.buildInfo;
    };
    BuildCommand.prototype.getUserInput = function () {
        if (this.useDefault) {
            return;
        }
        var projectType = ControlUtils_1.ControlUtils.ask('Non-vroong project(n)? Or vroong project(v) (default:n)? ', 'n');
        this.buildInfo = (projectType == 'n')
            ? this.buildInfo
            : {
                dockerImage: 'openjdk:8-jre-alpine',
                groupName: 'com.vroong',
                javaVersion: JavaVersion_1.JavaVersion.JAVA_8,
                mediaType: 'application/vnd.vroong.private.v1+json',
                packageName: 'com.vroong.example',
                portNumber: 8080,
                projectName: 'example',
                projectType: ProjectType_1.ProjectType.VROONG,
                skipTokens: ['.DS_Store'],
            };
        var incorrect = true;
        while (incorrect) {
            var input = ControlUtils_1.ControlUtils.ask('Which Java version do you want to use, 8 or 11? ');
            if (input == 8 || input == 11) {
                this.buildInfo.javaVersion = (input == 8) ? JavaVersion_1.JavaVersion.JAVA_8 : JavaVersion_1.JavaVersion.JAVA_11;
                this.buildInfo.dockerImage = (input == 8) ? 'openjdk:8-jre-alpine' : 'amazoncorretto:11-alpine-jdk';
                incorrect = false;
            }
        }
        this.buildInfo.projectName = ControlUtils_1.ControlUtils.ask('What is the project name (default:{})? ', this.buildInfo.projectName);
        this.buildInfo.groupName = ControlUtils_1.ControlUtils.ask('What is the group name (default:{})? ', this.buildInfo.groupName);
        this.buildInfo.portNumber = ControlUtils_1.ControlUtils.ask('What is the web server port (default:{})? ', this.buildInfo.portNumber);
        this.buildInfo.mediaType = ControlUtils_1.ControlUtils.ask('What is the media type for api request & response (default:{})? ', this.buildInfo.mediaType);
        this.buildInfo.packageName = this.buildInfo.groupName + "." + this.buildInfo.projectName;
    };
    BuildCommand.prototype.calculateTargetFileName = function (srcFilename) {
        var targetFilename = srcFilename.replace(this.sourceDir, this.buildInfo.projectName);
        if (srcFilename.indexOf("src/main/java") !== -1) {
            targetFilename = targetFilename
                .replace("src/main/java", "src/main/java/" + this.buildInfo.packageName
                .replace(/\./g, "/"));
        }
        if (srcFilename.indexOf("src/test/java") !== -1) {
            targetFilename = targetFilename
                .replace("src/test/java", "src/test/java/" + this.buildInfo.packageName
                .replace(/\./g, "/"));
        }
        targetFilename = this.targetDir + "/" + targetFilename;
        return targetFilename;
    };
    BuildCommand.prototype.compileAndWriteFile = function (srcFilename, targetFilename) {
        FileUtils_1.FileUtils.mkdir(targetFilename);
        var fileContent = FileUtils_1.FileUtils.read(srcFilename);
        if (FileUtils_1.FileUtils.isBinary(srcFilename)) {
            FileUtils_1.FileUtils.copy(srcFilename, targetFilename);
        }
        else {
            var template = handlebars_1.default.compile(fileContent);
            FileUtils_1.FileUtils.write(targetFilename, template(this.buildInfo));
        }
    };
    BuildCommand.prototype.registerHandlebarsHelper = function () {
        var _this = this;
        handlebars_1.default.registerHelper('ifJava11', function (input, options) {
            if (input == JavaVersion_1.JavaVersion.JAVA_11) {
                return options.fn(_this);
            }
            return options.inverse(_this);
        });
        handlebars_1.default.registerHelper('ifVroongProject', function (input, options) {
            if (input == ProjectType_1.ProjectType.VROONG) {
                return options.fn(_this);
            }
            return options.inverse(_this);
        });
    };
    return BuildCommand;
}());
exports.BuildCommand = BuildCommand;
