#!/bin/sh

SVNREV=$(cat svn_revision)
MAINVER=$(./configure --version|grep ^eid-mw|cut -d' ' -f3)

# Generate changelog entry with correct version number
dch -v ${MAINVER}r${SVNREV} "Snapshot build"

# Build
make distclean
debuild -uc -us -i -I.svn -b

