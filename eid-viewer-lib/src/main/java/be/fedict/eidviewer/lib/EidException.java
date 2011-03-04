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

package be.fedict.eidviewer.lib;

/**
 *
 * @author frank
 */
public class EidException extends RuntimeException
{
    public EidException()
    {
        super();
    }

    public EidException(String message)
    {
        super(message);
    }

    public EidException(String message, Throwable cause)
    {
        super(message,cause);
    }

    public EidException(Throwable cause)
    {
        super(cause);
    }
}
