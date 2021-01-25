#!/bin/bash
set -e;

function log_message { echo "--> [$1[m $2"; }
function log_error   { log_message "1;41m[ERROR]" "${1}"; }
function log_warning { log_message "1;33m[WARNING]" "${1}"; }
function log_info    { log_message "1;32m[INFO]" "${1}"; }
function log_debug   { log_message "1;36m[DEBUG]" "${1}"; }
function log_trace   { log_message "1;34m[TRACE]" "${1}"; }

if [ -z $LOGGING_LEVEL ]; then
    export LOGGING_LEVEL=info
fi
log_info "logging level set to: ${LOGGING_LEVEL}";

config_file=scripts/config/clj_env.sh

if [ ! -f "${config_file}" ]; then
    read -e -p "Please enter Java Home:" inJavaHome;
    read -e -p "Please enter GraalVM Home:" inGraalVMHome;
    read -e -p "Please enter Project Name:" inProjectName;
    read -e -p "Please enter Project Version:" inProjectVersion;
    echo "which cli tool do you want to use?
1) clj
2) jar
3) native
enter either number or name, or press any other character to use the default (clj):"
    read ans
    case $ans in
        jar|2       ) export inCliTool="jar" ;;
        native|3    ) export inCliTool="native" ;;
        clj|1|""|* ) export inCliTool="clj" ;;
    esac

    cat > ${config_file} <<EOL
log_info "setting Java Home..."
export JAVA_HOME=${inJavaHome}
log_info "setting GraalVM Home..."
export GRAALVM_HOME=${inGraalVMHome}
log_info "setting defatul cli tool..."
export CLI_TOOL=${inCliTool}

log_info "setting Project information..."
export PROJECT_NAME=${inProjectName}
export PROJECT_VERSION=${inProjectVersion}
EOL
    log_info "Settings saved to ${config_file}";
fi

if [ -f "${config_file}" ]; then
    log_info "loading environment variables from ${config_file}";
    source ${config_file}
else
    log_error "missing configuration file ${config_file}!";
    exit 1;
fi