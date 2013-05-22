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

package be.fedict.eidviewer.lib.file.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Frank Marien
 */
public class CertFileFilter extends FileFilter
{
    private boolean acceptPEM,acceptDER;
    private String  description;

    public CertFileFilter(boolean acceptPEM, boolean acceptDER, String description)
    {
        super();
        this.acceptPEM =    acceptPEM;
        this.acceptDER =    acceptDER;
        this.description =  description;
    }

    public boolean accept(File file)
    {
        if(file.isDirectory())
            return true;
        String lowerCaseName=file.getName().toLowerCase();
        if(acceptPEM && lowerCaseName.endsWith(".pem"))
        	return true;
        if(acceptDER && lowerCaseName.endsWith(".der"))
        	return true;
        return false;
    }

    public String getDescription()
    {
        return description;
    }
}
