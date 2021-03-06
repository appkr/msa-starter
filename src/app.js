#!/usr/bin/env node

import {BuildCommand} from "./BuildCommand";
import {PublishCommand} from "./PublishCommand";
import {CleanCommand} from "./CleanCommand";
import {program} from "@caporal/core";

program
  .name('msa-starter')
  .description('spring-boot msa project starter')
  .command('build', 'build a spring-boot base microservice')
      .option('-y, --use-default <value>', 'Use default values', {
        default: false
      })
      .action(({ logger, options}) => {
        logger.info("Cleaning the build dir...")
        new CleanCommand('build').clean();

        const buildInfo = new BuildCommand('templates', 'build', options.useDefault).build();
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
