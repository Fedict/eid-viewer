/*
 * eID Middleware Project.
 * Copyright (C) 2010-2011 FedICT.
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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Frank Marien
 */

public class ProxyUtils
{
    private static final Logger logger              = Logger.getLogger(ProxyUtils.class.getName());
    private static final String USE_SYSTEM_PROXIES  = "java.net.useSystemProxies";
    private static final String PROXY_TEST_URL      = "http://trust-ws.services.belgium.be/eid-trust-service-ws/xkms2";
    private static final Proxy  systemProxy         =determineSystemProxy(PROXY_TEST_URL);
    
    public static Proxy getSystemProxy()
    {
        if(systemProxy==null)
            return Proxy.NO_PROXY;
        return systemProxy;
    }

    public static String getHostName(Proxy proxy)
    {
        InetSocketAddress address=(InetSocketAddress) proxy.address();
        return address.getHostName();
    }
    
    public static int getPort(Proxy proxy)
    {
        InetSocketAddress address=(InetSocketAddress) proxy.address();
        return address.getPort();
    }
    
    private static Proxy determineSystemProxy(String forURL)
    {
        Proxy newProxy=Proxy.NO_PROXY;
        
        logger.log(Level.FINEST, "Determining System Proxy For[{0}]", forURL);
        
        final String savedProxySetting = System.getProperty(USE_SYSTEM_PROXIES);
        logger.finest("Saved Original useSystemProxies Setting");

        try
        {
            logger.finest("Temporarily Enabling useSystemProxies");
            System.setProperty(USE_SYSTEM_PROXIES,"true");
            logger.log(Level.FINEST, "using default ProxySelector on [{0}]", forURL);
            List<Proxy> availableProxies=ProxySelector.getDefault().select(new java.net.URI(PROXY_TEST_URL));
            logger.log(Level.FINEST, "Default ProxySelector returned [{0}] Proxy Objects", availableProxies.size());
            
            logger.finest("Finding HTTP Proxies");
            for(Proxy proxy : availableProxies)             // try HTTP proxies
            {
                logger.log(Level.FINEST, "Checking Out [{0}]", proxy.toString());
                if(proxy.type().equals(Proxy.Type.HTTP))
                {
                    logger.log(Level.FINEST, "Found HTTP Connection [{0}]", proxy.toString());
                    newProxy=proxy;
                    break;
                }
            }
   
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING,"Cannot Determine System HTTP Proxy", e);
        }
        finally
        {
            if(savedProxySetting!=null)
            {
                logger.finest("Restoring Enabling useSystemProxies");
                System.setProperty(USE_SYSTEM_PROXIES, savedProxySetting);
            }
        }

        return newProxy;
    }
}
