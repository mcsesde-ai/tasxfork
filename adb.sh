adb devices
echo Forwarding 1080 Turn on SOCKS proxy on Android
adb forward tcp:1080 tcp:1080
adb forward --list
