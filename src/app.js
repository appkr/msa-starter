#!/usr/bin/env node

const { program } = require("@caporal/core");
const { BuildCommand } = require("./BuildCommand");

program
  .name('msa-starter')
  .description('spring-boot msa project starter')
  .command('build', 'build a spring-boot base microservice')
      .action(({ logger}) => {
        const buildInfo = new BuildCommand('templates', 'build').build();
        logger.info("A spring-boot application has been created in ./build/%s. " +
            "You can publish the build result with \"publish\" command.",
            buildInfo.projectName);
      }).default()
;

program.run();
