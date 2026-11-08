#!/bin/bash
# runServer smoke for a ported mod: new world, Done, optional RCON commands, stop, error summary.
# Env: TASK (default runServer), RUNDIR (default run/server), CMDS (newline-separated server commands).
REPO=$1; NAME=$2
QD=${QA_OUT:-/root/home/forks/.port-tools/qa-out}; mkdir -p $QD
mkdir -p $OUT; rm -f $OUT/server.log $OUT/cmds.txt
cd "$REPO" || exit 1
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 PATH=/usr/lib/jvm/java-25-openjdk-amd64/bin:$PATH
OPC=""; grep -q opcScript build.gradle 2>/dev/null && OPC="-PopcScript=/root/home/forks/OperaPublicaCreator/gradle/mod.gradle"
TASK=${TASK:-runServer}; D=${RUNDIR:-run/server}; mkdir -p $D; echo "eula=true" > $D/eula.txt
read PORT RPORT < <(python3 - <<'PORTS'
import socket
sockets=[]
for _ in range(2):
    s=socket.socket(); s.bind(('0.0.0.0',0)); sockets.append(s)
print(*(s.getsockname()[1] for s in sockets))
PORTS
)
echo "Selected available ports: game=$PORT rcon=$RPORT" >> "$OUT/cmds.txt"
PW=qa$RANDOM
printf "level-name=qa-%s\nonline-mode=false\nserver-port=%s\nenable-rcon=true\nrcon.port=%s\nrcon.password=%s\n" "$(date +%m%d%H%M)" $PORT $RPORT $PW > $D/server.properties
timeout 1200 ./gradlew $TASK --no-daemon $OPC -Dorg.gradle.jvmargs=-Xmx768m --console=plain < /dev/null > $OUT/server.log 2>&1 &
GPID=$!
for i in $(seq 1 220); do grep -qaE "Done \([0-9.]+s\)|BUILD FAILED|Crash report|Failed to start" $OUT/server.log && break; kill -0 $GPID 2>/dev/null || break; sleep 5; done
sleep 5; RESULT=FAIL; grep -qa "Done (" $OUT/server.log && RESULT=STARTED
if [ $RESULT = STARTED ]; then
  for i in $(seq 1 12); do grep -qa "RCON running" $OUT/server.log && break; sleep 2; done
  if [ -n "$CMDS" ]; then mapfile -t C < <(printf "%b\n" "$CMDS"); python3 /root/home/forks/mi-stack/Industrialization-Overdrive/.port-tools/qa-out/overdrive-smoke/rcon.py $RPORT $PW "${C[@]}" > $OUT/cmds.txt 2>&1; fi
  python3 /root/home/forks/mi-stack/Industrialization-Overdrive/.port-tools/qa-out/overdrive-smoke/rcon.py $RPORT $PW stop >> "$OUT/cmds.txt" 2>&1
fi
for i in $(seq 1 60); do kill -0 $GPID 2>/dev/null || break; sleep 2; done
# Last resort: never leave a server behind (exact PIDs only: this gradle client and its descendants).
if kill -0 $GPID 2>/dev/null; then
  echo "ERROR: own run still alive; no automatic kill performed (GPID=$GPID)" >> "$OUT/cmds.txt"
fi
SAVED=no; sed -n '/Stopping the server/,$p' $OUT/server.log | grep -qa "All dimensions are saved" && SAVED=yes
sed 's/\x1b\[[0-9;]*m//g' $OUT/server.log | grep -aE "/(ERROR|FATAL)\]|Exception|Caused by" | grep -vE "OnlyIn|OpenAL|PluginCaller|Ambiguity|kqueue|OSX/BSD" | sed -E 's/^\[[0-9:]+\] //' | cut -c1-230 | sort | uniq -c | sort -rn | head -25 > $OUT/errors.txt
echo "$NAME: $RESULT saved=$SAVED errors=$(wc -l < $OUT/errors.txt)" | tee $OUT/summary.txt
