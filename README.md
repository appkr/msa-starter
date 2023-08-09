[![](https://api.travis-ci.com/appkr/msa-starter.svg)](https://travis-ci.com/github/appkr/msa-starter)

> **`중요 공지`** 
> 
> 1.x TypeScript/Node.js -> 2.x Groovy/Gradle task -> 3.x Java 로 구현 언어가 다릅니다.
> 
> 3.4.0부터 webflux/R2DBC 프로젝트 생성을 지원하지 않습니다; Java 17, Spring Boot 3.0, MySQL 8 버전만 지원합니다.

## msa-starter

메쉬코리아의 스택과 업무 표준에 따르는 스프링부트 마이크로서비스 베이스 프로젝트를 만드는 스타터 앱입니다. 이하 가이드에 따라 빌드된 결과물로 API Spec 작성부터 바로 시작할 수 있습니다.

### 설치법

방법 1: [Release](https://github.com/appkr/msa-starter/releases)에서 최신 바이너리 파일을 받아서 사용합니다(**jdk 불필요**).
```shell
VERSION=3.4.3
OS=macos
ARCH=amd64 # for Intel cpu
ARCH=aarch64 # for m1 or m2 cpu
wget https://github.com/appkr/msa-starter/releases/download/$VERSION/msastarter-$VERSION-$OS-$ARCH.zip
sudo unzip msastarter-$VERSION-$OS-$ARCH.zip -d /usr/local/bin/

# To add an application to MacOS Gatekeeper's exceptions
spctl --add /usr/local/bin/msastarter

msastarter
# The application is running in NATIVE mode!!!
# 
# Usage: msastarter [-hV] [COMMAND]
# Command that generates a Spring-boot microservice skeleton
# 
# Options:
#   -h, --help      Show this help message and exit.
#   -V, --version   Print version information and exit.
# 
# Commands:
#   generate       Create a new project
#   publish        Publish the project to a new directory
# 
# appkr<juwonkim@me.com>
```

방법 2: [Release](https://github.com/appkr/msa-starter/releases)에서 최신 jar 파일을 받아서 사용합니다(**jdk 필요**)
```shell
VERSION=3.4.3
wget https://github.com/appkr/msa-starter/releases/download/$VERSION/msastarter-$VERSION-all.jar
java -jar msastarter-$VERSION-all.jar
```

방법 3: [Release](https://github.com/appkr/msa-starter/releases)에서 최신 zip 파일을 받아서 사용합니다(**jdk 필요**)
```shell
VERSION=3.4.3
wget https://github.com/appkr/msa-starter/releases/download/$VERSION/msastarter-shadow-$VERSION.zip
unzip msastarter-shadow-$VERSION.zip
msastarter-shadow-$VERSION/bin/msastarter
``` 

### 사용법

`generate`, `publish` 두 개의 서브 커맨드를 제공합니다

#### generate 커맨드

```shell
APP=msastarter 
# OR APP="java -jar msastarter-$VERSION-all.jar"

$APP generate 
# Is vroong project(y/n, default: n)? ↵
# What is the project name(default: example)? demo↵
# What is the group name(default: dev.appkr)? me.group↵
# What is the web server port(default: 8080)? 19999↵
# What is the media type for request and response(default: application/json)? ↵
# Include example codes(y/n, default: y)? ↵

# Proceed ('Enter' to continue OR 'n' to quit)? ↵
# ✔ templates/webmvc/build.gradle -> /path/to/.msa-starter/build.gradle
# ...
```

#### publish 커맨드

```shell
APP=msastarter
$APP publish 
# What is the dir you want to publish? ~/demo ↵
# Proceed ('Enter' to continue OR 'n' to quit)?
# targetDir: /path/to/demo
```

### 포함된 내용

- spring-boot 3.x
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
            └── webmvc      # WebMVC/JPA 프로젝트 템플릿
```

### 테스트

```shell
bash jig.sh
```

### 배포전 버전 수정

- build.gradle
- README.md
- src/main/java/dev/appkr/starter/MsaStarter.java

### 네이티브 빌드

- [src/main/resources/META-INF/README-native-image.md](src/main/resources/META-INF/README-native-image.md)
- [https://github.com/meshkorea/msa-starter/settings/actions/runners](https://github.com/meshkorea/msa-starter/settings/actions/runners)
