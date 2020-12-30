import {
  existsSync,
  statSync,
  lstatSync,
  readdirSync,
  mkdirSync,
  rmdirSync,
  symlinkSync,
  readlinkSync,
  readFileSync,
  writeFileSync,
  copyFileSync,
  chmodSync
} from 'fs';

import {
  resolve,
  join,
  dirname
} from 'path'

const isBinaryFileSync = require("isbinaryfile").isBinaryFileSync;

export class FileUtils {

  static files(dir: string): string[] {
    return readdirSync(dir).reduce((files: string[], file: string) => {
      const name = join(dir, file);
      const isDirectory = statSync(name).isDirectory();
      return isDirectory ? [...files, ...FileUtils.files(name.toString())] : [...files, name.toString()];
    }, []);
  }

  static isBinary(filePath: string): boolean {
    return isBinaryFileSync(filePath, lstatSync(filePath).size);
  }

  static read(filePath: string): string {
    return readFileSync(filePath, 'utf8');
  }

  static mkdir(dirOrFile: string): void {
    const targetDirname = dirname(dirOrFile);
    if (!existsSync(targetDirname)) {
      mkdirSync(targetDirname, { recursive: true });
    }
  }

  static copy(src: string, dest: string): void {
    copyFileSync(src, dest);
  }

  static write(filePath: string, content: string): void {
    writeFileSync(filePath, content);
  }

  static chmod(filePath: string, mode: string): void {
    chmodSync(filePath, mode);
  }

}
