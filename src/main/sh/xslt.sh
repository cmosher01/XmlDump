#!/bin/sh
if [ $# != 1 ] ; then
  echo "usage: $0 xslt <input.xml >output.xml"
  exit 0
fi
saxonjar=/usr/local/Cellar/saxon/9.9.0.1/libexec/saxon9he.jar
saxon -xmlversion:1.1 -dtd:on -s:- -xsl:$1
