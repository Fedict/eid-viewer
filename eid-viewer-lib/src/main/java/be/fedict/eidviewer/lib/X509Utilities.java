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

package be.fedict.eidviewer.lib;

import be.fedict.trust.client.TrustServiceDomains;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Signature;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.openssl.PEMWriter;

/**
 *
 * @author Frank Marien
 */
public class X509Utilities
{
    private static final Logger logger=Logger.getLogger(X509Utilities.class.getName());
    private static final int            CONSTRAINT_DIGITALSIGNATURE=0;
    private static final int 			CONSTRAINT_NONREPUDIATION = 1;
    private static final List<String>   keyUsageStringNames;
	

    static
    {
        keyUsageStringNames=new ArrayList<String>(9);
        keyUsageStringNames.add("constraint_digitalSignature");
        keyUsageStringNames.add("constraint_nonRepudiation");
        keyUsageStringNames.add("constraint_keyEncipherment");
        keyUsageStringNames.add("constraint_dataEncipherment");
        keyUsageStringNames.add("constraint_keyAgreement");
        keyUsageStringNames.add("constraint_keyCertSign");
        keyUsageStringNames.add("constraint_cRLSignKey");
        keyUsageStringNames.add("constraint_encipherOnly");
        keyUsageStringNames.add("constraint_decipherOnly");
    }
    
    public static boolean isSelfSigned(X509Certificate certificate)
    {
        return certificate.getIssuerDN().equals(certificate.getSubjectDN());
    }

    public static boolean isCertificateAuthority(X509Certificate certificate)
    {
        return (certificate.getBasicConstraints()!=-1) && isSelfSigned(certificate);
    }

    public static String getCN(X509Certificate certificate)
    {
        String[] dn=certificate.getSubjectDN().getName().split("\\s*,\\s*");
        for(String dnPart : dn)
        {
            String[] labelValue=dnPart.trim().split("=");
            if(labelValue.length==2 && labelValue[0].equalsIgnoreCase("CN"))
                return labelValue[1].trim();
        }
        return null;
    }
    
    public static String getHumanReadableName(X509Certificate certificate)
    {
    	String commonName=getCN(certificate);
    	ResourceBundle bundle=ResourceBundle.getBundle("be/fedict/eidviewer/lib/resources/X509Utilities");
    	if(bundle!=null)
    	{
    		try
    		{
    			return bundle.getString(commonName);
    		}
    		catch(MissingResourceException mre)
    		{
    			// intentionally empty ; will return commonName below
    		}
    	}
    	
    	return commonName;
    }

    public static List<String> getKeyUsageStrings(ResourceBundle bundle, boolean[] keyUsage)
    {
        List<String> uses=new ArrayList<String>(9);
        for(int i=0;i<keyUsage.length;i++)
            if(keyUsage[i])
                uses.add(bundle.getString(keyUsageStringNames.get(i)));
        return uses;
    }

    public static boolean hasDigitalSignatureConstraint(X509Certificate certificate)
    {
        return certificate.getKeyUsage()[CONSTRAINT_DIGITALSIGNATURE];
    }
    
    public static boolean hasNonRepudiationConstraint(X509Certificate certificate)
    {
        return certificate.getKeyUsage()[CONSTRAINT_NONREPUDIATION];
    }
    
    
    
    public static void setCertificateChainsFromCertificates(EidData eidData, X509Certificate rootCert, X509Certificate citizenCert, X509Certificate authenticationCert, X509Certificate signingCert, X509Certificate rrnCert)
    {
        if(rootCert != null && citizenCert != null)
        {
            if (authenticationCert != null)
            {
                logger.fine("Setting Authentication Certificate Chain");
                List<X509Certificate> authChain = new LinkedList<X509Certificate>();
                authChain.add(authenticationCert);
                authChain.add(citizenCert);
                authChain.add(rootCert);
                eidData.setAuthCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_AUTH_TRUST_DOMAIN, authChain));
            }

            if (signingCert != null)
            {
                logger.fine("Setting Signing Certificate Chain");
                List<X509Certificate> signChain = new LinkedList<X509Certificate>();
                signChain.add(signingCert);
                signChain.add(citizenCert);
                signChain.add(rootCert);
                eidData.setSignCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_NON_REPUDIATION_TRUST_DOMAIN, signChain));
            }

            if (rrnCert != null)
            {
                logger.fine("Setting RRN Certificate Chain");
                List<X509Certificate> rrnChain = new LinkedList<X509Certificate>();
                rrnChain.add(rrnCert);
                rrnChain.add(rootCert);
                eidData.setRRNCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_NATIONAL_REGISTRY_TRUST_DOMAIN, rrnChain));
            }
        }
    }
    
    public static boolean isValidSignature(X509Certificate certificate, byte[] data, byte[] data2, byte[] signature )
    {
        try
        {
            Signature   verifier = Signature.getInstance("SHA1withRSA");
                        verifier.initVerify(certificate);
                        verifier.update(data);
                        if(data2!=null)
                            verifier.update(data2);      
                 return verifier.verify(signature);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static boolean isValidSignature(X509Certificate certificate, byte[] data, byte[] signature )
    {
        return isValidSignature(certificate, data, null, signature);
    }
    
    public static void certificateToDERFile(X509Certificate certificate, File file) throws CertificateEncodingException, IOException
    {
    	FileOutputStream outputStream=new FileOutputStream(file);
    	outputStream.write(certificate.getEncoded());
    }
    
    public static void certificateToPEMFile(X509Certificate certificate, File file) throws CertificateEncodingException, IOException
    {
    	FileOutputStream outputStream=new FileOutputStream(file);
    	PEMWriter pemWriter=new PEMWriter(new OutputStreamWriter(outputStream));
    			  pemWriter.writeObject(certificate);
    			  pemWriter.close();
    }

	public static void certificateChainToPEMFile(List<X509Certificate> certificates, File file) throws IOException
	{
		FileOutputStream outputStream=new FileOutputStream(file);
		PEMWriter pemWriter=new PEMWriter(new OutputStreamWriter(outputStream));
		for(X509Certificate certificate : certificates)
			pemWriter.writeObject(certificate);
    	pemWriter.close();
	}
}
