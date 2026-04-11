export XDG_RUNTIME_DIR=~/../usr/bin
weston --no-config --backend=vnc --width=1440 --height=720 --address=127.0.0.1 --port=5900 --xwayland
