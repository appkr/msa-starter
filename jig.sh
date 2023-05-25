#!/usr/bin/env bash

PWD=$(pwd)

[ -d $HOME/.msa-starter ] && rm -rf $HOME/.msa-starter

# build starter app
./gradlew clean build
mv build/libs/msastarter-*-all.jar build/libs/msastarter.jar

# run starter & update metadata for nativeImage
#java -jar build/libs/msastarter.jar generate --useDefault
#java -jar build/libs/msastarter.jar publish --useDefault

java -agentlib:native-image-agent=config-merge-dir=./src/main/resources/META-INF/native-image -jar build/libs/msastarter.jar
java -agentlib:native-image-agent=config-merge-dir=./src/main/resources/META-INF/native-image -jar build/libs/msastarter.jar generate --useDefault
java -agentlib:native-image-agent=config-merge-dir=./src/main/resources/META-INF/native-image -jar build/libs/msastarter.jar publish --useDefault

# build generated application
cd $HOME/.msa-starter && ./gradlew clean build

# clean up
cd $PWD
rm -rf $HOME/.msa-starter
