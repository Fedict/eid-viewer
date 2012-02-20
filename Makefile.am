if COMPILE
#########

EXTRA_DIST = pom.xml eid-viewer-platforms eid-viewer-package eid-viewer-lib eid-viewer-gui debian rpm build-debian.sh svn_revision README
BUILT_SOURCES=svn_revision

svn_revision:
	svnversion > svn_revision

all: pom.xml eid-viewer-package eid-viewer-lib eid-viewer-gui
	cd $(srcdir) ; chmod -R u+w eid-viewer-package eid-viewer-lib eid-viewer-gui
	cd $(srcdir) ; echo "version=4.1.0-`cat svn_revision`" > eid-viewer-gui/src/main/resources/be/fedict/eidviewer/gui/resources/Version.properties
	cd $(srcdir) ; $(MVN) -e clean install
#	cd $(srcdir) ; $(ZIP) -j eid-viewer-package/target/package-4.1.0-SNAPSHOT.one-jar.jar eid-viewer-package/src/main/assembly/xkms.wsdl

clean-local:
	rm -f eid-viewer.desktop ; cd $(srcdir) ; $(MVN) clean 

install-data-local:
	mkdir -p $(DESTDIR)$(datadir) $(DESTDIR)$(bindir)
	cd $(srcdir) ; cp -r eid-viewer-package/target/package-4.1.0-SNAPSHOT/eid-viewer $(DESTDIR)$(datadir)/eid-viewer
	chmod -R 0755 $(DESTDIR)$(datadir)/eid-viewer/
	ln -sf $(datadir)/eid-viewer/eid-viewer.sh $(DESTDIR)$(bindir)/eid-viewer

#####
endif



if PREBUILT
###########

all: 
	cd $(srcdir) ; cp -r $(PREBUILT_PATH) .
	cd $(srcdir) ; $(TAR) xvfz *.tar.gz
	cd $(srcdir) ; rm -Rf eid-viewer-4.0.0/eid-viewer.exe

clean-local:
	cd $(srcdir) ; rm -Rf eid-viewer-4.0.0* 

install-data-local:
	mkdir -p $(DESTDIR)$(datadir) $(DESTDIR)$(bindir)
	cd $(srcdir) ; cp -r eid-viewer-4.0.0/ $(DESTDIR)$(datadir)/eid-viewer
	chmod -R 0755 $(DESTDIR)$(datadir)/eid-viewer/
	ln -sf $(datadir)/eid-viewer/eid-viewer.sh $(DESTDIR)$(bindir)/eid-viewer

###########
endif

#######################################################

eid-viewer.desktop: eid-viewer.desktop.sh
	sh eid-viewer.desktop.sh

desktopdir=@datadir@/applications
icondir=@datadir@/icons
desktop_DATA=eid-viewer.desktop
icon_DATA=rpm/eid-viewer.png

uninstall-local:
	rm -Rf $(DESTDIR)$(bindir)/eid-viewer
	rm -Rf $(DESTDIR)$(datadir)/eid-viewer
