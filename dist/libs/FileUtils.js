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
var os_1 = require("os");
var isBinaryFileSync = require("isbinaryfile").isBinaryFileSync;
var FileUtils = /** @class */ (function () {
    function FileUtils() {
    }
    FileUtils.files = function (dirname) {
        return fs_1.readdirSync(dirname).reduce(function (files, file) {
            var name = path_1.join(dirname, file);
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
    FileUtils.copyFolder = function (srcDir, destDir) {
        var _this = this;
        try {
            fs_1.mkdirSync(destDir, { recursive: true });
        }
        catch (e) { }
        fs_1.readdirSync(srcDir).forEach(function (element) {
            var stat = fs_1.lstatSync(path_1.join(srcDir, element));
            if (stat.isFile()) {
                fs_1.copyFileSync(path_1.join(srcDir, element), path_1.join(destDir, element));
            }
            else if (stat.isSymbolicLink()) {
                fs_1.symlinkSync(fs_1.readlinkSync(path_1.join(srcDir, element)), path_1.join(destDir, element));
            }
            else if (stat.isDirectory()) {
                _this.copyFolder(path_1.join(srcDir, element), path_1.join(destDir, element));
            }
        });
    };
    FileUtils.write = function (filePath, content) {
        fs_1.writeFileSync(filePath, content);
    };
    FileUtils.chmod = function (filePath, mode) {
        fs_1.chmodSync(filePath, mode);
    };
    FileUtils.resolve = function (relativePath) {
        return path_1.resolve(relativePath.replace('~', os_1.homedir()));
    };
    FileUtils.exists = function (filePath) {
        return fs_1.existsSync(filePath);
    };
    FileUtils.rmdir = function (dirname) {
        fs_1.rmdirSync(dirname, { recursive: true });
    };
    return FileUtils;
}());
exports.FileUtils = FileUtils;
