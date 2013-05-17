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

package be.fedict.eidviewer.lib.file.imports;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import be.fedict.eid.applet.service.Address;
import be.fedict.eid.applet.service.DocumentType;
import be.fedict.eid.applet.service.Gender;
import be.fedict.eid.applet.service.Identity;
import be.fedict.eid.applet.service.impl.tlv.DataConvertorException;
import be.fedict.eid.applet.service.impl.tlv.DateOfBirthDataConvertor;
import be.fedict.eidviewer.lib.EidData;
import be.fedict.eidviewer.lib.X509CertificateChainAndTrust;
import be.fedict.eidviewer.lib.X509Utilities;
import be.fedict.eidviewer.lib.file.helper.TextFormatHelper;
import be.fedict.trust.client.TrustServiceDomains;

/**
 *
 * @author Frank Marien
 */
public class Version35CSVFile
{      
    private static final Logger logger=Logger.getLogger(Version35CSVFile.class.getName());
    private static final int    CERTFIELDS                      =4;
    private static final int    CERT_NAME_OFFSET            =0;
    private static final int    CERT_DATA_OFFSET            =2;
   
    // token numbers of fixed parts
    private static final int    DOCUMENTTYPE                =3;
    private static final int    FIRSTNAMES                  =4;
    private static final int    LASTNAME                    =5;
    private static final int    GENDER                      =6;
    private static final int    BIRTHDATE                   =7;
    private static final int    PLACEOFBIRTH                =8;
    private static final int    NATIONALITY                 =10;
    private static final int    NATIONALNUMBER              =11;
    private static final int    CARDNUMBER                  =16;
    private static final int    CARDCHIPNUMBER              =17;
    private static final int    CARDVALIDFROM               =18;
    private static final int    CARDVALIDUNTIL              =19;
    private static final int    CARDISSUINGMUNICIPALITY     =20;
    private static final int    STREETANDNUMBER             =22;
    private static final int    ZIP                         =23;
    private static final int    MUNICIPALITY                =24;
    private static final int    PHOTO                       =30;
    private static final int    RRNCERTIFICATE              =53;
    private static final int    CERTCOUNT                       =55;
    private static final int    CERTBASE                        =56; // variable number of certs starts here.
   
    private CertificateFactory          certificateFactory  = null;
    private Address                     address             = null;
    private Identity                    identity            = null;
    private X509Certificate             rootCert            = null;
    private X509Certificate             citizenCert         = null;
    private X509Certificate             authenticationCert  = null;
    private X509Certificate             signingCert         = null;
    private X509Certificate             rrnCert                 = null;
    private EidData                     eidData                         = null;
    private DateFormat                  dateFormat                      = null;
    private byte[]                                      photo                           = null;
    private int                                         variableCertCount                       = -1;  

    public static void load(File file, EidData eidData) throws CertificateException, IOException
    {
        Version35CSVFile v35CVSFile=new Version35CSVFile(eidData);
                         v35CVSFile.load(file);
    }

    public Version35CSVFile(EidData eidData)
    {
        this.eidData=eidData;
        dateFormat=new SimpleDateFormat("dd.MM.yyyy");
    }

    public void load(File file) throws CertificateException, FileNotFoundException, IOException
    {
        logger.fine("Loading Version 35X CSV File");
       
        String line=null;
       
        certificateFactory = CertificateFactory.getInstance("X.509");
        InputStreamReader inputStreamReader=new InputStreamReader(new FileInputStream(file),"utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        line=bufferedReader.readLine();
        if(line!=null)
        {
            String[] tokens=line.split(";");
            for(int tokenNumber=0; (variableCertCount<0) && (tokenNumber<tokens.length); tokenNumber++)
            {
                String token=tokens[tokenNumber];
                logger.log(Level.FINEST, "token #{0} : [{1}]", new Object[]{tokenNumber, token});
               
                try
                {
                       
                    switch(tokenNumber)
                    {
                        case DOCUMENTTYPE:
                        identity=new Identity();
                        identity.documentType=DocumentType.toDocumentType(token.getBytes("utf-8"));
                        if(identity.documentType==null)
                            logger.log(Level.SEVERE, "Unknown Document Type \"{0}\"", token);
                        break;

                        case FIRSTNAMES:
                            TextFormatHelper.setFirstNamesFromString(identity, token);
                        break;

                        case LASTNAME:
                        identity.name=token;
                        break;

                        case GENDER:
                        identity.gender=token.equals("M")?Gender.MALE:Gender.FEMALE;
                        break;

                        case BIRTHDATE:
                        {
                            logger.fine("field BIRTHDATE");
                            DateOfBirthDataConvertor dateOfBirthConvertor=new DateOfBirthDataConvertor();
                            identity.dateOfBirth=dateOfBirthConvertor.convert(token.getBytes("utf-8"));
                        }
                        break;

                        case PLACEOFBIRTH:
                        identity.placeOfBirth=token;
                        break;

                        case NATIONALITY:
                        identity.nationality=token;
                        break;
                       
                        case NATIONALNUMBER:
                        identity.nationalNumber=token;
                        break;

                        case CARDNUMBER:
                        identity.cardNumber=token;
                        break;

                        case CARDCHIPNUMBER:
                        identity.chipNumber=token;
                        break;

                        case CARDVALIDFROM:
                        {
                            GregorianCalendar validityBeginCalendar=new GregorianCalendar();
                            try
                            {
                                validityBeginCalendar.setTime(dateFormat.parse(token));
                                identity.cardValidityDateBegin=validityBeginCalendar;
                            }
                            catch (ParseException ex)
                            {
                                logger.log(Level.SEVERE, "Failed to parse Card Validity Start Date \"" + token + "\"", ex);
                            }  
                        }
                        break;

                        case CARDVALIDUNTIL:
                            GregorianCalendar validityEndCalendar=new GregorianCalendar();
                            try
                            {
                                validityEndCalendar.setTime(dateFormat.parse(token));
                                identity.cardValidityDateEnd=validityEndCalendar;
                            }
                            catch (ParseException ex)
                            {
                                logger.log(Level.SEVERE, "Failed to parse Card Validity End Date \"" + token + "\"", ex);
                            }
                        break;

                        case CARDISSUINGMUNICIPALITY:
                        identity.cardDeliveryMunicipality=token;
                        break;

                        case STREETANDNUMBER:
                        address=new Address();
                        address.streetAndNumber=token;
                        break;

                        case ZIP:
                        address.zip=token;
                        break;

                        case MUNICIPALITY:
                        address.municipality=token;
                        break;

                        case PHOTO:
                        byte[] tokenBytes=token.getBytes();
                        eidData.setPhoto(Base64.decodeBase64(tokenBytes));
                        break;
                       
                        case RRNCERTIFICATE:
                           logger.finer("Gathering RRN Certificate");
                           try
                           {
                               rrnCert  = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(token.getBytes())));
                           }
                           catch (CertificateException ex)
                           {
                               logger.log(Level.SEVERE, "Failed to RRN Certificate", ex);
                           }
                       break;
                       
                        case CERTCOUNT:
                                logger.finer("Certificate Count: [" + token + "]");
                                variableCertCount=Integer.parseInt(token);
                        break;
                    }
                }
                catch (UnsupportedEncodingException ex)
                {
                    logger.log(Level.SEVERE, "Unsupported Encoding for Token " + tokenNumber , ex);
                }
                catch (DataConvertorException ex)
                {
                    logger.log(Level.SEVERE, "Couldn't Convert Date \"" + tokens[tokenNumber] + "\" in Token " + tokenNumber, ex);
                }
            } // done looping over fixed parts..

            if(identity!=null)
            {
                TextFormatHelper.setFirstNamesFromStrings(identity, identity.getFirstName(), identity.getMiddleName());
                eidData.setIdentity(identity);
            }

            if(address!=null)
                eidData.setAddress(address);
           
            // get variableCertCount variable certs
            for(int i=0;i<variableCertCount;i++)
            {
                X509Certificate thisCertificate=null;
                int certOffset=CERTBASE+(CERTFIELDS*i);
                String certType=tokens[certOffset+CERT_NAME_OFFSET];
                String certData=tokens[certOffset+CERT_DATA_OFFSET];
               
                logger.finer("Gathering " + certType + " Certificate");
               
                try
                {
                        thisCertificate=(X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(certData.getBytes())));
                }
                catch (CertificateException ex)
                {
                    logger.log(Level.SEVERE, "Failed to Convert Signing Certificate", ex);
                    thisCertificate=null;
                }
               
                if(thisCertificate!=null)
                {
                        if(certType.equalsIgnoreCase("Authentication"))
                                authenticationCert=thisCertificate;
                        else if(certType.equalsIgnoreCase("Signature"))
                                signingCert=thisCertificate;
                        else if(certType.equalsIgnoreCase("CA"))
                                citizenCert=thisCertificate;
                        else if(certType.equalsIgnoreCase("Root"))
                                rootCert=thisCertificate;
                }
            }
           
        }

        if(rootCert != null && citizenCert != null)
        {
            logger.fine("Certificates were gathered");
           
            if (rrnCert != null)
            {
                logger.fine("Setting RRN Certificate Chain");
                List<X509Certificate> rrnChain = new LinkedList<X509Certificate>();
                rrnChain.add(rrnCert);
                rrnChain.add(rootCert);
                eidData.setRRNCertChain(new X509CertificateChainAndTrust(TrustServiceDomains.BELGIAN_EID_NATIONAL_REGISTRY_TRUST_DOMAIN, rrnChain));
            }
           
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
        }
    }
   
    public void fromIdentityAddressPhotoAndCertificates(Identity identity, Address address, byte[] photo, X509Certificate authCert, X509Certificate signCert, X509Certificate citizenCert, X509Certificate rrnCert, X509Certificate rootCert) throws Exception
    {
        this.identity=identity;
        this.address=address;
        this.photo=photo;
        this.authenticationCert=authCert;
        this.signingCert=signCert;
        this.rrnCert=rrnCert;
        this.citizenCert=citizenCert;
        this.rootCert=rootCert;
    }
   
   
   
    public static void X509CertToCSV(X509Certificate certificate, String label, OutputStreamWriter writer) throws Exception
    {
                writer.write(String.format("%s;1;%s;;",label,X509Utilities.eidBase64Encode(certificate.getEncoded())));
    }
    
     
   
   
    public static void toCSV(Version35CSVFile v35File, OutputStream outputStream) throws Exception
    {
        /* version;type;name;surname;gender;date_of_birth;location_of_birth;nobility;nationality;
        national_nr;special_organization;member_of_family;special_status;logical_nr;chip_nr;
        date_begin;date_end;issuing_municipality;version;street;zip;municipality;country;
        file_id;file_id_sign;file_address;file_address_sign; */
       
        /* picturedata;picturehash */
       
       
        /*
        serial_nr;component_code;os_nr;os_version;softmask_nr;softmask_version;applet_version;
            global_os_version;applet_interface_version;PKCS1_support;key_exchange_version;
            application_lifecycle;graph_perso;elec_perso;elec_perso_interface;
        */
       
        /* challenge, response */
       
        /*
         * RRN Cert
         */
       
        /*
        certificatescount;certificate1;certificate2;...
        */
       
        /*
         * PIN codes
         */


        DateFormat dottyDate=new SimpleDateFormat("dd.MM.yyyy");
       
        OutputStreamWriter      writer=new OutputStreamWriter(outputStream);
        
        logger.finest(TextFormatHelper.dateToRRNDate(v35File.identity.dateOfBirth.getTime()).toUpperCase());
       
        // write all fixed pos data
                                                writer.write(String.format("1;eid;;%02d;%s;%s;%c;%s;%s;%s;%s;%s;%s;%s;%s;%1d;%s;%s;%s;%s;%s;;%s;%s;%s;be;;;;;%s;;;;;;;;;;;;;;;;;;;;;RRN;1;%s;;",
                                                                                                    v35File.identity.documentType.getKey(),
                                                                                                    v35File.identity.firstName,
                                                                                                    v35File.identity.name,
                                                                                                   (v35File.identity.gender==Gender.FEMALE?'F':'M'),
                                                                                                   TextFormatHelper.dateToRRNDate(v35File.identity.dateOfBirth.getTime()).toUpperCase(),
                                                                                                    v35File.identity.placeOfBirth,
                                                                                                    (v35File.identity.nobleCondition!=null?v35File.identity.nobleCondition:""),
                                                                                                    v35File.identity.nationality,
                                                                                                    v35File.identity.nationalNumber,
                                                                                                    (v35File.identity.duplicate!=null?v35File.identity.duplicate:""),
                                                                                                    (v35File.identity.specialOrganisation!=null?v35File.identity.specialOrganisation:""),
                                                                                                    (v35File.identity.memberOfFamily?"1":""),
                                                                                                    (v35File.identity.specialStatus!=null?v35File.identity.specialStatus.ordinal():0),
                                                                                                    // logicalNumber
                                                                                                    v35File.identity.cardNumber,
                                                                                                    v35File.identity.chipNumber,
                                                                                                    dottyDate.format(v35File.identity.cardValidityDateBegin.getTime()),
                                                                                                    dottyDate.format(v35File.identity.cardValidityDateEnd.getTime()),
                                                                                                    v35File.identity.cardDeliveryMunicipality,
                                                                                                    // version
                                                                                                    v35File.address.streetAndNumber,
                                                                                                    v35File.address.zip,
                                                                                                    v35File.address.municipality,
                                                                                                    // file_id
                                                                                                    // file_id_sign
                                                                                                    // file_address
                                                                                                    // file_address_sign
                                                                                                    X509Utilities.eidBase64Encode(v35File.photo),
                                                                                                    // picturehash
                                                                                                    // serial_nr
                                                                                                    // component_code
                                                                                                    // os_nr
                                                                                                    // os_version
                                                                                                    // softmask_nr
                                                                                                    // softmask_version
                                                                                                    // applet_version
                                                                                            // global_os_version
                                                                                                    // applet_interface_version
                                                                                                    // PKCS1_support
                                                                                                    // key_exchange_version
                                                                                            // application_lifecycle
                                                                                                    // graph_perso
                                                                                                    // elec_perso
                                                                                                    // elec_perso_interface
                                                                                                    // challenge
                                                                                                    // response
                                                                                                    X509Utilities.eidBase64Encode(v35File.rrnCert.getEncoded())
                                                                                                    ));
                                               
        // write variable number of certificates
        int ncerts=0;
        if(v35File.authenticationCert!=null)    ncerts++;
        if(v35File.signingCert!=null)                   ncerts++;
        if(v35File.citizenCert!=null)                   ncerts++;
        if(v35File.rootCert!=null)                              ncerts++;
        writer.write(String.format("%d;",ncerts));
        if(v35File.authenticationCert!=null) X509CertToCSV(v35File.authenticationCert,  "Authentication",writer);
        if(v35File.signingCert!=null)            X509CertToCSV(v35File.signingCert,                     "Signature",     writer);
        if(v35File.citizenCert!=null)            X509CertToCSV(v35File.citizenCert,                     "CA",                    writer);
        if(v35File.rootCert!=null)                       X509CertToCSV(v35File.rootCert,                        "Root",                  writer);
       
        // write variable number of pin codes..
        writer.write("0");      // zero PIN codes in this file
        writer.flush();
        writer.close();
                         
    }
}
