#!/usr/bin/env bash

file="./config/application.yml"

if [ "$RUNTIME_ENV" == "PROD" ]; then
  echo "Deploying in PROD"
  if [ -f "$file" ]
  then
    echo "$file found - Using file injected from pipeline"
  java -server -Djmxtrans.agent.premain.delay=30 -Dsun.net.inetaddr.ttl=30 -Duser.timezone=${TIMEZONE_STR} -Dlog4j2.formatMsgNoLookups=true -XX:+UseZGC -XX:+ZGenerational ${VM_MEM} -XX:+UseCompressedOops -XX:+DisableExplicitGC -XX:+UseTLAB -XX:MetaspaceSize=${VM_MIN_META_MEM} -XX:MaxMetaspaceSize=${VM_MAX_META_MEM} -Dcom.sun.management.jmxremote.port=${JMX_PORT} -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false ${OTEL_AGENT} -cp ./file-nexus.jar application.com.samsepiol.file.nexus.FileNexusApplication --spring.config.location=./config/application.yml
  else
    echo "$file not found - Using config file bundled earlier"
  java -server -Djmxtrans.agent.premain.delay=30 -Dsun.net.inetaddr.ttl=30 -Duser.timezone=${TIMEZONE_STR} -Dlog4j2.formatMsgNoLookups=true -XX:+UseZGC -XX:+ZGenerational ${VM_MEM} -XX:+UseCompressedOops -XX:+DisableExplicitGC -XX:+UseTLAB -XX:MetaspaceSize=${VM_MIN_META_MEM} -XX:MaxMetaspaceSize=${VM_MAX_META_MEM} -Dcom.sun.management.jmxremote.port=${JMX_PORT} -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false ${OTEL_AGENT}  -cp ./file-nexus.jar application.com.samsepiol.file.nexus.FileNexusApplication --spring.config.location=./local-config/application.yml
  fi
else
  echo "Deploying in $RUNTIME_ENV"
  if [ -f "$file" ]
  then
    echo "$file found - Using file injected from pipeline"
        eval "env $(cat /mnt/secrets-store/"$BANK_NAME"_"$OTEL_SERVICE_NAME" | jq -r 'to_entries | map("\(.key)=\(.value)") | @sh')   java -server -Djmxtrans.agent.premain.delay=30 -Dsun.net.inetaddr.ttl=30 -Duser.timezone=${TIMEZONE_STR} -Dlog4j2.formatMsgNoLookups=true -XX:+UseZGC ${VM_MEM} -XX:+UseCompressedOops -XX:+DisableExplicitGC -XX:+UseTLAB -XX:MetaspaceSize=${VM_MIN_META_MEM} -XX:MaxMetaspaceSize=${VM_MAX_META_MEM} -Dcom.sun.management.jmxremote.port=${JMX_PORT} -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false ${OTEL_AGENT} -cp ./file-nexus.jar com.samsepiol.file.nexus.application.FileNexusApplication --spring.config.location=./config/application.yml"
  else
    echo "$file not found - Using config file bundled earlier"
   eval "env $(cat /mnt/secrets-store/"$BANK_NAME"_"$OTEL_SERVICE_NAME" | jq -r 'to_entries | map("\(.key)=\(.value)") | @sh')  java -server -Djmxtrans.agent.premain.delay=30 -Dsun.net.inetaddr.ttl=30 -Duser.timezone=${TIMEZONE_STR} -Dlog4j2.formatMsgNoLookups=true -XX:+UseZGC ${VM_MEM} -XX:+UseCompressedOops -XX:+DisableExplicitGC -XX:+UseTLAB -XX:MetaspaceSize=${VM_MIN_META_MEM} -XX:MaxMetaspaceSize=${VM_MAX_META_MEM} -Dcom.sun.management.jmxremote.port=${JMX_PORT} -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false ${OTEL_AGENT}  -cp ./file-nexus.jar com.samsepiol.file.nexus.application.FileNexusApplication --spring.config.location=./local-config/application.yml"
  fi
fi