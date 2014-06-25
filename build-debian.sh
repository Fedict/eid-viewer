#!/bin/bash

SVNREV=$(cat svn_revision)
MAINVER=$(./configure --version|grep ^eid-viewer|cut -d' ' -f3 | cut -d "-" -f 1)
DIST=$(lsb_release -c -s)

if [ "$MAINVER" == "0.0.0" ]; then 
	echo "TRUNK"
	yes | dch -b -v ${MAINVER}r${SVNREV}-0${DIST}1 -D ${DIST} "Snapshot build"
else
	echo "BRANCH"
	yes | dch -v ${MAINVER}r${SVNREV}-0${DIST}1 -D${DIST} "Release build"
fi

# Build
#make distclean
debuild -uc -us -i -I.svn -b

