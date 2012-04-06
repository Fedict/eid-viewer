# $Id$
# Authority: dag

Summary: Belgium electronic identity card viewer
Name: eid-viewer
Version: 4.0.4
Release: 0.%{revision}%{?dist}
License: LGPL
Group: Applications/Communications
URL: http://eid.belgium.be/

Source0: http://eidfiles.be/continuous/sources/eid-viewer-4.0.4-%{revision}.src.tar.gz
Source1: eid-viewer.png
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

Obsoletes: beid-mw
Obsoletes: eid-belgium
Obsoletes: eid-mw-viewer

BuildRequires: desktop-file-utils
Requires: java-1.6.0-openjdk
Requires: pcsc-lite
Requires: ccid

%description
The eid-viewer application allows the user to read out any information from
a Belgian electronic identity card. Both identity information and information
about the stored cryptographic keys can be read in a user-friendly manner,
and can easily be printed out or stored for later reviewal.

The application verifies the signature of the identity information,
checks whether it was signed by a government-issued key, and optionally
checks the certificate against the government's Trust Service.

%prep
%setup

%{__cat} <<EOF >eid-viewer.sh
#!/bin/bash
java -jar %{_datadir}/eid-viewer/eid-viewer.jar
EOF

%{__cat} <<EOF >eid-viewer.desktop
[Desktop Entry]
Encoding=UTF-8
Name=eID Viewer
Comment=Display and administer your eID card
Name[nl]=eID Kaart Lezer
Comment[nl]=Weergeven en beheren van uw eID kaart
Name[fr]=Lecteur de Carte eID
Comment[fr]=Affichage et gestion de votre carte eID
GenericName=eid-viewer
Exec=%{_bindir}/eid-viewer
Terminal=false
Type=Application
Icon=eid-viewer
Categories=Application;Utility;
EOF

%build

%install
%{__rm} -rf %{buildroot}

%{__install} -Dp -m0755 eid-viewer.sh %{buildroot}%{_bindir}/eid-viewer
%{__install} -d -m0755 %{buildroot}%{_datadir}/eid-viewer/
%{__cp} -av *.jar %{buildroot}%{_datadir}/eid-viewer/

%{__install} -Dp -m0644 %{SOURCE1} %{buildroot}%{_datadir}/icons/eid-viewer.png
%{__install} -d -m0755 %{buildroot}%{_datadir}/applications/
desktop-file-install --dir %{buildroot}%{_datadir}/applications --vendor fedict eid-viewer.desktop

%clean
%{__rm} -rf %{buildroot}

%post
/sbin/ldconfig

### openct and pcscd are mutual exclusive and we need pcscd for eID Middleware.
### Not nice but if people need the eID Middleware, this is what is required !
if /sbin/service openct status &>/dev/null; then
    /sbin/service openct stop || :
fi

### Disable openct on boot during first install only !
if (( $1 == 1 )) && /sbin/chkconfig --list | grep -qP '^openct\s.+\s3:on\s'; then
    echo "WARNING: The openct service is now disabled on boot." >&2
    /sbin/chkconfig openct off
fi

### Make sure pcscd is enabled and make pcscd reread configuration and rescan USB bus.
if /sbin/service pcscd status &>/dev/null; then
    /usr/sbin/pcscd -H &>/dev/null || :
elif /sbin/chkconfig --list | grep -qP '^pcscd\s'; then
    /sbin/service pcscd start || :
else
    echo "ERROR: Your pcscd installation is seriously broken." >&2
    exit 1
fi

### Enable pcscd on boot during first install only !
if (( $1 == 1 )) && /sbin/chkconfig --list | grep -qP '^pcscd\s.+\s3:off\s'; then
    echo "INFO: The pcscd service is now enabled on boot." >&2
    /sbin/chkconfig pcscd on
fi

### Notify user if an action is required for the eID plugin to work.
if /usr/bin/pgrep 'firefox' &>/dev/null; then
    echo "INFO: You may have to restart Firefox for the Belgium eID add-on to work." >&2
elif /usr/bin/pgrep 'iceweasel' &>/dev/null; then
    echo "INFO: You may have to restart Iceweasel for the Belgium eID add-on to work." >&2
fi

%postun
/sbin/ldconfig

### Make pcscd reread configuration and rescan USB bus.
if /sbin/service pcscd status &>/dev/null; then
    %{_sbindir}/pcscd -H &>/dev/null || :
fi

%files
%defattr(-, root, root, 0755)
### FIXME: Include a man-page about eid-viewer
#%doc %{_mandir}/man8/eid-viewer.8*
%{_bindir}/eid-viewer
%{_datadir}/applications/fedict-eid-viewer.desktop
%{_datadir}/eid-viewer/
%{_datadir}/icons/eid-viewer.png

%changelog
* Thu Apr 5 2012 Frank Marien <frank@apsu.be> - 4.0.4-0.R
- Upgrade to 4.0.4

* Wed Mar 14 2012 Frank Marien <frank@apsu.be> - 4.0.2-0.R
- Upgrade to 4.0.2

* Mon Mar 21 2011 Frank Marien <frank@apsu.be> - 4.0.0-0.R
- Dynamic Revision for continuous builds

* Thu Mar 17 2011 Dag Wieers <dag@wieers.com> - 4.0.0-0.6
- Split eid-mw and eid-viewer packages.

* Thu Feb 24 2011 Dag Wieers <dag@wieers.com> - 4.0.0-0.5
- Added post-install script and desktop file.

* Thu Feb 24 2011 Dag Wieers <dag@wieers.com> - 4.0.0-0.4
- Included pre-built JAR files.

* Wed Feb 23 2011 Dag Wieers <dag@wieers.com> - 4.0.0-0.3
- Added patched eid-applet core.

* Sun Feb 13 2011 Dag Wieers <dag@wieers.com> - 4.0.0-0.2
- Included eid-viewer build using maven.

* Mon Feb  7 2011 Dag Wieers <dag@wieers.com> - 4.0.0-0.1
- Initial package.

