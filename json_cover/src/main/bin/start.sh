#!/bin/sh
source /etc/profile
BASEDIR=`dirname $0`/..
BASEDIR=`(cd "$BASEDIR"; pwd)`
MAIN_CLASS=com.sprucetec.swagger.json_cover.BoneLauncher

# If a specific java binary isn't specified search for the standard 'java' binary
if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
      JAVACMD="$JAVA_HOME/bin/java"
  else
    JAVACMD=`which java`
  fi
fi

CLASSPATH="$BASEDIR/lib/*:$BASEDIR/conf/"
LOGDIR="$BASEDIR/log"

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

if [ -z "$OPTS_MEMORY" ] ; then
    OPTS_MEMORY="-server -Xms128M -Xmx256M -Xss512k -XX:MetaspaceSize=1G -XX:MaxMetaspaceSize=1G"
fi
echo $1

"$JAVACMD" $JAVA_OPTS \
  $OPTS_MEMORY \
  -classpath "$CLASSPATH" \
  -Dbasedir="$BASEDIR" \
  -Dfile.encoding="UTF-8" \
  -Djava.awt.headless="true" \
  -Dsun.net.client.defaultConnectTimeout="60000" \
  -Dsun.net.client.defaultReadTimeout="60000" \
  -Djmagick.systemclassloader="no" \
  -Dnetworkaddress.cache.ttl="300" \
  -Dsun.net.inetaddr.ttl=300 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath="$LOGDIR/" \
  -XX:ErrorFile="$LOGDIR/java_error_%p.log" \
  $MAIN_CLASS $1 $2
