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

package be.fedict.eidviewer.lib.file.imports;

import be.fedict.eidviewer.lib.file.helper.TLVEntry;
import be.fedict.eid.applet.service.Address;
import be.fedict.eid.applet.service.Identity;
import be.fedict.eid.applet.service.impl.tlv.TlvParser;
import be.fedict.eidviewer.lib.EidData;
import be.fedict.eidviewer.lib.X509CertificateChainAndTrust;
import be.fedict.eidviewer.lib.file.helper.TextFormatHelper;
import be.fedict.trust.client.TrustServiceDomains;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Frank Marien
 */
public class Version35EidFile
{
    private static final Logger logger=Logger.getLogger(Version35EidFile.class.getName());
    
    private static final byte BEID_TLV_TAG_FILE_ID      = 0x01;
    private static final byte BEID_TLV_TAG_FILE_ADDR    = 0x03;
    private static final byte BEID_TLV_TAG_FILE_PHOTO   = 0x05;
    private static final byte BEID_TLV_TAG_FILE_CERTS   = 0x0C;

    public static void load(File file, EidData eidData) throws CertificateException, FileNotFoundException, IOException
    {
        CertificateFactory certificateFactory = null;
        X509Certificate rootCert = null;
        X509Certificate citizenCert = null;
        X509Certificate authenticationCert = null;
        X509Certificate signingCert = null;
        X509Certificate rrnCert = null;
        
        certificateFactory = CertificateFactory.getInstance("X.509");
        FileInputStream fis = new FileInputStream(file);
        TLVEntry entry;

        while ((entry = TLVEntry.next(fis)) != null)
        {
            logger.log(Level.FINEST, "L1:Type[{0}]:Len[{1}]", new Object[]{entry.tag, entry.length});

            switch (entry.tag)
            {
                case BEID_TLV_TAG_FILE_ID:
                {
                    Identity identity=TlvParser.parse(entry.data, Identity.class);
                    TextFormatHelper.setFirstNamesFromStrings(identity, identity.getFirstName(), identity.getMiddleName());
                    eidData.setIdentity(identity);
                }
                break;

                case BEID_TLV_TAG_FILE_ADDR:
                    eidData.setAddress(TlvParser.parse(entry.data, Address.class));
                    break;

                case BEID_TLV_TAG_FILE_PHOTO:
                    eidData.setPhoto(entry.data);
                    break;

                case BEID_TLV_TAG_FILE_CERTS:
                {
                    TLVEntry certEntry = null;
                    ByteArrayInputStream bis = new ByteArrayInputStream(entry.data);
                    X509Certificate cert = null;

                    while ((certEntry = TLVEntry.next(bis)) != null)
                    {
                        logger.log(Level.FINEST, "L2:Type[{0}]:Len[{1}]", new Object[]{certEntry.tag, certEntry.length});

                        switch (certEntry.tag)
                        {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            {
                                TLVEntry certEntry2 = null;
                                ByteArrayInputStream bis2 = new ByteArrayInputStream(certEntry.data);

                                while ((certEntry2 = TLVEntry.next(bis2)) != null)
                                {
                                    logger.log(Level.FINEST, "L3:Type[{0}]:Len[{1}]", new Object[]{certEntry2.tag, certEntry2.length});

                                    switch (certEntry2.tag)
                                    {
                                        case 0:
                                        {
                                            cert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certEntry2.data));
                                            //System.err.println(cert.getSubjectDN().getName());

                                            switch (certEntry.tag)
                                            {
                                                case 0:
                                                    rrnCert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certEntry2.data));
                                                    break;
                                                    
                                                case 1:
                                                    authenticationCert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certEntry2.data));
                                                    break;

                                                case 2:
                                                    signingCert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certEntry2.data));
                                                    break;

                                                case 3:
                                                    citizenCert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certEntry2.data));
                                                    break;

                                                case 4:
                                                    rootCert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certEntry2.data));
                                                    break;

                                            }

                                        }
                                        break;
                                    }
                                }

                            }
                            break;
                        }
                    }
                }
                break;
            }
        }

        if (rootCert != null && citizenCert != null)
        {
            if (authenticationCert != null)
            {
                List authChain = new LinkedList<X509Certificate>();
                authChain.add(authenticationCert);
                authChain.add(citizenCert);
                authChain.add(rootCert);
                eidData.setAuthCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_AUTH_TRUST_DOMAIN, authChain));
            }

            if (signingCert != null)
            {
                List signChain = new LinkedList<X509Certificate>();
                signChain.add(signingCert);
                signChain.add(citizenCert);
                signChain.add(rootCert);

                eidData.setSignCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_NON_REPUDIATION_TRUST_DOMAIN, signChain));
            }

            if (rrnCert != null)
            {
                List signChain = new LinkedList<X509Certificate>();
                signChain.add(rrnCert);
                signChain.add(rootCert);
                eidData.setSignCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_NATIONAL_REGISTRY_TRUST_DOMAIN, signChain));
            }
        }
    }
}
