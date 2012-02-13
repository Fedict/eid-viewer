/*
 * eID Middleware Project.
 * Copyright (C) 2010-2012 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see
 * http://www.gnu.org/licenses/.
 */
package be.fedict.eidviewer.lib.file.helper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Frank Marien
 */
public class Version35PrefsFile
{
    private static final    Logger logger=Logger.getLogger(Version35PrefsFile.class.getName());
    private                 File   prefsFile=null;
    
    public Version35PrefsFile()
    {
        String osName = System.getProperty("os.name");
        if(osName.startsWith("Linux"))
		{
            logger.finest("Using GNU/Linux style config file");
            prefsFile=getGNULinuxConfigFile();
		}
		else if(osName.startsWith("Mac"))
		{
			logger.finest("Using OSX style config file");
            prefsFile=getDarwinConfigFile();
		}
    }
    
    /*
     * write one single property in one single section. Overwrites entire file with just those.
     * this is arguably an incomplete implementation, but sufficient to write the user's language setting,
     * which it it's only application today.
     * If it gets more complex or we need to maintain existing contents, we'll learn
     * http://commons.apache.org/configuration/apidocs/org/apache/commons/configuration/HierarchicalINIConfiguration.html
     * instead.
     */
    public boolean writeString(String key,String valueName,String value)
	{
		logger.log(Level.FINEST,"WriteString {0} {1} {2}",new Object[]{key,valueName,value});
        
        if(prefsFile==null)
        {
            logger.severe("Preferences Not Available");
            return false;
        }
        
        try
        {
            prefsFile.createNewFile();
            PrintWriter writer=new PrintWriter(prefsFile, "UTF-8");
                        writer.printf("[%s]\n",key);  
                        writer.printf("%s=%s\n",valueName,value);
                        writer.flush();
                        writer.close();
            return true;
                        
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Can't write to Preferences File", ex);
        }        
       
        return false;
    }
    
    private File getDarwinConfigFile()
    {
        return getPersonalConfigFile("Library/Preferences");
    }
    
    private File getGNULinuxConfigFile()
    {
        return getPersonalConfigFile(".config");
    }

    private File getPersonalConfigFile(String personalConfigPath)
    {
        File homeDir=new File(System.getenv("HOME"));
        if(homeDir.isDirectory())
        {
            try
            {
                logger.log(Level.FINEST, "Home Directory found as [{0}]", homeDir.getCanonicalPath());
                File configDir=new File(homeDir,personalConfigPath);
                if(configDir.isDirectory() || configDir.mkdir())
                {
                    logger.log(Level.FINEST, "Config directory found or created as [{0}]", configDir.getCanonicalPath());
                    return new File(configDir,"beid.conf");
                }
                else
                {
                    logger.log(Level.SEVERE, "Can''t use or create [{0}] directory", personalConfigPath);
                }
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, "Can't work with [" + personalConfigPath + "] directory", ex);
            }
        }
        
        return null;
    }
}
