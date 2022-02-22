import { FileUtils } from './libs/FileUtils'

export class CleanCommand {
  constructor(private readonly buildDir: string) {
    this.buildDir = buildDir
  }

  clean(): void {
    if (FileUtils.exists(this.buildDir)) {
      FileUtils.rmdir(this.buildDir)
    }
  }
}
