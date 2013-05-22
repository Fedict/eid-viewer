/*
 * eID Middleware Project.
 * Copyright (C) 2010-2013 FedICT.
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

import java.util.Locale;
import java.util.logging.Logger;

/**
 *
 * @author Frank Marien
 */
public class Version35LocalePrefs
{
	private static final Logger logger=Logger.getLogger(Version35LocalePrefs.class.getName());

	public static boolean writeUserLocaleChoice(Locale locale)
	{
		String osName = System.getProperty("os.name");
        if(osName.startsWith("Windows"))
		{
			logger.finest("Writing prefs in Registry MS Windows style");
			UserRegistryWriter userRegistryWriter=new UserRegistryWriter();
			return userRegistryWriter.writeString("Software\\BEID\\general","language",locale.getLanguage());
		}
        else
		{
			logger.finest("Writing prefs file Unix-style");
            Version35PrefsFile version35PrefsFile=new Version35PrefsFile();
                               version35PrefsFile.writeString("general","language",locale.getLanguage());
		}

		return false;
	}
}
