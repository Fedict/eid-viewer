#!/bin/sh

VERSIONFILE="eid-viewer-version"

if [ -f "$VERSIONFILE" ]; then
    cat $VERSIONFILE
else
    VERSION=`svn info | grep ^URL | cut -d " " -f 2 | awk -F/ '{print $NF}'`
    REVISION=`svnversion`
    if [ "$VERSION" == "trunk" ]; then VERSION="0.0.0"; fi
    echo -n $VERSION | tee $VERSIONFILE
    echo -n $REVISION > svn_revision
fi

