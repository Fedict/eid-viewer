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

package be.fedict.eidviewer.gui;

import be.fedict.eidviewer.gui.helper.ProxyUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 *
 * @author Frank Marien
 */
public class ViewerPrefs
{
        private static final Logger logger = Logger.getLogger(ViewerPrefs.class.getName());
       
    public static final int             PROXY_DIRECT                     = 0;
    public static final int             PROXY_SYSTEM                     = 1;
    public static final int             PROXY_SPECIFIC                   = 2;
   
    public static final String          AUTO_VALIDATE_TRUST              = "auto_validate_trust";
    public static final String          TRUSTSERVICE_PROTO               = "trust_service_proto";
    public static final String          TRUSTSERVICE_URI                 = "trust_service_uri";
    public static final String          HTTP_PROXY_TYPE                  = "http_proxy_type";
    public static final String          HTTP_PROXY_HOST                  = "http_proxy_host";
    public static final String          HTTP_PROXY_PORT                  = "http_proxy_port";
    public static final String          SHOW_LOG_TAB                     = "show_log_tab";
    public static final String          LOG_LEVEL                        = "log_level";
    public static final String          LOCALE_LANGUAGE                  = "locale_language";
    public static final String          LOCALE_COUNTRY                   = "locale_country";
    public static final String          LAST_SAVE_LOCATION               = "last_save_location";
    public static final String          LAST_OPEN_LOCATION               = "last_open_location";
   
    public static final boolean         DEFAULT_AUTO_VALIDATE_TRUST     = false;

    public static final boolean         DEFAULT_SHOW_LOG_TAB            = false;
    public static final String          DEFAULT_LOG_LEVEL_STR           = "INFO";
    public static final Level           DEFAULT_LOG_LEVEL               = Level.INFO;
   
    public static final String          DEFAULT_TRUSTSERVICE_PROTO      = "https";
    public static final String          DEFAULT_TRUSTSERVICE_URI        = "trust-ws.services.belgium.be/eid-trust-service-ws/xkms2";
    public static final int             DEFAULT_HTTP_PROXY_TYPE         = PROXY_SYSTEM;
    public static final String          DEFAULT_HTTP_PROXY_HOST         = "";
    public static final int             DEFAULT_HTTP_PROXY_PORT         = 8080;
   
    public static final String          DEFAULT_LOCALE_LANGUAGE         = "en";
    public static final String          DEFAULT_LOCALE_COUNTRY          = "US";
    public static final String          DEFAULT_LAST_SAVE_LOCATION      = null; // null = reasonable default: user's home.
    public static final String          DEFAULT_LAST_OPEN_LOCATION      = null; // null = reasonable default: user's home.
   
    private static final String         COLON_SLASH_SLASH               = "://";
   
    private static Preferences          preferences;

    public static Preferences getPrefs()
    {
        if(preferences==null)
            preferences=Preferences.userNodeForPackage(ViewerPrefs.class);
        return preferences;
    }

    public static boolean getIsAutoValidating()
    {
        return getPrefs()!=null?getPrefs().getBoolean(AUTO_VALIDATE_TRUST, DEFAULT_AUTO_VALIDATE_TRUST):DEFAULT_AUTO_VALIDATE_TRUST;
    }

    public static void setAutoValidating(boolean state)
    {
        if(getPrefs()==null)
            return;
        getPrefs().putBoolean(AUTO_VALIDATE_TRUST, state);
    }

    public static String getTrustServiceProto()
    {
        return getPrefs()!=null?getPrefs().get(TRUSTSERVICE_PROTO, DEFAULT_TRUSTSERVICE_PROTO):DEFAULT_TRUSTSERVICE_PROTO;
    }
 
    public static String getTrustServiceURI()
    {
        return getPrefs()!=null?getPrefs().get(TRUSTSERVICE_URI, DEFAULT_TRUSTSERVICE_URI):DEFAULT_TRUSTSERVICE_URI;
    }

    public static String getTrustServiceURL()
    {
        return getTrustServiceProto() + COLON_SLASH_SLASH + getTrustServiceURI();
    }
   
    public static int getProxyType()
    {
        validateProxy();
        return getProxyTypeSet();
    }
   
    public static void setProxyType(int type)
    {
         if(getPrefs()==null)
            return;
        getPrefs().putInt(HTTP_PROXY_TYPE, type);
    }
   
    public static void setSpecificProxyHost(String host)
    {
         if(getPrefs()==null)
            return;
        getPrefs().put(HTTP_PROXY_HOST, host);
    }

    public static String getSpecificProxyHost()
    {
        return getPrefs()!=null?getPrefs().get(HTTP_PROXY_HOST, DEFAULT_HTTP_PROXY_HOST):DEFAULT_HTTP_PROXY_HOST;
    }

     public static void setSpecificProxyPort(int port)
    {
         if(getPrefs()==null)
            return;
        getPrefs().putInt(HTTP_PROXY_PORT, port);
    }

    public static int getSpecificProxyPort()
    {
        return getPrefs()!=null?getPrefs().getInt(HTTP_PROXY_PORT, DEFAULT_HTTP_PROXY_PORT):DEFAULT_HTTP_PROXY_PORT;
    }

    public static boolean getShowLogTab()
    {
        return getPrefs()!=null?getPrefs().getBoolean(SHOW_LOG_TAB, DEFAULT_SHOW_LOG_TAB):DEFAULT_SHOW_LOG_TAB;
    }

    public static void setShowLogTab(boolean state)
    {
        if(getPrefs()==null)
            return;
        getPrefs().putBoolean(SHOW_LOG_TAB, state);
    }

    public static Level getLogLevel()
    {
        return getPrefs()!=null?Level.parse(getPrefs().get(LOG_LEVEL, DEFAULT_LOG_LEVEL_STR)):DEFAULT_LOG_LEVEL;
    }

    public static void setLogLevel(Level level)
    {
        if(getPrefs()==null)
            return;
        getPrefs().put(LOG_LEVEL, level.toString());
    }
   
    public static void setLocale(Locale locale)
    {
         if(getPrefs()==null)
            return;
        getPrefs().put(LOCALE_LANGUAGE, locale.getLanguage());
        getPrefs().put(LOCALE_COUNTRY, locale.getCountry());
    }

    public static Locale getLocale()
    {
        Preferences prefs=getPrefs();
        if(prefs==null)
            return getDefaultLocale();

        String language=prefs.get(LOCALE_LANGUAGE,DEFAULT_LOCALE_LANGUAGE);
        String country=prefs.get(LOCALE_COUNTRY,DEFAULT_LOCALE_COUNTRY);

        if(language==null || country==null)
            return getDefaultLocale();

        if(isLocaleSupported(language, country))
            return new Locale(language,country);
        else
            return Locale.US;
    }

    public static String getFullVersion()
    {
        return "eID Viewer " + getVersion();
    }
   
    public static String getVersion()
    {
        ResourceBundle bundle=ResourceBundle.getBundle("be/fedict/eidviewer/gui/resources/Version");
        return bundle.getString("version");
    }
   
    public static Proxy getProxy()
    {
       validateProxy();
       switch(getProxyType())
       {
           case PROXY_SPECIFIC:
                return new Proxy(Proxy.Type.HTTP,new InetSocketAddress(getSpecificProxyHost(),getSpecificProxyPort()));
           case PROXY_SYSTEM:
               return ProxyUtils.getSystemProxy();    
       }
       return Proxy.NO_PROXY;
    }
   
    public static boolean hasSystemProxy()
    {
        validateProxy();
        Proxy systemProxy=ProxyUtils.getSystemProxy();
        return (systemProxy!=null && systemProxy!=Proxy.NO_PROXY);
    }

    public static int getProxyTypeSet()
    {
        return getPrefs()!=null?getPrefs().getInt(HTTP_PROXY_TYPE, DEFAULT_HTTP_PROXY_TYPE):DEFAULT_HTTP_PROXY_TYPE;
    }
   
    public static File getLastSaveLocation()
    {
        if(getPrefs()==null)
            return null;
        String path=getPrefs().get(LAST_SAVE_LOCATION, DEFAULT_LAST_SAVE_LOCATION);
        if(path==null)
                return null;
        File file=new File(path);
        if(!file.exists())
                return null;
        return file;
    }

    public static void setLastSaveLocation(File file)
    {
        if(getPrefs()==null)
            return;
       
        try
        {
                getPrefs().put(LAST_SAVE_LOCATION, file.getCanonicalPath());
                }
        catch (IOException iox)
        {
                logger.log(Level.SEVERE,"Can't Save Last Open Location",iox);
                }  
    }
   
    public static File getLastOpenLocation()
    {
        if(getPrefs()==null)
            return null;
        String path=getPrefs().get(LAST_OPEN_LOCATION, DEFAULT_LAST_OPEN_LOCATION);
        if(path==null)
                return null;
        File file=new File(path);
        if(!file.exists())
                return null;
        return file;
    }

    public static void setLastOpenLocation(File file)
    {
        if(getPrefs()==null)
            return;
       
        try
        {
                getPrefs().put(LAST_OPEN_LOCATION, file.getCanonicalPath());
                }
        catch (IOException iox)
        {
                logger.log(Level.SEVERE,"Can't Save Last Open Location",iox);
                }  
    }
   
    private static boolean isLocaleSupported(String language, String country)
    {
        String localeStr=language + "_" + country;
        return (localeStr.equals("en_US") || localeStr.equals("nl_BE") || localeStr.equals("fr_BE") || localeStr.equals("de_BE"));
    }

    private static Locale getDefaultLocale()
    {
        String language=System.getProperty("user.language");
        String country=System.getProperty("user.country");
        if(isLocaleSupported(language, country))
            return new Locale(language,country);
        else
            return Locale.US;
    }
   
    private static void validateProxy()
    {
        if(getProxyTypeSet()==PROXY_SYSTEM)
        {
            Proxy systemProxy=ProxyUtils.getSystemProxy();
            if(systemProxy==null || systemProxy==Proxy.NO_PROXY)            
                setProxyType(PROXY_DIRECT);
        }
    }
}

