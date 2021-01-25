#! /bin/sh
source scripts/config/setup_env.sh

clj -M:uberjar --app-artifact-id=${PROJECT_NAME} --app-version=${PROJECT_VERSION}

rm -f lib/${PROJECT_NAME}-*.jar
mv target/${PROJECT_NAME}-${PROJECT_VERSION}.jar lib/
mv target/${PROJECT_NAME}-${PROJECT_VERSION}-standalone.jar lib/