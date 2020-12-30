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

import { resolve, join, dirname } from 'path'

import { homedir } from "os";

const isBinaryFileSync = require("isbinaryfile").isBinaryFileSync;

export class FileUtils {

  static files(dirname: string): string[] {
    return readdirSync(dirname).reduce((files: string[], file: string) => {
      const name = join(dirname, file);
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

  static copyFolder(srcDir: string, destDir: string) {
    try {
      mkdirSync(destDir, { recursive: true });
    } catch(e) {}

    readdirSync(srcDir).forEach((element) => {
      const stat = lstatSync(join(srcDir, element));
      if (stat.isFile()) {
        copyFileSync(join(srcDir, element), join(destDir, element));
      } else if (stat.isSymbolicLink()) {
        symlinkSync(readlinkSync(join(srcDir, element)), join(destDir, element));
      } else if (stat.isDirectory()) {
        this.copyFolder(join(srcDir, element), join(destDir, element));
      }
    });
  }

  static write(filePath: string, content: string): void {
    writeFileSync(filePath, content);
  }

  static chmod(filePath: string, mode: string): void {
    chmodSync(filePath, mode);
  }

  static resolve(relativePath: string): string {
    return resolve(relativePath.replace('~', homedir()));
  }

  static exists(filePath: string): boolean {
    return existsSync(filePath);
  }

  static rmdir(dirname: string) {
    rmdirSync(dirname, { recursive: true });
  }
}
