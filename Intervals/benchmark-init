echo Logfile: $1
echo "> date"
date
echo 'git show --pretty=oneline | head -1'
git show --pretty=oneline | head -1
echo "git diff | cat"
git diff | cat
echo "pwd"
pwd

export CLASSPATH=$CLASSPATH:$DIR/bin:$DIR/lib

echo 
echo "Settings:"
echo "JAVAOPTS=${JAVAOPTS}"
echo "NOBUILD=${NOBUILD}"
echo "CLASSPATH=${CLASSPATH}"

if [ ! -n "$NOBUILD" ]; then
	ant
fi
