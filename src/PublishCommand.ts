import { PublishInfo } from './model/PublishInfo'
import { ControlUtils } from './libs/ControlUtils'
import { FileUtils } from './libs/FileUtils'
import simpleGit from 'simple-git'

export class PublishCommand {

  private publishInfo: PublishInfo = {
    buildDir: 'build/example',
    publishDir: '~/example'
  }

  publish(): PublishInfo {
    this.getUserInput()

    console.log('Publishing the project with ', this.publishInfo)
    ControlUtils.continue()

    FileUtils.copyFolder(this.publishInfo.buildDir, this.publishInfo.publishDir)

    this.gitCommit('new project created from msa-starter')

    return this.publishInfo
  }

  getUserInput(): void {
    let buildDir = ControlUtils.ask(
      'What is the build artifact path you want to publish(e.g. ./build/example)? ')
    if (!buildDir) {
      throw Error('Invalid directory value: ' + buildDir)
    }
    buildDir = FileUtils.resolve(buildDir)
    if (!FileUtils.exists(buildDir)) {
      throw Error('Not existing directory value: ' + buildDir)
    }

    let publishDir = ControlUtils.ask('Where do you want to publish the build(e.g. ~/example)? ')
    if (!publishDir) {
      throw Error('Invalid directory value: ' + publishDir)
    }
    publishDir = FileUtils.resolve(publishDir)
    if (FileUtils.exists(publishDir)) {
      throw Error('Already existing directory value: '
        + publishDir + '; Not-existing directory value required')
    }

    this.publishInfo = {
      buildDir: buildDir,
      publishDir: publishDir
    }
  }

  gitCommit(message: string): void {
    const git = simpleGit(this.publishInfo.publishDir, { binary: 'git' })
    git.init()
      .then(() => git.add('.'))
      .then(() => git.commit(message))
  }
}
