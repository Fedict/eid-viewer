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
import org.simpleframework.xml.Element;

/**
 *
 * @author Frank Marien
 */
public final class Version4XMLFileAddress
{
    @Element(name = "streetandnumber")
    private String streetAndNumber;
    @Element(name = "zip")
    private String zip;
    @Element(name = "municipality")
    private String municipality;

    public Version4XMLFileAddress(Address address)
    {
        super();
        fromAddress(address);
    }

    public Version4XMLFileAddress()
    {
        super();
    }

    public void fromAddress(Address eidAddress)
    {
        setStreetAndNumber(eidAddress.getStreetAndNumber().trim());
        setZip(eidAddress.getZip().trim());
        setMunicipality(eidAddress.getMunicipality().trim());
    }

    public void toAddress(Address eidAddress)
    {
        eidAddress.streetAndNumber=getStreetAndNumber();
        eidAddress.zip=getZip();
        eidAddress.municipality=getMunicipality();
    }
    
    public String getMunicipality()
    {
        return municipality;
    }

    public void setMunicipality(String municipality)
    {
        this.municipality = municipality;
    }

    public String getStreetAndNumber()
    {
        return streetAndNumber;
    }

    public void setStreetAndNumber(String streetAndNumber)
    {
        this.streetAndNumber = streetAndNumber;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }
}
