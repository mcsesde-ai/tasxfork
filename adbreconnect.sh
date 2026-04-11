#!/bin/bash

set -o errexit
set -o nounset
#set -o xtrace

PROGNAME=$(basename $0)
die () {
  echo "${PROGNAME}: ${1:-"Unknown Error, Abort."}" 1>&2
  exit 1
}
status=0
./adb.sh &
while [[ 1 ]]; do
  curl -sSf --socks5-hostname localhost:1080 www.google.com > /dev/null || status=$?
  if [[ $status -ne 0 ]]; then
if [ `pgrep $!` ]; then
kill -9 $!
fi
./adb.sh &
    echo "Trying to reconnect .."
    # kill proxy
    # reconnect-cmd || die "$LINENO: reconnecting failed"
  fi
  sleep 15
done
