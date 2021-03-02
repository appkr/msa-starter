# {{projectName}}

## 개발 환경
{{#ifJava11 javaVersion}}
- [amazonaws corretto jdk11](https://docs.aws.amazon.com/ko_kr/corretto/latest/corretto-11-ug/what-is-corretto-11.html) 을 사용합니다
```bash
$ brew install homebrew/cask-versions/corretto11 --cask
$ jenv add /Library/Java/JavaVirtualMachines/amazon-corretto-11.jdk/Contents/Home
$ jenv versions
```
{{/ifJava11}}
- jhipster-uaa.zip 파일 압축을 풀고 도커 이미지를 빌드합니다. 이 과정은 최초 한번만 실행하면 됩니다
```bash
~ $ cp msa-starter/jhipster-uaa.zip ./
~ $ unzip jhipster-uaa.zip && cd jhipster-uaa && ./gradlew jibDockerBuild
```
- 아래 명령으로 MySQL(3306), Kafka(9092), jhipster-uaa(9999) 등을 구동합니다
```bash
~/{{projectName}} $ ./gradlew clusterUp
# Ctrl + c to quit
```
- 애플리케이션을 구동합니다
```bash
~/{{projectName}} $ export SPRING_PROFILES_ACTIVE=local; export USER_TIMEZONE="Asia/Seoul"; ./gradlew clean bootRun
$ curl -s http://localhost:{{portNumber}}/management/health
```
- [Postman Collection & Environment](./postman)를 import하여 Example 및 UAA API를 작동해볼 수 있습니다

### 계정
docker service|username|password
---|---|---
mysql|root|secret
{{#ifVroongProject projectType}}
kafka|admin|admin-secret
kafka|alice|alice-secret
{{/ifVroongProject}}

{{#ifVroongProject projectType}}
## 개발
- java code는 [google style guide](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml) 를 따릅니다 (hard wrap은 120까지 허용)
{{#ifJava11 javaVersion}}
- Java version 11을 사용함에도 불구하고 Java version 8 스타일로 코딩합니다
```java
var a = "foo";    // WRONG 
String a = "foo"; // GOOD
```
{{/ifJava11}}

## 빌드
- [Jenkins 적용 가이드](https://wiki.mm.meshkorea.net/pages/viewpage.action?pageId=95855850) 에 따라 빌드합니다
- [Jenkins BlueOcean](https://jenkins.meshtools.io/blue/organizations/jenkins/{{projectName}}/activity) 화면에서 빌드합니다

## 클라이언트 SDK 빌드 및 배포
```bash
~/{{projectName}} $ ./gradlew :clients:clean :clients:publish -Dorg.gradle.internal.publish.checksums.insecure=true
# 배포 결과는 https://nexus.mm.meshkorea.net/ 에서 확인할 수 있습니다
```
```bash
~/{{projectName}} $ ./gradlew :clients:redoc
# 빌드된 API 문서는 clients/build/redoc.html 입니다
```

## 배포
- 릴리스 브랜치에서 `gradle.properties`에 애플리케이션 버전을 부여합니다
- [ArgoCD](https://argocd.meshtools.io/applications?search={{projectName}}) 를 사용합니다 

## 모니터링
- [Kibana](https://kibana.meshtools.io/)
- [Sentry](https://sentry.io)
- [Newrelic](http://rpm.newrelic.com/)
{{/ifVroongProject}}
