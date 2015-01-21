# This configure/make install provision to facilitate GNU/Linux packaging
#
# to create a source tarball from svn checkout:
# place "eid-viewer.jar" in $(srcdir)=same dir as this file (get it from the maven2 repo)
# autoreconf -i
# ./configure
# make dist
#
# to install from source tarball:
# ./configure
# sudo make install
#
# (This is how distro package builders use the source tarball as well)
#
######################################################################################################################

EXTRA_DIST = eid-viewer.jar debian rpm build-debian.sh svn_revision README

clean-local:
	rm -f eid-viewer.desktop eid-viewer eid-viewer.jar

install-data-local: eid-viewer eid-viewer.desktop eid-viewer.jar
	mkdir -p $(DESTDIR)$(datadir)/eid-viewer $(DESTDIR)$(bindir)
	cp $(srcdir)/eid-viewer.jar $(DESTDIR)$(datadir)/eid-viewer
	cp eid-viewer $(DESTDIR)$(bindir)/eid-viewer
	chmod -R 0755 $(DESTDIR)$(datadir)/eid-viewer/
	chmod -R 0755 $(DESTDIR)$(bindir)/eid-viewer

#######################################################################################################################

svn_revision:
	git describe > svn_revision

eid-viewer.desktop: eid-viewer.desktop.sh
	sh eid-viewer.desktop.sh

eid-viewer: eid-viewer.sh
	sh eid-viewer.sh

desktopdir=@datadir@/applications
icondir=@datadir@/icons
desktop_DATA=eid-viewer.desktop
icon_DATA=rpm/eid-viewer.png

uninstall-local:
	rm -Rf $(DESTDIR)$(bindir)/eid-viewer
	rm -Rf $(DESTDIR)$(datadir)/eid-viewer
