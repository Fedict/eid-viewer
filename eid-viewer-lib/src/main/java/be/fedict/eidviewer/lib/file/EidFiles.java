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

package be.fedict.eidviewer.lib.file;

import be.fedict.eidviewer.lib.file.imports.Version35EidFile;
import be.fedict.eidviewer.lib.file.imports.Version35XMLFile;
import be.fedict.eidviewer.lib.file.imports.Version35CSVFile;
import be.fedict.eidviewer.lib.file.imports.EidQuickKeyXMLFile;
import be.fedict.eidviewer.lib.EidData;
import be.fedict.eidviewer.lib.X509CertificateChainAndTrust;
import be.fedict.trust.client.TrustServiceDomains;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Frank Marien
 */
public class EidFiles
{
    private static final Logger logger = Logger.getLogger(EidFiles.class.getName());
   
    public static void loadFromFile(File file, EidData eidData) throws Exception
    {
        try
        {
            if(file.getName().toLowerCase().endsWith(".eid"))
            {
        	if(isXMLEidFile(file))
        	{
        	    loadFromXMLFile(file, eidData);
        	}
        	else if(isTLVEidFile(file))
        	{
        	    Version35EidFile.load(file, eidData);
        	}
        	else if(isCSVEidFile(file))
        	{
        	    Version35CSVFile.load(file, eidData);
        	}
            }
            else if(file.getName().toLowerCase().endsWith(".csv") && isCSVEidFile(file))
            {
        	Version35CSVFile.load(file, eidData);
            }
            else if(file.getName().toLowerCase().endsWith(".xml") && isXMLEidFile(file))
            {
                loadFromXMLFile(file, eidData);
            }
        }
        catch (CertificateException ex)
        {
            Logger.getLogger(EidFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(EidFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(EidFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   

    public static void loadFromXMLFile(File file, EidData eidData) throws FileNotFoundException, IOException, Exception
    {
        switch(getXMLFileVersion(file))
        {
            case 4:
            logger.fine("parsing as 4.0 .XML file");
            Version4XMLFile v4File=Version4XMLFile.fromXML(new FileInputStream(file));
            logger.fine("parsed as 4.0 .XML file");
            eidData.setIdentity(v4File.toIdentity());
            eidData.setAddress(v4File.toAddress());
            eidData.setPhoto(v4File.toPhoto());
            List<X509Certificate> authChain=v4File.toAuthChain();
            if(authChain!=null)
                eidData.setAuthCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_AUTH_TRUST_DOMAIN, authChain));
            List<X509Certificate> signChain=v4File.toSignChain();
            if(signChain!=null)
                eidData.setSignCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_NON_REPUDIATION_TRUST_DOMAIN, signChain));
            List<X509Certificate> rrnChain=v4File.toRRNChain();
            if(rrnChain!=null)
                eidData.setRRNCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_NATIONAL_REGISTRY_TRUST_DOMAIN, rrnChain));
            break;
               
            case 3:
            logger.fine("parsing as 3.5.X .XML file");
            Version35XMLFile v35xmlFile = new Version35XMLFile(eidData);
            v35xmlFile.load(file);
            logger.fine("3.5.x XML data loaded ok");
            break;

            case -2:
            logger.fine("parsing as eID Quick Keys Toolset XML file");
            EidQuickKeyXMLFile eidqkxmlFile = new EidQuickKeyXMLFile(eidData);
            eidqkxmlFile.load(file);
            logger.fine("eID Quick Keys Toolset XML data loaded ok");
            break;
            
            case -1:
            logger.severe("Unknown XML format. Ignoring.");
            break;
        }
    }

    public static void saveToXMLFile(OutputStream file, EidData eidData)
    {
        try
        {
            Version4XMLFile version4file=new Version4XMLFile();
                                        version4file.fromIdentityAddressPhotoAndCertificates(   eidData.getIdentity(),eidData.getAddress(),eidData.getPhoto(),
                                                                                                                                                        eidData.getAuthCert(),
                                                                                                                                                                        eidData.getSignCert(),
                                                                                                                                                                        eidData.getCACert(),
                                                                                                                                                                        eidData.getRRNCert(),
                                                                                                                                                                        eidData.getRootCert());
                         Version4XMLFile.toXML(version4file, file);
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Failed To Save To Version 4.x.x XML-Based eID File", ex);
        }
    }
    public static void saveToXMLFile(File file, EidData eidData) {
    	try {
    		saveToXMLFile(new FileOutputStream(file), eidData);
    	} catch (Exception ex) {
    		logger.log(Level.SEVERE, "Failed to save to Version 4.x.x XML-Base eID File", ex);
    	}
    }
   
    public static void saveToCSVFile(File file, EidData eidData)
    {
        try
        {
            Version35CSVFile version3file=new Version35CSVFile(eidData);
                                         version3file.fromIdentityAddressPhotoAndCertificates(  eidData.getIdentity(),eidData.getAddress(),eidData.getPhoto(),
                                                                                                                                                        eidData.getAuthCert(),
                                                                                                                                                        eidData.getSignCert(),
                                                                                                                                                        eidData.getCACert(),
                                                                                                                                                        eidData.getRRNCert(),
                                                                                                                                                        eidData.getRootCert());
           
            Version35CSVFile.toCSV(version3file, new FileOutputStream(file));
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Failed To Save To Version 3.5.x CSV-Based eID File", ex);
        }
    }

    public static int getXMLFileVersion(File file)
    {
        int             version =-1;
        FileInputStream fis     =null;

        try
        {
            if (!file.canRead())
                return 0;

            byte[] buffer = new byte[512];
            fis = new FileInputStream(file);
            fis.read(buffer);
            fis.close();
            String headStr = new String(buffer, "utf-8");
            if (headStr.contains("<eid>"))
            {
                version = 4;
                logger.finest("Found Version 4.x XML file");
            }
            else if (headStr.contains("<beid_card>"))
            {
                version = 3;
                logger.finest("Found Version 3.x.x XML file");
            }
            else if (headStr.contains("<BelPicDirectory>"))
            {
                version = -2;
                logger.finest("Found eID Quick Key Toolset XML file");
            }
            return version;
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Failed to get XML Version", ex);
        }
        finally
        {
            try
            {
                if(fis!=null)
                    fis.close();
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, "Failed to close XML File", ex);
            }
        }

        return version;
    }

    public static int getCSVFileVersion(File file)
    {
        int             version = -1;
        FileInputStream fis     = null;

        try
        {
            if (!file.canRead())
                return 0;

            byte[] buffer = new byte[16];
            fis = new FileInputStream(file);
            fis.read(buffer);
            fis.close();
            String headStr = new String(buffer, "utf-8");
            String[] fields = headStr.split(";");
            if (fields.length >= 2 && fields[1].equalsIgnoreCase("eid"))
            {
                try
                {
                    version = Integer.parseInt(fields[0]);
                }
                catch (NumberFormatException nfe)
                {
                    logger.log(Level.FINE, "CSV File Failed To Parse Version", nfe);
                }
            }
            return version;
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Failed to get determine CSV Version", ex);
        }
        finally
        {
            try
            {
                if(fis!=null)
                    fis.close();
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, "Failed to close CSV file", ex);
            }
        }

        return version;
    }
    
    private static boolean isXMLEidFile(File file)
    {
   	return getXMLFileVersion(file)>0;
    }
    
    private static boolean isCSVEidFile(File file)
    {
   	return getCSVFileVersion(file)>0;
    }

    public static boolean isTLVEidFile(File file)
    {
        FileInputStream fis = null;
        boolean isTLVEid = false;

        if (!file.canRead())
            return false;

        try
        {
            byte[] buffer = new byte[128];
            fis = new FileInputStream(file);
            fis.read(buffer);
            fis.close();
            String headStr = new String(buffer, "utf-8");
            isTLVEid = (buffer[0] == 0 && buffer[1] == 1 && headStr != null && headStr.length() > 0);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Failed to get determine TLV format", ex);
        }
        finally
        {
            try
            {
                if(fis!=null)
                    fis.close();
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, "Failed to close TLV file", ex);
            }
        }

        return isTLVEid;
    }
}
