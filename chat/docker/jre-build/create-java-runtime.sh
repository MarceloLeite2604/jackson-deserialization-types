#!/bin/sh

set -e;

set \
  java.base \
  java.compiler \
  java.desktop \
  java.instrument \
  java.management \
  java.net.http \
  java.prefs \
  java.rmi \
  java.scripting \
  java.security.jgss \
  java.sql.rowset \
  jdk.jfr \
  jdk.net \
  jdk.unsupported;

IFS=",";
add_modules_value=$*
readonly add_modules_value;
IFS=" "

if [ -n "$JAVA_HOME"  ];
then
  jlink_bin="$JAVA_HOME/bin/jlink";
else
  jlink_bin=$(which jlink);
fi;

[ -z "$jlink_bin" ] && >&2 echo "Could not find jlink binary." && exit 1;

echo "Jlink found on \"$jlink_bin\" path.";
echo "The following modules will be added on Java runtime: $add_modules_value";

"$jlink_bin" \
  --add-modules "$add_modules_value" \
  --strip-java-debug-attributes \
  --no-man-pages \
  --no-header-files \
  --compress=2 \
  --output /tmp/java-runtime;