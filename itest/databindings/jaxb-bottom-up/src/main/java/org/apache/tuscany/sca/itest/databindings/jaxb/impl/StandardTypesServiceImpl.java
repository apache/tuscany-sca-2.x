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
import org.apache.tuscany.sca.itest.databindings.jaxb.StandardTypesService;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of StandardTypesService.
 * This implementation provides both a local and a remotable service.
 */
@Service(interfaces={StandardTypesService.class, StandardTypesLocalService.class})
public class StandardTypesServiceImpl implements StandardTypesService, StandardTypesLocalService {

    public BigInteger getNewBigInteger(BigInteger bi) {
        return bi.negate();
    }

    public BigInteger[] getNewBigIntegerArray(BigInteger[] bia) {
        BigInteger[] resp = new BigInteger[bia.length];
        for(int i = 0; i < bia.length; ++i) {
            resp[i] = bia[i].negate();
        }
        return resp;
    }

    public BigDecimal getNewBigDecimal(BigDecimal bd) {
        return bd.negate();
    }

    public BigDecimal[] getNewBigDecimalArray(BigDecimal[] bda) {
        BigDecimal[] resp = new BigDecimal[bda.length];
        for(int i = 0; i < bda.length; ++i) {
            resp[i] = bda[i].negate();
        }
        return resp;
    }

    public Calendar getNewCalendar(Calendar c) {
        Calendar resp = (Calendar)c.clone();
        resp.add(Calendar.DAY_OF_MONTH, 5);
        return resp;
    }

    public Calendar[] getNewCalendarArray(Calendar[] ca) {
        Calendar[] resp = new Calendar[ca.length];
        for(int i = 0; i < ca.length; ++i) {
            resp[i] = getNewCalendar(ca[i]);
        }
        return resp;
    }

    public Date getNewDate(Date d) {
        return new Date(d.getTime() + 5*24*60*60*1000);
    }

    public Date[] getNewDateArray(Date[] da) {
        Date[] resp = new Date[da.length];
        for(int i = 0; i < da.length; ++i) {
            resp[i] = getNewDate(da[i]);
        }
        return resp;
    }

    public QName getNewQName(QName qname) {
        return new QName(qname.getNamespaceURI()+"q", qname.getLocalPart()+"q", qname.getPrefix()+"q");
        
    }

    public QName[] getNewQNameArray(QName[] qnames) {
        QName[] resp = new QName[qnames.length];
        for(int i = 0; i < qnames.length; ++i) {
            resp[i] = getNewQName(qnames[i]);
        }
        return resp;
    }

    public URI getNewURI(URI uri) {
        return uri.resolve("uri");
    }

    public URI[] getNewURIArray(URI[] uris) {
        URI[] resp = new URI[uris.length];
        for(int i = 0; i < uris.length; ++i) {
            resp[i] = getNewURI(uris[i]);
        }
        return resp;
    }

    public XMLGregorianCalendar getNewXMLGregorianCalendar(XMLGregorianCalendar xgcal) {
        xgcal = (XMLGregorianCalendar)xgcal.clone();
        xgcal.setDay(xgcal.getDay()+5);
        return xgcal;
    }

    public XMLGregorianCalendar[] getNewXMLGregorianCalendarArray(XMLGregorianCalendar[] xgcals) {
        XMLGregorianCalendar[] resp = new XMLGregorianCalendar[xgcals.length];
        for(int i = 0; i < xgcals.length; ++i) {
            resp[i] = getNewXMLGregorianCalendar(xgcals[i]);
        }
        return resp;
    }

    public Duration getNewDuration(Duration d) {
        return d.negate();
    }

    public Duration[] getNewDurationArray(Duration[] da) {
        Duration[] resp = new Duration[da.length];
        for(int i = 0; i < da.length; ++i) {
            resp[i] = da[i].negate();
        }
        return resp;
    }

    public Object getNewObject(Object obj) {
        return StandardTypesTransformer.getNewObject(obj);
    }

    public Object[] getNewObjectArray(Object[] objs) {
        Object[] resp = new Object[objs.length];
        for(int i = 0; i < objs.length; ++i) {
            resp[i] = getNewObject(objs[i]);
        }
        return resp;
    }

    public Image getNewImage(Image img) {
        return StandardTypesTransformer.getNewImage(img);
    }

    public Image[] getNewImageArray(Image[] imgs) {
        Image[] resp = new Image[imgs.length];
        for(int i = 0; i < imgs.length; ++i) {
            resp[i] = getNewImage(imgs[i]);
        }
        return resp;
    }

    public DataHandler getNewDataHandler(DataHandler dh) {
        // FIXME: transform the input
        return dh;
    }

    public DataHandler[] getNewDataHandlerArray(DataHandler[] dha) {
        DataHandler[] resp = new DataHandler[dha.length];
        for(int i = 0; i < dha.length; ++i) {
            resp[i] = dha[i];
        }
        return resp;
    }
    
    public Source getNewSource(Source src) {
        return StandardTypesTransformer.getNewSource(src);
    }

    public Source[] getNewSourceArray(Source[] srcs) {
        Source[] resp = new Source[srcs.length];
        for(int i = 0; i < srcs.length; ++i) {
            resp[i] = getNewSource(srcs[i]);
        }
        return resp;
    }

    public UUID getNewUUID(UUID uuid) {
        return UUID.fromString(uuid.toString()+"AAA");
    }

    public UUID[] getNewUUIDArray(UUID[] uuids) {
        UUID[] resp = new UUID[uuids.length];
        for(int i = 0; i < uuids.length; ++i) {
            resp[i] = getNewUUID(uuids[i]);
        }
        return resp;
    }
}
