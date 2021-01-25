#!/bin/bash
set -e;

if [ -z $CLI_TOOL ]; then
    echo "which tool do you want to use?
1) clj
2) jar
3) native
enter either number or name, or press any other character to use the default (clj):"
    read ans
    case $ans in
        jar|2       ) export CLI_TOOL="jar" ;;
        native|3    ) export CLI_TOOL="native" ;;
        clj|1|""|* ) export CLI_TOOL="clj" ;;
    esac
fi

log_info "cli tool: ${CLI_TOOL}"
case $CLI_TOOL in
    "clj"    ) export cmd="scripts/bin/-clj.sh" ;;
    "jar"    ) export cmd="scripts/bin/-jar.sh" ;;
    "native" ) export cmd="./${PROJECT_NAME} " ;;
esac
