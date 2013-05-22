package be.fedict.eidviewer.lib.file.helper;

import java.io.File;
import java.util.logging.Logger;

/**
 * (Backported from commons-eid) Encapsulate fixes regarding the dynamic loading
 * of the pcsclite library on GNU/Linux Systems. statically call
 * LibJ2PCSCGNULinuxFix.fixNativeLibrary() before using a TerminalFactory.
 * 
 * @author Frank Cornelis
 * @author Frank Marien
 */
public class LibJ2PCSCGNULinuxFix {

    private static final Logger logger = Logger
	    .getLogger(LibJ2PCSCGNULinuxFix.class.getName());

    private static final int PCSC_LIBRARY_VERSION = 1;
    private static final String SMARTCARDIO_LIBRARY_PROPERTY = "sun.security.smartcardio.library";
    private static final String LIBRARY_PATH_PROPERTY = "java.library.path";
    private static final String GNULINUX_OS_PROPERTY_PREFIX = "Linux";
    private static final String PCSC_LIBRARY_NAME = "pcsclite";
    private static final String UBUNTU_MULTILIB_32_PATH = "/lib/i386-linux-gnu";
    private static final String UBUNTU_MULTILIB_64_PATH = "/lib/x86_64-linux-gnu";
    private static final String JRE_BITNESS_PROPERTY = "os.arch";
    private static final String OS_NAME_PROPERTY = "os.name";
    private static final String JRE_BITNESS_32_VALUE = "i386";
    private static final String JRE_BITNESS_64_VALUE = "amd64";

    private static enum UbuntuBitness {
	NA, PURE32, PURE64, MULTILIB
    };

    /**
     * Make sure libpcsclite is found. The libj2pcsc.so from the JRE attempts to
     * dlopen using the linker name "libpcsclite.so" instead of the appropriate
     * "libpcsclite.so.1". This causes libpcsclite not to be found on GNU/Linux
     * distributions that don't have the libpcsclite.so symbolic link. This
     * method finds the library and forces the JRE to use it instead of
     * attempting to locate it by itself. See also:
     * http://bugs.debian.org/cgi-bin/bugreport.cgi?bug=529339
     * 
     * Does nothing if not on a GNU/Linux system
     */

    public static void fixNativeLibrary() {
	final String osName = System.getProperty(OS_NAME_PROPERTY);
	if ((osName != null)
		&& (osName.startsWith(GNULINUX_OS_PROPERTY_PREFIX))) {
	    logger.info("OS is [" + osName + "]. Enabling PCSC library fix.");
	    final File libPcscLite = findGNULinuxNativeLibrary(
		    PCSC_LIBRARY_NAME, PCSC_LIBRARY_VERSION, logger);
	    if (libPcscLite != null) {
		logger.info("Setting [" + SMARTCARDIO_LIBRARY_PROPERTY
			+ "] to [" + libPcscLite.getAbsolutePath() + "]");
		System.setProperty(SMARTCARDIO_LIBRARY_PROPERTY,
			libPcscLite.getAbsolutePath());
	    }
	} else {
	    logger.info("OS is [" + osName
		    + "]. Not Enabling PCSC library fix.");
	}
    }

    // ----------------------------------------------------------------------------------------
    // -------------------------------- supporting private methods.
    // ---------------------------
    // ----------------------------------------------------------------------------------------

    /*
     * Determine Ubuntu-type multilib configuration
     */
    private static UbuntuBitness getUbuntuBitness() {
	boolean has32 = false, has64 = false;
	File multilibdir = new File(UBUNTU_MULTILIB_32_PATH);
	has32 = (multilibdir != null && multilibdir.isDirectory());
	multilibdir = new File(UBUNTU_MULTILIB_64_PATH);
	has64 = (multilibdir != null && multilibdir.isDirectory());

	if (has32 && (!has64)) {
	    return UbuntuBitness.PURE32;
	} else if ((!has32) && has64) {
	    return UbuntuBitness.PURE64;
	} else if (has32 && has64) {
	    return UbuntuBitness.MULTILIB;
	} else {
	    return UbuntuBitness.NA;
	}
    }

    /*
     * return the path with extension appended, if it wasn't already contained
     * in the path
     */
    private static String extendLibraryPath(final String lib_path,
	    final String extension) {
	if (lib_path.contains(extension)) {
	    return lib_path;
	}
	return lib_path + ":" + extension;
    }

    /*
     * Oracle Java 7, java.library.path is severely limited as compared to the
     * OpenJDK default and doesn't contain Ubuntu 12's MULTILIB directories.
     * Test for Ubuntu in various configs and add the required paths
     */
    private static String fixPathForUbuntuMultiLib(final String libraryPath,
	    final Logger logger) {
	logger.fine("Looking for Ubuntu-style multilib installation.");

	switch (getUbuntuBitness()) {
	case PURE32:
	    // pure 32-bit Ubuntu. Add the 32-bit lib dir.
	    logger.fine("pure 32-bit Ubuntu detected, using 32-bit multilib path: "
		    + UBUNTU_MULTILIB_32_PATH);
	    return extendLibraryPath(libraryPath, UBUNTU_MULTILIB_32_PATH);

	case PURE64:
	    // pure 64-bit Ubuntu. Add the 64-bit lib dir.
	    logger.fine("pure 64-bit Ubuntu detected, using 64-bit multilib path: "
		    + UBUNTU_MULTILIB_64_PATH);
	    return extendLibraryPath(libraryPath, UBUNTU_MULTILIB_64_PATH);

	case MULTILIB: {
	    // multilib Ubuntu. Let the currently running JRE's bitness
	    // determine which lib dir to add.
	    logger.fine("Multilib Ubuntu detected. Using JRE Bitness.");

	    final String jvmBinaryArch = System
		    .getProperty(JRE_BITNESS_PROPERTY);
	    if (jvmBinaryArch == null) {
		return libraryPath;
	    }

	    logger.fine("JRE Bitness is [" + jvmBinaryArch + "]");

	    if (jvmBinaryArch.equals(JRE_BITNESS_32_VALUE)) {
		logger.fine("32-bit JRE, using 32-bit multilib path: "
			+ UBUNTU_MULTILIB_32_PATH);
		return extendLibraryPath(libraryPath, UBUNTU_MULTILIB_32_PATH);
	    }

	    if (jvmBinaryArch.equals(JRE_BITNESS_64_VALUE)) {
		logger.fine("64-bit JRE, using 64-bit multilib path: "
			+ UBUNTU_MULTILIB_64_PATH);
		return extendLibraryPath(libraryPath, UBUNTU_MULTILIB_64_PATH);
	    }
	}
	    break;

	default: {
	    logger.fine("Did not find Ubuntu-style multilib.");
	}
	}
	return libraryPath;
    }

    /*
     * Finds .so.version file on GNU/Linux. avoid guessing all GNU/Linux
     * distros' library path configurations on 32 and 64-bit when working around
     * the buggy libj2pcsc.so implementation based on JRE implementations adding
     * the native library paths to the end of java.library.path. Fixes the path
     * for Oracle JRE which doesn't contain the Ubuntu MULTILIB directories
     */
    private static File findGNULinuxNativeLibrary(final String baseName,
	    final int version, final Logger logger) {
	// get java.library.path
	String nativeLibraryPaths = System.getProperty(LIBRARY_PATH_PROPERTY);
	if (nativeLibraryPaths == null) {
	    return null;
	}

	logger.fine("Original Path=[" + nativeLibraryPaths + "]");

	// when on Ubuntu, add appropriate MULTILIB path
	nativeLibraryPaths = fixPathForUbuntuMultiLib(nativeLibraryPaths,
		logger);

	logger.fine("Path after Ubuntu multilib Fixes=[" + nativeLibraryPaths
		+ "]");

	// scan the directories in the path and return the first library called
	// "baseName" with version "version"

	final String libFileName = System.mapLibraryName(baseName) + "."
		+ version;

	logger.fine("Scanning path for [" + libFileName + "]");

	for (String nativeLibraryPath : nativeLibraryPaths.split(":")) {
	    logger.fine("Scanning [" + nativeLibraryPath + "]");
	    final File libraryFile = new File(nativeLibraryPath, libFileName);
	    if (libraryFile.exists()) {
		logger.fine("[" + libFileName + "] found in ["
			+ nativeLibraryPath + "]");
		return libraryFile;
	    }
	}

	logger.fine("[" + libFileName + "] not found.");
	return null;
    }
}
