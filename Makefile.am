if COMPILE
#########

EXTRA_DIST = pom.xml eid-viewer-win32 eid-viewer-osx eid-viewer-lib eid-viewer-gui debian rpm build-debian.sh svn_revision README
BUILT_SOURCES=svn_revision

svn_revision:
	svnversion > svn_revision

all: pom.xml eid-viewer-win32 eid-viewer-osx eid-viewer-lib eid-viewer-gui eid-viewer.desktop eid-viewer
	cd $(srcdir) && chmod -R u+w eid-viewer-*
	cd $(srcdir) && $(MVN) -e -Dproject.revision=`cat svn_revision` clean install

clean-local:
	rm -f eid-viewer.desktop eid-viewer && cd $(srcdir) && $(MVN) clean 

install-data-local:
	mkdir -p $(DESTDIR)$(datadir)/eid-viewer $(DESTDIR)$(bindir)
	cd $(srcdir) && cp -r eid-viewer-gui/target/eid-viewer-gui-*.jar $(DESTDIR)$(datadir)/eid-viewer
	cp eid-viewer $(DESTDIR)$(bindir)/eid-viewer
	chmod -R 0755 $(DESTDIR)$(datadir)/eid-viewer/
	chmod -R 0755 $(DESTDIR)$(bindir)/eid-viewer

#####
endif



if PREBUILT
###########

all: 
	cd $(srcdir) && cp -r $(PREBUILT_PATH) .

clean-local:
	cd $(srcdir) && rm eid-viewer.desktop eid-viewer -Rf eid-viewer-gui-*.jar

install-data-local:
	mkdir -p $(DESTDIR)$(datadir)/eid-viewer $(DESTDIR)$(bindir)
	cd $(srcdir) && cp eid-viewer-*.jar $(DESTDIR)$(datadir)/eid-viewer
	cp eid-viewer $(DESTDIR)$(bindir)/eid-viewer
	chmod -R 0755 $(DESTDIR)$(datadir)/eid-viewer/
	chmod -R 0755 $(DESTDIR)$(bindir)/eid-viewer

###########
endif

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
