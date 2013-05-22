package be.fedict.eidviewer.gui.helper;

import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper {
    public static void logJavaSpecs(Logger logger) {
   	Properties properties = System.getProperties();
   	Set<String> labels = properties.stringPropertyNames();
   	for (String label : labels) {
   	    logger.log(Level.INFO, "{0}={1}",
   		    new Object[] { label, properties.getProperty(label) });
   	}
       }

}
