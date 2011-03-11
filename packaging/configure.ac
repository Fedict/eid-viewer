AC_PREREQ([2.61])
AC_INIT([eid-viewer],[4.0.0],[servicedesk@fedict.be])
AC_CONFIG_SRCDIR([Makefile.am])
AC_CANONICAL_HOST
AM_INIT_AUTOMAKE([1.9 foreign tar-pax])

# Checks for programs.
AC_CHECK_PROGS(TAR,[tar],[missing])
AS_VAR_IF([TAR],[missing],AC_MSG_ERROR([tar archiver is required.]),AC_SUBST(TAR))

linux="no"
osx="no"
win32="no"

AS_CASE([$host],
  [*-*-linux*],[linux=true],
  [*-*-darwin*],[osx=true],
  [*-mingw*|*-winnt*],[win32=true])

AM_CONDITIONAL([LINUX],[test x$linux = xtrue])
AM_CONDITIONAL([OSX],[test x$osx = xtrue])
AM_CONDITIONAL([WIN32],[test x$win32 = xtrue])

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
