for i in `find . -name '*.java'`; do javac -sourcepath .. -g -cp .. $i; done
