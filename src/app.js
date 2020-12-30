#!/usr/bin/env node

const { program } = require("@caporal/core");

program
  .name("msa-starter")
  .description("spring-boot msa project starter")
  .action(({ logger }) => {
    logger.info("Hello there?");
  });

program.run();
