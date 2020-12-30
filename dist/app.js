#!/usr/bin/env node
"use strict";
var program = require("@caporal/core").program;
var BuildCommand = require("./BuildCommand").BuildCommand;
program
    .name('msa-starter')
    .description('spring-boot msa project starter')
    .command('build', 'build a spring-boot base microservice')
    .action(function (_a) {
    var logger = _a.logger;
    var buildInfo = new BuildCommand('templates', 'build').build();
    logger.info("A spring-boot application has been created in ./build/%s. " +
        "You can publish the build result with \"publish\" command.", buildInfo.projectName);
}).default();
program.run();
