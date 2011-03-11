all: 
	cd $(srcdir) && $(TAR) xvfz eid-viewer-4.0.0-*.tar.gz

clean-local:
	cd $(srcdir) && rm -Rf eid-viewer-4.0.0*

install-data-local:
	cd $(srcdir) && cp -r eid-viewer-4.0.0 $(DESTDIR)$(datadir)/eid-viewer
	chmod -R 755 $(DESTDIR)$(datadir)/eid-viewer/
	ln -sf $(DESTDIR)$(datadir)/eid-viewer/eid-viewer.sh $(DESTDIR)$(bindir)/eid-viewer

uninstall-local:
	rm -Rf $(DESTDIR)$(bindir)/eid-viewer
	rm -Rf $(DESTDIR)$(datadir)/eid-viewer
