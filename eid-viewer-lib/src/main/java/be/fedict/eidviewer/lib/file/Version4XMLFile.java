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

import be.fedict.eid.applet.service.Address;
import be.fedict.eid.applet.service.Identity;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Frank Marien
 */

@Root (name="eid")
public class Version4XMLFile
{
    @Element (name="identity")
    private Version4XMLFileIdentity        identity;
    @Element (name="card")
    private Version4XMLFileCard            card;
    @Element (name="address")
    private Version4XMLFileAddress         address;
    @Element (name="certificates")
    private Version4XMLFileCertificates    certificates;
   
   

    public Version4XMLFile()
    {
        super();
        }

        public static void toXML(Version4XMLFile v4File, OutputStream outputStream) throws Exception
    {
        Serializer  serializer = new Persister();
                    serializer.write(v4File, outputStream);
    }

    public static Version4XMLFile fromXML(InputStream inputStream) throws Exception
    {
        Serializer  serializer = new Persister();
             return serializer.read(Version4XMLFile.class, inputStream);

    }
   
    public void fromIdentityAddressPhotoAndCertificates(Identity eidIdentity,Address eidAddress, byte[] photo, X509Certificate authCert,X509Certificate signCert, X509Certificate caCert,X509Certificate rrnCert, X509Certificate rootCert)
    {
        identity    =new Version4XMLFileIdentity(eidIdentity,photo);
        card        =new Version4XMLFileCard(eidIdentity);
        address     =new Version4XMLFileAddress(eidAddress);
        certificates=new Version4XMLFileCertificates(authCert,signCert,caCert,rrnCert,rootCert);
               
        }

    public Identity toIdentity() throws ParseException
    {
        Identity eidIdentity=new Identity();
        identity.toIdentity(eidIdentity);
        card.toIdentity(eidIdentity);
        return eidIdentity;
    }

    public Address toAddress()
    {
        Address eidAddress=new Address();
        address.toAddress(eidAddress);
        return eidAddress;
    }

    public List<X509Certificate> toAuthChain() throws CertificateException
    {
        return getCertificates().toAuthChain();
    }

    public List<X509Certificate> toSignChain() throws CertificateException
    {
        return getCertificates().toSignChain();
    }

    public List<X509Certificate> toRRNChain() throws CertificateException
    {
        return getCertificates().toRRNChain();
    }

    public byte[] toPhoto()
    {
        return identity.toPhoto();
    }

    public Version4XMLFileAddress getAddress()
    {
        return address;
    }

    public void setAddress(Version4XMLFileAddress address)
    {
        this.address = address;
    }

    public Version4XMLFileCard getCard()
    {
        return card;
    }

    public void setCard(Version4XMLFileCard card)
    {
        this.card = card;
    }

    public Version4XMLFileIdentity getIdentity()
    {
        return identity;
    }

    public void setIdentity(Version4XMLFileIdentity identity)
    {
        this.identity = identity;
    }

    public Version4XMLFileCertificates getCertificates()
    {
        return certificates;
    }

    public void setCertificates(Version4XMLFileCertificates certificates)
    {
        this.certificates = certificates;
    }

       
}
