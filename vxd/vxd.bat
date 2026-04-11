@echo off
:choice
set /P c=Load Tasks.xml [y/n]?
if /i "%c%" EQU "Y" goto :tasks

java -cp "C:\Program Files\Java\jdk1.8.0_281\lib\dt.jar";"C:\Program Files\Java\jdk1.8.0_281\lib\tools.jar";..\..\crimson-1.1.3.jar;.. vxd.vxd
exit

:tasks
java -cp "C:\Program Files\Java\jdk1.8.0_281\lib\dt.jar";"C:\Program Files\Java\jdk1.8.0_281\lib\tools.jar";..\..\crimson-1.1.3.jar;.. vxd.vxd ./Work/Tasks/Tasks.xml
