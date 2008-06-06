/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.itest.databindings.jaxb.impl;

import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.tuscany.sca.itest.databindings.jaxb.StandardTypesLocalService;
import org.apache.tuscany.sca.itest.databindings.jaxb.StandardTypesServiceClient;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of StandardTypesLocalServiceClient.
 * The client forwards the request to the service component and returns the response from the service component.
 */
@Service(StandardTypesServiceClient.class)
public class StandardTypesLocalServiceClientImpl implements StandardTypesServiceClient {

    private StandardTypesLocalService service;

    @Reference(required=false)
    protected void setStandardTypesLocalService(StandardTypesLocalService service) {
        this.service = service;
    }

    public BigInteger getNewBigIntegerForward(BigInteger bi) {
        return service.getNewBigInteger(bi);
    }

    public BigInteger[] getNewBigIntegerArrayForward(BigInteger[] bia) {
        return service.getNewBigIntegerArray(bia);
    }

    public BigDecimal getNewBigDecimalForward(BigDecimal bd) {
        return service.getNewBigDecimal(bd);
    }

    public BigDecimal[] getNewBigDecimalArrayForward(BigDecimal[] bda) {
        return service.getNewBigDecimalArray(bda);
    }
    public Calendar getNewCalendarForward(Calendar c) {
        return service.getNewCalendar(c);
    }
    public Calendar[] getNewCalendarArrayForward(Calendar[] ca) {
        return service.getNewCalendarArray(ca);
    }

    public Date getNewDateForward(Date d) {
        return service.getNewDate(d);
    }

    public Date[] getNewDateArrayForward(Date[] da) {
        return service.getNewDateArray(da);
    }

    public QName getNewQNameForward(QName qname) {
        return service.getNewQName(qname);
    }

    public QName[] getNewQNameArrayForward(QName[] qnames) {
        return service.getNewQNameArray(qnames);        
    }

    public URI getNewURIForward(URI uri) {
        return service.getNewURI(uri);
    }

    public URI[] getNewURIArrayForward(URI[] uris) {
        return service.getNewURIArray(uris);
    }

    public XMLGregorianCalendar getNewXMLGregorianCalendarForward(XMLGregorianCalendar xgcal) {
        return service.getNewXMLGregorianCalendar(xgcal);
    }

    public XMLGregorianCalendar[] getNewXMLGregorianCalendarArrayForward(XMLGregorianCalendar[] xgcals) {
        return service.getNewXMLGregorianCalendarArray(xgcals);
    }

    public Duration getNewDurationForward(Duration d) {
        return service.getNewDuration(d);
    }

    public Duration[] getNewDurationArrayForward(Duration[] da) {
        return service.getNewDurationArray(da);
    }

    public Object getNewObjectForward(Object obj) {
        return service.getNewObject(obj);
    }

    public Object[] getNewObjectArrayForward(Object[] objs) {
        return service.getNewObjectArray(objs);
    }
    
    public Image getNewImageForward(Image img) {
        return service.getNewImage(img);
    }

    public Image[] getNewImageArrayForward(Image[] imgs) {
        return service.getNewImageArray(imgs);
    }

    public DataHandler getNewDataHandlerForward(DataHandler dh) {
        return service.getNewDataHandler(dh);
    }

    public DataHandler[] getNewDataHandlerArrayForward(DataHandler[] dha) {
        return service.getNewDataHandlerArray(dha);
    }

    
    public Source getNewSourceForward(Source src) {
        return service.getNewSource(src);
    }

    public Source[] getNewSourceArrayForward(Source[] srcs) {
        return service.getNewSourceArray(srcs);
    }
    
    public UUID getNewUUIDForward(UUID uuid) {
        return service.getNewUUID(uuid);
    }

    public UUID[] getNewUUIDArrayForward(UUID[] uuids) {
        return service.getNewUUIDArray(uuids);
    }
}
