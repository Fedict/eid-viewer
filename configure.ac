AC_PREREQ([2.61])
AC_INIT([eid-viewer],m4_esyscmd([./eid-viewer-version.sh]),[servicedesk@fedict.be])
AC_CONFIG_SRCDIR([pom.xml])
AC_CANONICAL_HOST
AM_INIT_AUTOMAKE([1.9 foreign tar-pax])

AC_MSG_CHECKING([pre-built jar])
AC_ARG_WITH([jar],
AC_HELP_STRING([--with-jar=DIR],[pre-built jar path]),
[],
[with_jar=no])
AC_MSG_RESULT([$with_jar])

AS_VAR_IF([with_jar],[no],
[
	AC_CHECK_PROGS(JAVAC,[javac],[missing])
	AS_VAR_IF([JAVAC],[missing],AC_MSG_ERROR([Java compiler is required.]),AC_SUBST(JAVAC))

	AC_CHECK_PROGS(MVN,[mvn],[missing])
	AS_VAR_IF([MVN],[missing],AC_MSG_ERROR([Maven2 or higher is required.]),AC_SUBST(MVN))
],
[
	AC_MSG_WARN([working from pre-built jar])
])

AM_CONDITIONAL(PREBUILT,test x$with_jar != xno)
AC_SUBST([PREBUILT_PATH],[$with_jar])
AM_CONDITIONAL(COMPILE,test x$with_jar = xno)

AC_CONFIG_FILES([Makefile
		eid-viewer.desktop.sh
        	eid-viewer.sh])
AC_OUTPUT
