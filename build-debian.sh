#!/bin/bash

SVNREV=$(cat svn_revision)
MAINVER=$(./configure --version|grep ^eid-viewer|cut -d' ' -f3 | cut -d "-" -f 1)

if [ "$MAINVER" == "0.0.0" ]; then 
	echo "TRUNK"
	dch -b -v ${MAINVER}r${SVNREV} "Snapshot build"
else
	echo "BRANCH"
	dch -v ${MAINVER}r${SVNREV} "Release build"
fi

# Build
#make distclean
debuild -uc -us -i -I.svn -b

