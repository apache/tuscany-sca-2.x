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


/**
 * The interface for StandardTypesServiceClient.
 */
public interface StandardTypesServiceClient {
    BigInteger getNewBigIntegerForward(BigInteger bi);
    BigInteger[] getNewBigIntegerArrayForward(BigInteger[] bia);

    BigDecimal getNewBigDecimalForward(BigDecimal bd);
    BigDecimal[] getNewBigDecimalArrayForward(BigDecimal[] bda);

    Calendar getNewCalendarForward(Calendar c);
    Calendar[] getNewCalendarArrayForward(Calendar[] ca);

    Date getNewDateForward(Date d);
    Date[] getNewDateArrayForward(Date[] da);

    QName getNewQNameForward(QName qname);
    QName[] getNewQNameArrayForward(QName[] qnames);

    URI getNewURIForward(URI uri);
    URI[] getNewURIArrayForward(URI[] uris);

    XMLGregorianCalendar getNewXMLGregorianCalendarForward(XMLGregorianCalendar xgcal);
    XMLGregorianCalendar[] getNewXMLGregorianCalendarArrayForward(XMLGregorianCalendar[] xgcals);

    Duration getNewDurationForward(Duration d);
    Duration[] getNewDurationArrayForward(Duration[] da);

    Object getNewObjectForward(Object obj);
    Object[] getNewObjectArrayForward(Object[] objs);
    
    Image getNewImageForward(Image img);
    Image[] getNewImageArrayForward(Image[] imgs);
    
    DataHandler getNewDataHandlerForward(DataHandler dh);
    DataHandler[] getNewDataHandlerArrayForward(DataHandler[] dha);

    Source getNewSourceForward(Source src);
    Source[] getNewSourceArrayForward(Source[] srcs);

    UUID getNewUUIDForward(UUID uuid);
    UUID[] getNewUUIDArrayForward(UUID[] uuids);
}
