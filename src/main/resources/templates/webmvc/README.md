# {{projectName}}

## 개발 환경

- amazonaws corretto jdk 17을 사용합니다
```shell
$ brew install homebrew/cask-versions/corretto17 --cask
$ jenv add /Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home
$ jenv versions
```

- 아래 명령으로 MySQL(3306), Kafka(9092), vroong-uaa(9999) 등을 구동합니다
```shell
~/{{projectName}} $ ./gradlew composeUp
# To stop and delete the cluster, ./gradlew composeDown
```

- 애플리케이션을 구동합니다
```shell
~/{{projectName}} $ export SPRING_PROFILES_ACTIVE=local; export USER_TIMEZONE="Asia/Seoul"; ./gradlew clean bootRun -x :clients:bootRun
$ curl -s http://localhost:{{portNumber}}/management/health
```
- [Postman Collection & Environment](./postman)를 import하여 Example 및 UAA API를 작동해볼 수 있습니다

### 계정

Infra

docker service|username|password
---|---|---
mysql|root|secret
{{#isVroongProject}}
kafka|admin|admin-secret
kafka|alice|alice-secret
{{/isVroongProject}}

OAuth2 클라이언트

| grant_type         | clientId                             | clientSecret  |
| ------------------ | ------------------------------------ | ------------- |
| client_credentials | bc68ef76-95c4-40b2-9854-b3111318e319 | machinesecret |
| password           | eb4814fb-66f8-498d-b65c-d6ffa80bb712 | webappsecret  |

OAuth2 사용자

| username                                 | password | role  |
| ---------------------------------------- | -------- | ----- |
| 95c464a5-ea74-43e9-b1cd-ac002a20adf2     | user     | USER  |
| 19ab74d5-68c4-4f8a-b6e9-12f960495141     | admin    | ADMIN |

## 개발

- java code는 [google style guide](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)를 따릅니다 (hard wrap은 120까지 허용)
- 패키지 구조는 [육각형 구조](https://reflectoring.io/spring-hexagonal/)를 따릅니다

### 프로젝트 최신화

```shell
~/{{projectName}} $ ./gradlew dependencyUpdates
```

{{#isVroongProject}}
## 빌드

- [Jenkins 적용 가이드](https://wiki.mm.meshkorea.net/pages/viewpage.action?pageId=95855850)에 따라 빌드합니다
- [Jenkins BlueOcean](https://jenkins.meshtools.io/blue/organizations/jenkins/{{projectName}}/activity) 화면에서 빌드합니다
{{/isVroongProject}}

## 클라이언트 SDK 빌드 및 배포

{{#isVroongProject}}
```shell
~/{{projectName}} $ ./gradlew :clients:clean :clients:publish
# 배포 결과는 https://nexus.mm.meshkorea.net/ 에서 확인할 수 있습니다
```
{{/isVroongProject}}

```shell
~/{{projectName}} $ ./gradlew redoc
# 빌드된 API 문서는 build/redoc.html 입니다
~/{{projectName}} $ ./gradlew generate
# 빌드된 비동기 API 문서는 build/asyncapi.html 입니다
```

{{#isVroongProject}}
## 배포

- 릴리스 브랜치에서 `gradle.properties`에 애플리케이션 버전을 부여합니다
- [ArgoCD](https://argocd.meshtools.io/applications?search={{projectName}})를 사용합니다 

## 모니터링

- [Grafana](https://monitoring.meshtools.io/)
- [Kibana](https://kibana.meshtools.io/)
- [Sentry](https://sentry.io)
{{/isVroongProject}}
