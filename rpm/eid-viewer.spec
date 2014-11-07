# $Id$
# Authority: dag

Summary: Belgium electronic identity card viewer
Name: eid-viewer
Version: 4.0.7
Release: 0.%{revision}%{?dist}
License: LGPL
Group: Applications/Communications
URL: http://eid.belgium.be/

Source0: http://dist.eid.belgium.be/continuous/sources/eid-viewer-4.0.7-%{revision}.src.tar.gz
Source1: eid-viewer.png
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

Obsoletes: beid-mw
Obsoletes: eid-belgium
Obsoletes: eid-mw-viewer

BuildRequires: desktop-file-utils
Requires: eid-mw
%if 0%{?suse_version}
Requires: java >= 1.6.0
%else
Requires: java >= 1:1.6.0
%endif
Requires: pcsc-lite
%if 0%{?suse_version}
Requires: pcsc-ccid
%else
Requires: ccid
%endif
Conflicts: openct

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
java -jar %{_datadir}/eid-viewer/eid-viewer.jar '"$@"'
EOF

%if 0%{?suse_version} >= 1302
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
Categories=Utility;Security;
EOF
%else
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
%endif

%build

%install
%{__rm} -rf %{buildroot}

%{__install} -Dp -m0755 eid-viewer.sh %{buildroot}%{_bindir}/eid-viewer
%{__install} -d -m0755 %{buildroot}%{_datadir}/eid-viewer/
%{__cp} -av *.jar %{buildroot}%{_datadir}/eid-viewer/
%{__install} -Dp -m0644 %{SOURCE1} %{buildroot}%{_datadir}/icons/eid-viewer.png
%{__install} -d -m0755 %{buildroot}%{_datadir}/applications/
desktop-file-install --dir %{buildroot}%{_datadir}/applications --vendor fedict eid-viewer.desktop || true

%clean
%{__rm} -rf %{buildroot}

%post
/sbin/ldconfig

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
* Tue Mar 25 2014 Frank Marien <frank@apsu.be> - 4.0.7-0.R
- Upgrade to 4.0.7

* Wed Oct 14 2013 Frank Marien <frank@apsu.be> - 4.0.6-0.R
- Upgrade to 4.0.6

* Wed May 22 2013 Frank Marien <frank@apsu.be> - 4.0.5-0.R
- Upgrade to 4.0.5

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

