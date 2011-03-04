/*
 * eID Middleware Project.
 * Copyright (C) 2010 FedICT.
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

package be.fedict.eidviewer.gui.helper;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author Frank Marien
 */
public class ImageUtilities
{
    public static Image getImage(Class clazz, String path)
    {
        return getIcon(clazz,path).getImage();
    }

    public static ImageIcon getIcon(Class clazz, String path)
    {
        return (new ImageIcon(clazz.getResource(path)));
    }
}
