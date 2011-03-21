# $Id$
# Authority: dag

Summary: Belgium electronic identity card viewer
Name: eid-viewer
Version: 4.0.0
Release: 0.%{revision}%{?dist}
License: LGPL
Group: Applications/Communications
URL: http://eid.belgium.be/

Source0: http://eidmw.yourict.net/dist/eid-mw/viewer/eid-viewer-%{version}-%{revision}.tar.gz
Source1: eid-viewer.png
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

Obsoletes: beid-mw
Obsoletes: eid-belgium
Obsoletes: eid-mw-viewer

BuildRequires: desktop-file-utils
Requires: eid-mw
Requires: java-1.6.0-openjdk

%description
The eid-viewer application allows the user to read out any information from
a Belgian electronic identity card. Both identity information and information
about the stored cryptographic keys can be read in a user-friendly manner,
and can easily be printed out or stored for later reviewal.

The application verifies the signature of the identity information,
checks whether it was signed by a government-issued key, and optionally
checks the certificate against the government's Certificate Revocation List
(CRL) and/or by using the Online Certificate Status Protocol (OCSP) against
the government's servers.

%prep
%setup

%{__cat} <<EOF >eid-viewer.sh
#!/bin/bash
java -jar %{_datadir}/eid-viewer/eid-viewer-gui-4.0.0-SNAPSHOT.jar
EOF

%{__cat} <<EOF >eid-viewer.desktop
[Desktop Entry]
Encoding=UTF-8
Name=eID Card Reader
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

%configure %{?configureoptions}

%build

%install
%{__rm} -rf %{buildroot}
%{__make} install DESTDIR="%{buildroot}"
%{__install} -Dp -m0644 %{SOURCE1} %{buildroot}%{_datadir}/icons/eid-viewer.png
%{__install} -d -m0755 %{buildroot}%{_datadir}/applications/
desktop-file-install \
    --dir %{buildroot}%{_datadir}/applications \
    eid-viewer.desktop

%clean
%{__rm} -rf %{buildroot}

%files
%defattr(-, root, root, 0755)
%doc README-eid-viewer.txt
### FIXME: Include a man-page about eid-viewer
#%doc %{_mandir}/man8/eid-viewer.8*
%{_bindir}/eid-viewer
%{_datadir}/applications/eid-viewer.desktop
%{_datadir}/eid-viewer/
%{_datadir}/icons/eid-viewer.png

%changelog
* Mon Mar 21 2011 Frank Marien <frank@apsu.be> - 4.0.0-0.R
- Made revision dynamic to allow for continuous build

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

