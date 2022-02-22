import { BuildInfo } from '../model/BuildInfo'

const prompt = require('prompt-sync')({ sigint: true })

export class ControlUtils {
  static continue(): void {
    const mayIContinue = prompt('Continue with these values(y|NO, default:y)? ').toLowerCase() || 'y'
    if (mayIContinue != 'y') {
      throw Error('Stopped!')
    }
  }

  static shouldSkip(srcFilename: string, buildInfo: BuildInfo): boolean {
    let shouldSkip = false
    buildInfo.skipTokens.forEach(function (token) {
      if (srcFilename.indexOf(token) !== -1) {
        shouldSkip = true
      }
    })

    return shouldSkip
  }

  static ask(question: string, defVal: any = null): any {
    question = question.replace('default:{}', 'default:' + defVal)

    const input = prompt(question)
    if (input) {
      return input.toLowerCase()
    }

    return defVal
  }
}
