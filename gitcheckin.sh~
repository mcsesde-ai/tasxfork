echo Cleanup
rm ./vxd/Work/Tasks/*.decrypted 2> /dev/null
rm ./vxd/Work/Tasks/*.unencrypted 2> /dev/null
rm ./vxd/Work/Tasks/Archive/*.decrypted 2> /dev/null
rm ./vxd/Work/Tasks/Archive/*.unencrypted 2> /dev/null
for i in `find . -name '*.decrypted'`; do rm "$i"; done
for i in `find . -name '*.unencrypted'`; do rm "$i"; done
#or i in `find . -name '*.encrypted'`; do echo cp $i `echo $i|perl -pe 's/.encrypted//g'`; done

echo git add commit push
git config --global user.email "mark.brito@gmail.com"; git config --global user.name "Mark Brito"; git pull; git add .; git commit -m 'Commit'; git push
