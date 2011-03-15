EXTRA_DIST = pom.xml eid-viewer-package eid-viewer-lib eid-viewer-gui

if COMPILE
#########

all: pom.xml eid-viewer-package eid-viewer-lib eid-viewer-gui
	cd $(srcdir) ; chmod -R u+w eid-viewer-package eid-viewer-lib eid-viewer-gui
	cd $(srcdir) ; $(MVN) -e clean install

clean-local:
	cd $(srcdir) ; $(MVN) clean

install-data-local:
	cd $(srcdir) ; cp -r eid-viewer-package/target/package-4.0.0/eid-viewer $(DESTDIR)$(datadir)
	chmod -R 0755 $(DESTDIR)$(datadir)/eid-viewer/
	ln -sf $(DESTDIR)$(datadir)/eid-viewer/eid-viewer.sh $(DESTDIR)$(bindir)/eid-viewer

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
	cd $(srcdir) ; cp -r eid-viewer-4.0.0/ $(DESTDIR)$(datadir)/eid-viewer
	chmod -R 0755 $(DESTDIR)$(datadir)/eid-viewer/
	ln -sf $(DESTDIR)$(datadir)/eid-viewer/eid-viewer.sh $(DESTDIR)$(bindir)/eid-viewer

###########
endif

#######################################################

uninstall-local:
	rm -Rf $(DESTDIR)$(bindir)/eid-viewer
	rm -Rf $(DESTDIR)$(datadir)/eid-viewer
