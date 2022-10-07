[![](https://api.travis-ci.com/appkr/msa-starter.svg)](https://travis-ci.com/github/appkr/msa-starter)

> **`중요 공지`** 1.x TypeScript/Node.js -> 2.x Groovy/Gradle task -> 3.x Java 로 구현 언어가 다릅니다.
> 
> **`긴급 공지`** Native Build(*-macos-amd64.zip, *-macos-aarch64.zip)는 작동하지 않습니다; Jar(*-all.jar), Dist(*-shadow.zip) 빌드를 JDK17에서만 작동합니다; JDK8에서 작동할 수 있도록 수정중입니다.

## msa-starter

메쉬코리아의 스택과 업무 표준에 따르는 스프링부트 마이크로서비스 베이스 프로젝트를 만드는 스타터 앱입니다. 이하 가이드에 따라 빌드된 결과물로 API Spec 작성부터 바로 시작할 수 있습니다.

### 설치법

~~방법 1: [Release](https://github.com/appkr/msa-starter/releases)에서 최신 바이너리 파일을 받아서 사용합니다 (**jdk 불필요**).~~ **방법 1은 문제가 있어서 고치고 있습니다**
```shell
VERSION=3.2.0
OS=osx
ARCH=intel
wget https://github.com/appkr/msa-starter/releases/download/$VERSION/msastarter-$VERSION-$OS-$ARCH.zip
unzip msastarter-{VERSION}-{OS}-{ARCH}.zip -d /usr/local/bin/
msastarter
# Usage: msa-starter [-hV] [COMMAND]
# Command that generates a Spring-boot microservice skeleton
#   -h, --help      Show this help message and exit.
#   -V, --version   Print version information and exit.
# Commands:
#   generate       Create a new project
#   publish        Publish the project to a new directory
# Developed by appkr<juwonkim@me.com>
```

방법 2: [Release](https://github.com/appkr/msa-starter/releases)에서 최신 jar 파일을 받아서 사용합니다 (**jdk17 필요**). **jdk8을 사용할 수 있도록 고치고 있습니다**
```shell
VERSION=3.2.0
wget https://github.com/appkr/msa-starter/releases/download/$VERSION/msastarter-$VERSION-all.jar
java -jar msastarter-$VERSION-all.jar
```

방법 3: [Release](https://github.com/appkr/msa-starter/releases)에서 최신 zip 파일을 받아서 사용합니다 (**jdk17 필요**). **jdk8을 사용할 수 있도록 고치고 있습니다**
```shell
VERSION=3.2.0
wget https://github.com/appkr/msa-starter/releases/download/$VERSION/msastarter-shadow-$VERSION.zip
unzip msastarter-shadow-$VERSION.zip
msastarter-shadow-$VERSION/bin/msastarter
``` 

### 사용법

`generate`, `publish` 두 개의 서브 커맨드를 제공합니다

#### generate 커맨드

```shell
$ APP=msastarter # OR APP="java -jar msastarter-$VERSION-all.jar"
$ $APP generate↵ 
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
$ APP=msastarter
$ $APP publish↵ 
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

온보딩 중이신가요? 새 프로젝트를 진행중이신가요? 스타터 앱 및 템플릿의 문제점 또는 개선점을 발견했다면 고치고 PR해주세요

```
src
└── main
    ├── java                # 스타터 앱
    └── resources
        └── templates       # Handlebar 템플릿을 포함하고 있는 스프링부트 프로젝트
            ├── gradle.properties
            ├── webflux     # WebFlux/R2DBC 프로젝트 템플릿
            └── webmvc      # WebMVC/JPA 프로젝트 템플릿
```
