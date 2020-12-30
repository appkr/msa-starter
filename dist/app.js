#!/usr/bin/env node
"use strict";
var program = require("@caporal/core").program;
program
    .name("msa-starter")
    .description("spring-boot msa project starter")
    .action(function (_a) {
    var logger = _a.logger;
    logger.info("Hello there?");
});
program.run();
