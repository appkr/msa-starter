"use strict";
var __spreadArray = (this && this.__spreadArray) || function (to, from, pack) {
    if (pack || arguments.length === 2) for (var i = 0, l = from.length, ar; i < l; i++) {
        if (ar || !(i in from)) {
            if (!ar) ar = Array.prototype.slice.call(from, 0, i);
            ar[i] = from[i];
        }
    }
    return to.concat(ar || Array.prototype.slice.call(from));
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.FileUtils = void 0;
var fs_1 = require("fs");
var path_1 = require("path");
var os_1 = require("os");
var isBinaryFileSync = require('isbinaryfile').isBinaryFileSync;
var FileUtils = /** @class */ (function () {
    function FileUtils() {
    }
    FileUtils.files = function (dirname) {
        return (0, fs_1.readdirSync)(dirname).reduce(function (files, file) {
            var name = (0, path_1.join)(dirname, file);
            var isDirectory = (0, fs_1.statSync)(name).isDirectory();
            return isDirectory ? __spreadArray(__spreadArray([], files, true), FileUtils.files(name.toString()), true) : __spreadArray(__spreadArray([], files, true), [name.toString()], false);
        }, []);
    };
    FileUtils.isBinary = function (filePath) {
        return isBinaryFileSync(filePath, (0, fs_1.lstatSync)(filePath).size);
    };
    FileUtils.read = function (filePath) {
        return (0, fs_1.readFileSync)(filePath, 'utf8');
    };
    FileUtils.mkdir = function (dirOrFile) {
        var targetDirname = (0, path_1.dirname)(dirOrFile);
        if (!(0, fs_1.existsSync)(targetDirname)) {
            (0, fs_1.mkdirSync)(targetDirname, { recursive: true });
        }
    };
    FileUtils.copy = function (src, dest) {
        (0, fs_1.copyFileSync)(src, dest);
    };
    FileUtils.copyFolder = function (srcDir, destDir) {
        var _this = this;
        try {
            (0, fs_1.mkdirSync)(destDir, { recursive: true });
        }
        catch (e) {
        }
        (0, fs_1.readdirSync)(srcDir).forEach(function (element) {
            var stat = (0, fs_1.lstatSync)((0, path_1.join)(srcDir, element));
            if (stat.isFile()) {
                (0, fs_1.copyFileSync)((0, path_1.join)(srcDir, element), (0, path_1.join)(destDir, element));
            }
            else if (stat.isSymbolicLink()) {
                (0, fs_1.symlinkSync)((0, fs_1.readlinkSync)((0, path_1.join)(srcDir, element)), (0, path_1.join)(destDir, element));
            }
            else if (stat.isDirectory()) {
                _this.copyFolder((0, path_1.join)(srcDir, element), (0, path_1.join)(destDir, element));
            }
        });
    };
    FileUtils.write = function (filePath, content) {
        (0, fs_1.writeFileSync)(filePath, content);
    };
    FileUtils.chmod = function (filePath, mode) {
        (0, fs_1.chmodSync)(filePath, mode);
    };
    FileUtils.resolve = function (relativePath) {
        return (0, path_1.resolve)(relativePath.replace('~', (0, os_1.homedir)()));
    };
    FileUtils.exists = function (filePath) {
        return (0, fs_1.existsSync)(filePath);
    };
    FileUtils.rmdir = function (dirname) {
        (0, fs_1.rmdirSync)(dirname, { recursive: true });
    };
    return FileUtils;
}());
exports.FileUtils = FileUtils;
