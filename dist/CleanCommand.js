"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.CleanCommand = void 0;
var FileUtils_1 = require("./libs/FileUtils");
var CleanCommand = /** @class */ (function () {
    function CleanCommand(buildDir) {
        this.buildDir = buildDir;
        this.buildDir = buildDir;
    }
    CleanCommand.prototype.clean = function () {
        if (FileUtils_1.FileUtils.exists(this.buildDir)) {
            FileUtils_1.FileUtils.rmdir(this.buildDir);
        }
    };
    return CleanCommand;
}());
exports.CleanCommand = CleanCommand;
