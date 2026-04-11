@echo off
:choice
set /P c=Load Tasks.xml [y/n]?
if /i "%c%" EQU "Y" goto :tasks

cd vxd
java -agentlib:jdwp=transport=dt_shmem,address=jdbconn,server=y,suspend=n -classpath "C:\Program*Files\Java\jdk1.8.0_281\lib\dt.jar;C:\Program*Files\Java\jdk1.8.0_281\lib\tools.jar;..\crimson-1.1.3.jar;.." vxd.vxd 
cd ..

exit

:tasks

cd vxd
java -agentlib:jdwp=transport=dt_shmem,address=jdbconn,server=y,suspend=n -classpath "C:\Program*Files\Java\jdk1.8.0_281\lib\dt.jar;C:\Program*Files\Java\jdk1.8.0_281\lib\tools.jar;..\crimson-1.1.3.jar;.." vxd.vxd ./Work/Tasks/Tasks.xml
cd ..