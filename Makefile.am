# This configure/make install provision to facilitate GNU/Linux packaging
# place "eid-viewer.jar" in $(srcdir)=same dir as this file.
# ./configure && make install will install it, launcher script and desktop file
#
# autoreconf -i (only if when checked out from svn)
# ./configure
# sudo make install

#######################################################

clean-local:
	rm -f eid-viewer.desktop eid-viewer eid-viewer.jar

install-data-local: eid-viewer eid-viewer.desktop eid-viewer.jar
	mkdir -p $(DESTDIR)$(datadir)/eid-viewer $(DESTDIR)$(bindir)
	cp eid-viewer.jar $(DESTDIR)$(datadir)/eid-viewer
	cp eid-viewer $(DESTDIR)$(bindir)/eid-viewer
	chmod -R 0755 $(DESTDIR)$(datadir)/eid-viewer/
	chmod -R 0755 $(DESTDIR)$(bindir)/eid-viewer

#######################################################

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
