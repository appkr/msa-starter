[![](https://api.travis-ci.com/appkr/msa-starter.svg)](https://travis-ci.com/github/appkr/msa-starter)

> **`중요 공지`** 1.x TypeScript -> 2.x Gradle task -> 3.x Java 로 구현 언어가 다릅니다.  

## msa-starter

메쉬코리아의 스택과 업무 표준에 따르는 스프링부트 마이크로서비스 베이스 프로젝트를 만드는 스타터 앱입니다. 이하 가이드에 따라 빌드된 결과물로 API Spec 작성부터 바로 시작할 수 있습니다.

### 설치법

- 방법 1: [Releases 페이지](https://github.com/appkr/msa-starter/releases)에서 각자의 아키텍처에 맞는 바이너리를 내려 받습니다(추천).
- 방법 2: 프로젝트를 클론 받아 직접 빌드하고 사용합니다.
```shell
./gradlew clean build

# jar 파일을 이용한 방법
java -jar build/libs/msastarter-VERSION-all.jar generate
java -jar build/libs/msastarter-VERSION-all.jar publish

# zip 파일을 풀고, 쉘 스크립트를 이용하는 방법
unzip build/distributions/msastarter-shadow-VERSION.zip -d {DIR}
{DIR}/bin/msastarter
``` 
- 방법 3: 프로젝트를 클론 받아 Gradle로 구동합니다(추천하지 않음).
```shell
./gradlew run --args="generate"
./gradlew run --args="publish"
``` 

### 사용법

generate, publish 두 개의 서브 커맨드를 제공합니다

```shell
./msastarter-3.0.0-SNAPSHOT-all
# Usage: msa-starter [-hV] [COMMAND]
# Command that generates a Spring-boot microservice skeleton
#   -h, --help      Show this help message and exit.
#   -V, --version   Print version information and exit.
# Commands:
#   generate       Create a new project
#   publish        Publish the project to a new directory
# Developed by appkr<juwonkim@me.com>
```

#### generate 커맨드

```shell
$ ./msastarter-VERSION-all generate 
# A WebMVC/JPA project(m)? Or a WebFlux/R2DBC project(f) (default: m)? ↵
# Is vroong project(y/n, default: n)? ↵
# Which java version will you choose(1.8/11/17, default: 17)? ↵
# What is the project name(default: example)? demo↵
# What is the group name(default: dev.appkr)? me.group↵
# What is the web server port(default: 8080)? 19999↵
# What is the media type for request and response(default: application/json)? ↵

# Proceed ('Enter' to continue OR 'n' to quit)? ↵
# ✔ copyGradleProperties: ~/.msa-starter/gradle.properties
# ...
# ✔ ~/.msa-starter/src/main/java/me/group/demo/domain/Example.java
```

#### publish 커맨드

```shell
$ ./msastarter-VERSION-all publish 
# What is the dir you want to publish? ~/demo ↵
# Proceed ('Enter' to continue OR 'n' to quit)?
# targetDir:  ~/demo
```

### 포함된 내용

- spring-boot 2.x
- 마이크로서비스 패턴: 헬쓰 체크 패턴, 추적 및 로깅 패턴, 보안 패턴
- 도커를 이용한 로컬 개발 환경 설정: MySQL, Kafka 등
- JPA, MySQL/H2 driver, QueryDSL, JPA Specification 설정
- IPC
  - REST: API first 개발 방법론 적용
  - Messaging: Kafka 설정 
- 스케쥴 태스크 (분산 잠금 포함) 
- Jenkins & K8S 설정
- 예제
  - Example API(MockMvc 테스트 포함)
  - Transactional Outbox 패턴 포함

## 기여하기

온보딩 중이신가요? 새 프로젝트를 진행중이신가요? 스타터 앱 및 `templates`의 문제점을 발견했다면 고치고 PR해주세요

```
Folder Structure
src
└── main
    ├── java                # 스타터 앱
    └── resources
        └── templates       # Handlebar 템플릿을 포함하고 있는 스프링부트 프로젝트
            ├── gradle.properties
            ├── webflux     # WebFlux/R2DBC 프로젝트 템플릿
            └── webmvc      # WebMVC/JPA 프로젝트 템플릿
```

## Build

Install GraalVM

```shell
wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.2.0/graalvm-ce-java17-darwin-amd64-22.2.0.tar.gz
tar -xzf graalvm-ce-java17-darwin-amd64-22.2.0.tar.gz

VMDIR=/Library/Java/JavaVirtualMachines
GRDIR=graalvm-ce-java17-22.2.0

sudo mv $GRDIR $VMDIR/
sudo xattr -r -d com.apple.quarantine $VMDIR/$GRDIR # make 'gu' and 'native-image' command executable

export JAVA_HOME=$VMDIR/$GRDIR/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

gu install native-image
```

Build native image

```shell
./gradlew clean assemble
native-image -jar build/libs/msastarter-VERSION-all.jar
```