#!/bin/sh
saxonjar=/usr/local/Cellar/saxon/9.9.0.1/libexec/saxon9he.jar
java -cp $saxonjar net.sf.saxon.Query -xmlversion:1.1 -dtd:recover -s:- -qs:/ '!indent=yes' "$@"
