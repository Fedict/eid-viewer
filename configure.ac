AC_PREREQ([2.61])
AC_INIT([eid-viewer],[4.0.0],[servicedesk@fedict.be])
AC_CONFIG_SRCDIR([pom.xml])
AC_CANONICAL_HOST
AM_INIT_AUTOMAKE([1.9 foreign tar-pax])

AC_MSG_CHECKING([pre-built jars])
AC_ARG_WITH([jars],
AC_HELP_STRING([--with-jars=DIR],[pre-built jars archive path]),
[],
[with_jars=no])
AC_MSG_RESULT([$with_jars])

AS_VAR_IF([with_jars],[no],
[
	AC_CHECK_PROGS(JAVAC,[javac],[missing])
	AS_VAR_IF([JAVAC],[missing],AC_MSG_ERROR([Java compiler is required.]),AC_SUBST(JAVAC))

	AC_CHECK_PROGS(MVN,[mvn],[missing])
	AS_VAR_IF([MVN],[missing],AC_MSG_ERROR([Maven2 or higher is required.]),AC_SUBST(MVN))
],
[
	AC_CHECK_PROGS(TAR,[tar],[missing])
	AS_VAR_IF([TAR],[missing],AC_MSG_ERROR([TAR Archiver is required]),AC_SUBST(TAR))
	AC_MSG_WARN([working from pre-built jars])
])

AM_CONDITIONAL(PREBUILT,test x$with_jars != xno)
AC_SUBST([PREBUILT_PATH],[$with_jars])
AM_CONDITIONAL(COMPILE,test x$with_jars = xno)

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
