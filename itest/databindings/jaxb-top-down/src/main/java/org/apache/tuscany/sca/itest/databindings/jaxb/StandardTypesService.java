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
package org.apache.tuscany.sca.itest.databindings.jaxb;

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

import org.osoa.sca.annotations.Remotable;



/**
 * The interface for StandardTypesService.
 * 
 * @version $Rev$ $Date$
 */
@Remotable
public interface StandardTypesService {
    BigInteger getNewBigInteger(BigInteger bi);
    BigInteger[] getNewBigIntegerArray(BigInteger[] bia);
    
    BigDecimal getNewBigDecimal(BigDecimal bd);
    BigDecimal[] getNewBigDecimalArray(BigDecimal[] bda);

    Calendar getNewCalendar(Calendar c);
    Calendar[] getNewCalendarArray(Calendar[] ca);
    
    Date getNewDate(Date d);
    Date[] getNewDateArray(Date[] da);

    QName getNewQName(QName qname);
    QName[] getNewQNameArray(QName[] qnames);
    
    URI getNewURI(URI uri);
    URI[] getNewURIArray(URI[] uris);
    
    XMLGregorianCalendar getNewXMLGregorianCalendar(XMLGregorianCalendar xgcal);
    XMLGregorianCalendar[] getNewXMLGregorianCalendarArray(XMLGregorianCalendar[] xgcal);
    
    Duration getNewDuration(Duration d);
    Duration[] getNewDurationArray(Duration[] da);
    
    Object getNewObject(Object obj);
    Object[] getNewObjectArray(Object[] objs);
    
    Image getNewImage(Image img);
    Image[] getNewImageArray(Image[] imgs);
    
    DataHandler getNewDataHandler(DataHandler dh);
    DataHandler[] getNewDataHandlerArray(DataHandler[] dha);

    Source getNewSource(Source src);
    Source[] getNewSourceArray(Source[] srcs);
    
    UUID getNewUUID(UUID uuid);
    UUID[] getNewUUIDArray(UUID[] uuids);
}
