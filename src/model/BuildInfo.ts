import {ProjectType} from "./ProjectType"
import {JavaVersion} from "./JavaVersion"

export interface BuildInfo {
  projectType: ProjectType;
  projectName: string;
  groupName: string;
  packageName: string;
  portNumber: number;
  mediaType: string;
  javaVersion: JavaVersion;
  dockerImage: string
  skipTokens: string[]
}
