#!/bin/sh

set -e

#shellcheck disable=SC2086
"$JAVA_HOME/bin/java" $JAVA_OPTS -jar /opt/project/app.jar