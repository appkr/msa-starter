"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ControlUtils = void 0;
var prompt = require("prompt-sync")({ sigint: true });
var ControlUtils = /** @class */ (function () {
    function ControlUtils() {
    }
    ControlUtils.continue = function () {
        var mayIContinue = prompt('Continue with these values(y|NO, default:y)? ').toLowerCase() || 'y';
        if (mayIContinue != "y") {
            throw Error("Stopped!");
        }
    };
    ControlUtils.shouldSkip = function (srcFilename, buildInfo) {
        var shouldSkip = false;
        buildInfo.skipTokens.forEach(function (token) {
            if (srcFilename.indexOf(token) !== -1) {
                shouldSkip = true;
            }
        });
        return shouldSkip;
    };
    ControlUtils.ask = function (question, defVal) {
        if (defVal === void 0) { defVal = null; }
        question = question.replace('default:{}', 'default:' + defVal);
        var input = prompt(question);
        if (input) {
            return input.toLowerCase();
        }
        return defVal;
    };
    return ControlUtils;
}());
exports.ControlUtils = ControlUtils;
