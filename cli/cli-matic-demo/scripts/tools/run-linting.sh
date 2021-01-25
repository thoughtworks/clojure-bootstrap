#! /bin/sh
source scripts/config/setup_env.sh

clj-kondo --lint src --lint test