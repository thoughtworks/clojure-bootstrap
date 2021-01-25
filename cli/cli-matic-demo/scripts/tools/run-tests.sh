#! /bin/sh
source scripts/config/setup_env.sh

echo "Run test in watch mode?
1) (y)es
2) (n)o
enter either number or letter, or press return to select the default (yes):"
read ans
case $ans in
    n|2      ) scripts/bin/-kaocha.sh "$@" ;;
    y|1|""|* ) log_info "watch mode enabled!" ; scripts/bin/-kaocha.sh --watch "$@";;
esac
