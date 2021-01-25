#! /bin/sh
source scripts/config/setup_env.sh

clj -M:native-image --image-name ${PROJECT_NAME}
