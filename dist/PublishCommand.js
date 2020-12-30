"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.PublishCommand = void 0;
var ControlUtils_1 = require("./libs/ControlUtils");
var FileUtils_1 = require("./libs/FileUtils");
var simple_git_1 = __importDefault(require("simple-git"));
var PublishCommand = /** @class */ (function () {
    function PublishCommand() {
        this.publishInfo = {
            buildDir: 'build/example',
            publishDir: '~/example',
        };
    }
    PublishCommand.prototype.publish = function () {
        this.getUserInput();
        console.log('Publishing the project with ', this.publishInfo);
        ControlUtils_1.ControlUtils.continue();
        FileUtils_1.FileUtils.copyFolder(this.publishInfo.buildDir, this.publishInfo.publishDir);
        this.gitCommit('new project created from msa-starter');
        return this.publishInfo;
    };
    PublishCommand.prototype.getUserInput = function () {
        var buildDir = ControlUtils_1.ControlUtils.ask('What is the build artifact path you want to publish(e.g. ./build/example)? ');
        if (!buildDir) {
            throw Error('Invalid directory value: ' + buildDir);
        }
        buildDir = FileUtils_1.FileUtils.resolve(buildDir);
        if (!FileUtils_1.FileUtils.exists(buildDir)) {
            throw Error('Not existing directory value: ' + buildDir);
        }
        var publishDir = ControlUtils_1.ControlUtils.ask('Where do you want to publish the build(e.g. ~/example)? ');
        if (!publishDir) {
            throw Error('Invalid directory value: ' + publishDir);
        }
        publishDir = FileUtils_1.FileUtils.resolve(publishDir);
        if (FileUtils_1.FileUtils.exists(publishDir)) {
            throw Error('Already existing directory value: '
                + publishDir + '; Not-existing directory value required');
        }
        this.publishInfo = {
            buildDir: buildDir,
            publishDir: publishDir,
        };
    };
    PublishCommand.prototype.gitCommit = function (message) {
        var git = simple_git_1.default(this.publishInfo.publishDir, { binary: 'git' });
        git.init()
            .then(function () { return git.add('.'); })
            .then(function () { return git.commit(message); });
    };
    return PublishCommand;
}());
exports.PublishCommand = PublishCommand;
