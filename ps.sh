    for i in `ls /proc|sort -n|perl -ne 'if(/^[0-9]+$/gi){chomp;print "\/proc/$_\/cmdline\n";}'`; do echo $i; cat $i;echo; done
