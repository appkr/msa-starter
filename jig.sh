#!/usr/bin/env bash

[ -d $HOME/.msa-starter ] && rm -rf $HOME/.msa-starter
./gradlew clean build
mv build/libs/msastarter-*-all.jar build/libs/msastarter.jar
java -jar build/libs/msastarter.jar generate --useDefault
java -jar build/libs/msastarter.jar publish --useDefault
cd $HOME/.msa-starter && ./gradlew clean build
rm -rf $HOME/.msa-starter
