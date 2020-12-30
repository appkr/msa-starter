"use strict";
var __spreadArrays = (this && this.__spreadArrays) || function () {
    for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
    for (var r = Array(s), k = 0, i = 0; i < il; i++)
        for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
            r[k] = a[j];
    return r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.FileUtils = void 0;
var fs_1 = require("fs");
var path_1 = require("path");
var isBinaryFileSync = require("isbinaryfile").isBinaryFileSync;
var FileUtils = /** @class */ (function () {
    function FileUtils() {
    }
    FileUtils.files = function (dir) {
        return fs_1.readdirSync(dir).reduce(function (files, file) {
            var name = path_1.join(dir, file);
            var isDirectory = fs_1.statSync(name).isDirectory();
            return isDirectory ? __spreadArrays(files, FileUtils.files(name.toString())) : __spreadArrays(files, [name.toString()]);
        }, []);
    };
    FileUtils.isBinary = function (filePath) {
        return isBinaryFileSync(filePath, fs_1.lstatSync(filePath).size);
    };
    FileUtils.read = function (filePath) {
        return fs_1.readFileSync(filePath, 'utf8');
    };
    FileUtils.mkdir = function (dirOrFile) {
        var targetDirname = path_1.dirname(dirOrFile);
        if (!fs_1.existsSync(targetDirname)) {
            fs_1.mkdirSync(targetDirname, { recursive: true });
        }
    };
    FileUtils.copy = function (src, dest) {
        fs_1.copyFileSync(src, dest);
    };
    FileUtils.write = function (filePath, content) {
        fs_1.writeFileSync(filePath, content);
    };
    FileUtils.chmod = function (filePath, mode) {
        fs_1.chmodSync(filePath, mode);
    };
    return FileUtils;
}());
exports.FileUtils = FileUtils;
