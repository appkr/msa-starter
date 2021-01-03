#!/usr/bin/env node

import {PublishCommand} from "./PublishCommand";
import {CleanCommand} from "./CleanCommand";

const { program } = require("@caporal/core");
const { BuildCommand } = require("./BuildCommand");

program
  .name('msa-starter')
  .description('spring-boot msa project starter')
  .command('build', 'build a spring-boot base microservice')
      .action(({ logger}) => {
        logger.info("Cleaning the build dir...")
        new CleanCommand('build').clean();

        const buildInfo = new BuildCommand('templates', 'build').build();
        logger.info("A spring-boot application has been created in ./build/%s. " +
            "You can publish the build result with \"publish\" command.",
            buildInfo.projectName);
      }).default()
  .command('publish', 'publish build artifact to another folder')
      .action(({ logger}) => {
        const publishInfo = new PublishCommand().publish();
        logger.info("Done! Read %s/README.md to learn how to start.", publishInfo.publishDir);
      })
  .command('clean', 'clean build output')
      .action(({ logger}) => {
        new CleanCommand('build').clean();
        logger.info("Done!");
      })
;

program.run();
