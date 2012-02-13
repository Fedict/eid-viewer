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

package be.fedict.eidviewer.gui.helper;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

/**
 *
 * @author Frank Marien
 */
public class ImageUtilities
{
    public static Image getImage(Class<?> clazz, String path)
    {
        return getIcon(clazz,path).getImage();
    }

    public static ImageIcon getIcon(Class<?> clazz, String path)
    {
        return (new ImageIcon(clazz.getResource(path)));
    }
    
    public static Border getEIDBorder()
    {
        return BorderFactory.createLineBorder(new java.awt.Color(204, 255, 204), 24);
    }
    
    public static int getTotalStringWidth(Graphics2D graphics2D, String[] strings)
    {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int widest = 0;
        for (int i = 0; i < strings.length; i++)
        {
            int width = fontMetrics.stringWidth(strings[i]);
            if (width > widest)
                widest = width;
        }
        return widest;
    }

    public static int getStringWidth(Graphics2D graphics2D, String string)
    {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        return fontMetrics.stringWidth(string);
    }

    public static float getTotalStringHeight(Graphics2D graphics2D, String[] strings)
    {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        return fontMetrics.getHeight() * strings.length;
    }

    public  static float getStringHeight(Graphics2D graphics2D)
    {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        return fontMetrics.getHeight();
    }

    public static float getAscent(Graphics2D graphics2D)
    {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        return fontMetrics.getAscent();
    }
}
