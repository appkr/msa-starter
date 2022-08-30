[![](https://api.travis-ci.com/appkr/msa-starter.svg)](https://travis-ci.com/github/appkr/msa-starter)

> **`중요 공지`** 2.0.0 버전부터는 nodejs/Typescript 대신 gradle task를 사용합니다  

## msa-starter

메쉬코리아의 스택과 업무 표준에 따르는 스프링부트 마이크로서비스 베이스 프로젝트를 만드는 스타터 앱입니다. 이하 가이드에 따라 빌드된 결과물로 API Spec 작성부터 바로 시작할 수 있습니다.

### 설치법

```bash
$ git clone https://github.com/meshkorea/msa-starter.git
```

### 사용법

spring-boot 마이크로서비스 프로젝트를 만듭니다. `build` 폴더에 새 프로젝트가 생성됩니다.

```bash
~/msa-starter $ ./gradlew clean generate 
# > 부릉 프로젝트입니까(y/n, default: n)?: ↵
# > 사용하려는 자바 버전은 무엇입니까(1.8/11/17, default: 17)?: ↵
# > 프로젝트 이름은 무엇입니까(default: example)? demo
# > 그룹 이름은 무엇입니까(default: dev.appkr)? me.group
# > 웹 서버 포트는 무엇입니까(default: 8080)? 19999
# > 웹 요청 및 응답에 사용할 미디어 타입은 무엇입니까(default: application/json)? ↵

# 진행할까요('n' to quit)? [projectType:n, projectName:demo, ...] ↵
# .../msa-starter/build/docker/compose.yml created
# ...
# BUILD SUCCESSFUL in ...
```

`build` 폴더에 생성된 프로젝트를 다른 폴더로 복사해서 프로젝트를 시작합니다. git 초기화해야 빌드가 가능합니다.

```bash
~/msa-starter $ cp -r build/ {path-to-your-project}
~/msa-starter $ cd {path-to-your-project} && git init &&./gradlew clean build
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
├── templates               # Handlebar 템플릿을 포함하고 있는 스프링부트 프로젝트
│   ├── webflux             # WebFlux/R2DBC 프로젝트 템플릿
│   └── webmvc              # WebMVC/JPA 프로젝트 템플릿
└── build.gradle            # 스타터 앱 (a Gradle Task)
```
