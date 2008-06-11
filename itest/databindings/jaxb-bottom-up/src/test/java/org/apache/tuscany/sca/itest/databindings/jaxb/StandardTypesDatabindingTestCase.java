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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.tuscany.sca.databinding.xml.String2Node;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.itest.databindings.jaxb.impl.StandardTypesTransformer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * @version $Rev$ $Date$
 */
public class StandardTypesDatabindingTestCase {

    private static SCADomain domain;

    /**
     * Runs before each test method
     */
    @BeforeClass
    public static void setUp() throws Exception {
        try {
            domain = SCADomain.newInstance("standard-types-service.composite");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs after each test method
     */
    @AfterClass
    public static void tearDown() {
        domain.close();
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewBigInteger.
     */
    @Test
    public void testSCANewBigInteger() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewBigInteger(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewBigIntegerArray.
     */
    @Test
    public void testSCANewBigIntegerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewBigIntegerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewBigDecimal.
     */
    @Test
    public void testSCANewBigDecimal() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewBigDecimal(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewBigDecimalArray.
     */
    @Test
    public void testSCANewBigDecimalArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewBigDecimalArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewCalendar.
     */
    @Test
    public void testSCANewCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewCalendarArray.
     */
    @Test
    public void testSCANewCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewDate.
     */
    @Test
    public void testSCANewDate() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewDate(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewDateArray.
     */
    @Test
    public void testSCANewDateArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewDateArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewQName.
     */
    @Test
    public void testSCANewQName() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewQName(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewQNameArray.
     */
    @Test
    public void testSCANewQNameArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewQNameArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewURI.
     */
    @Test
    public void testSCANewURI() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewURI(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewURIArray.
     */
    @Test
    // @Ignore("junit.framework.AssertionFailedError: expected:<http://abcuri> but was:<http://abcuri>")
    public void testSCANewURIArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewURIArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewXMLGregorianCalendar.
     */
    @Test
    public void testSCANewXMLGregorianCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewXMLGregorianCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewXMLGregorianCalendarArray.
     */
    @Test
    public void testSCANewXMLGregorianCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewXMLGregorianCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewDuration.
     */
    @Test
    public void testSCANewDuration() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewDuration(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewDurationArray.
     */
    @Test
    public void testSCANewDurationArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewDurationArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewObject.
     */
    @Test
    public void testSCANewObject() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewObject(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewObjectArray.
     */
    @Test
    public void testSCANewObjectArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewObjectArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewImage.
     */
    @Test
    public void testSCANewImage() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewImage(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewImageArray.
     */
    @Test
    @Ignore("junit.framework.AssertionFailedError: expected:<BufferedImage@79ecf4: type = 5 ColorModel: #pixelBits = 24 numComponents = 3 color space = java.awt.color.ICC_ColorSpace@aee320 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 10 height = 10 #numDataElements 3 dataOff[0] = 2> but was:<BufferedImage@1c160cb: type = 0 ColorModel: #pixelBits = 24 numComponents = 3 color space = java.awt.color.ICC_ColorSpace@aee320 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 10 height = 10 #numDataElements 3 dataOff[0] = 0>")
    public void testSCANewImageArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewImageArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewDataHandler.
     */
    @Test
    public void testSCANewDataHandler() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewDataHandler(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewDataHandlerArray.
     */
    @Test
    public void testSCANewDataHandlerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewDataHandlerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewSource.
     */
    @Test
    public void testSCANewSource() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewSource(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewSourceArray.
     */
    @Test
    @Ignore("TUSCANY-2387")
    public void testSCANewSourceArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewSourceArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewUUID.
     */
    @Test
    public void testSCANewUUID() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewUUID(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using SCA binding.
     * Service method invoked is getNewUUIDArray.
     */
    @Test
    public void testSCANewUUIDArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientSCAComponent");
        performTestNewUUIDArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigInteger.
     */
    @Test
    public void testWSNewBigInteger() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewBigInteger(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigIntegerArray.
     */
    @Test
    public void testWSNewBigIntegerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewBigIntegerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigDecimal.
     */
    @Test
    public void testWSNewBigDecimal() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewBigDecimal(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigDecimalArray.
     */
    @Test
    public void testWSNewBigDecimalArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewBigDecimalArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewCalendar.
     */
    @Test
    public void testWSNewCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewCalendarArray.
     */
    @Test
    public void testWSNewCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDate.
     */
    @Test
    public void testWSNewDate() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewDate(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDateArray.
     */
    @Test
    public void testWSNewDateArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewDateArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewQName.
     */
    @Test
    public void testWSNewQName() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewQName(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewQNameArray.
     */
    @Test
    // @Ignore("QNames with just local part are not getting passsed on over the wire.")
    public void testWSNewQNameArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewQNameArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewURI.
     */
    @Test
    // @Ignore("junit.framework.AssertionFailedError: expected:<http://abcuri> but was:<http://abcuri>")
    public void testWSNewURI() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewURI(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewURIArray.
     */
    @Test
    // @Ignore("junit.framework.AssertionFailedError: expected:<http://abcuri> but was:<http://abcuri>")
    public void testWSNewURIArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewURIArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewXMLGregorianCalendar.
     */
    @Test
    public void testWSNewXMLGregorianCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewXMLGregorianCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewXMLGregorianCalendarArray.
     */
    @Test
    public void testWSNewXMLGregorianCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewXMLGregorianCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDuration.
     */
    @Test
    public void testWSNewDuration() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewDuration(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDurationArray.
     */
    @Test
    public void testWSNewDurationArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewDurationArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewObject.
     */
    @Test
    @Ignore("TUSCANY-2385")
    public void testWSNewObject() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewObject(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewObjectArray.
     */
    @Test
    @Ignore("TUSCANY-2385")
    public void testWSNewObjectArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewObjectArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewImage.
     */
    @Test
    @Ignore("junit.framework.AssertionFailedError: expected:<BufferedImage@1e9d0cc: type = 5 ColorModel: #pixelBits = 24 numComponents = 3 color space = java.awt.color.ICC_ColorSpace@aee320 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 10 height = 10 #numDataElements 3 dataOff[0] = 2> but was:<BufferedImage@18b0b4a: type = 0 ColorModel: #pixelBits = 24 numComponents = 3 color space = java.awt.color.ICC_ColorSpace@aee320 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 10 height = 10 #numDataElements 3 dataOff[0] = 0>")
    public void testWSNewImage() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewImage(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewImageArray.
     */
    @Test
    @Ignore("junit.framework.AssertionFailedError: expected:<BufferedImage@5afcb1: type = 5 ColorModel: #pixelBits = 24 numComponents = 3 color space = java.awt.color.ICC_ColorSpace@aee320 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 10 height = 10 #numDataElements 3 dataOff[0] = 2> but was:<BufferedImage@bb1bc4: type = 0 ColorModel: #pixelBits = 24 numComponents = 3 color space = java.awt.color.ICC_ColorSpace@aee320 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 10 height = 10 #numDataElements 3 dataOff[0] = 0>")
    public void testWSNewImageArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewImageArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDataHandler.
     */
    @Test
    public void testWSNewDataHandler() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewDataHandler(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDataHandlerArray.
     */
    @Test
    public void testWSNewDataHandlerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewDataHandlerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewSource.
     */
    @Test
    @Ignore("junit.framework.ComparisonFailure: null expected:<... encoding=\"UTF-8\"?><[a>A</a]>> but was:<... encoding=\"UTF-8\"?><[return xmlns=\"http://jaxb.databindings.itest.sca.tuscany.apache.org/\">A</return]>>")
    public void testWSNewSource() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewSource(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewSourceArray.
     */
    @Test
    @Ignore("TUSCANY-2386")
    public void testWSNewSourceArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewSourceArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewUUID.
     */
    @Test
    public void testWSNewUUID() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewUUID(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewUUIDArray.
     */
    @Test
    public void testWSNewUUIDArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientWSComponent");
        performTestNewUUIDArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewBigInteger.
     */
    @Test
    public void testSCALocalNewBigInteger() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewBigInteger(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewBigIntegerArray.
     */
    @Test
    public void testSCALocalNewBigIntegerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewBigIntegerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewBigDecimal.
     */
    @Test
    public void testSCALocalNewBigDecimal() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewBigDecimal(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewBigDecimalArray.
     */
    @Test
    public void testSCALocalNewBigDecimalArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewBigDecimalArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewCalendar.
     */
    @Test
    public void testSCALocalNewCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewCalendarArray.
     */
    @Test
    public void testSCALocalNewCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewDate.
     */
    @Test
    public void testSCALocalNewDate() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewDate(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewDateArray.
     */
    @Test
    public void testSCALocalNewDateArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewDateArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewQName.
     */
    @Test
    public void testSCALocalNewQName() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewQName(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewQNameArray.
     */
    @Test
    public void testSCALocalNewQNameArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewQNameArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewURI.
     */
    @Test
    public void testSCALocalNewURI() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewURI(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewURIArray.
     */
    @Test
    public void testSCALocalNewURIArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewURIArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewXMLGregorianCalendar.
     */
    @Test
    public void testSCALocalNewXMLGregorianCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewXMLGregorianCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewXMLGregorianCalendarArray.
     */
    @Test
    public void testSCALocalNewXMLGregorianCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewXMLGregorianCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewDuration.
     */
    @Test
    public void testSCALocalNewDuration() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewDuration(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewDurationArray.
     */
    @Test
    public void testSCALocalNewDurationArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewDurationArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewObject.
     */
    @Test
    public void testSCALocalNewObject() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewObject(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewObjectArray.
     */
    @Test
    public void testSCALocalNewObjectArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewObjectArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewImage.
     */
    @Test
    public void testSCALocalNewImage() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewImage(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewImageArray.
     */
    @Test
    public void testSCALocalNewImageArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewImageArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalLocalService service using SCA binding.
     * Service method invoked is getNewDataHandler.
     */
    @Test
    public void testSCALocalNewDataHandler() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewDataHandler(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewDataHandlerArray.
     */
    @Test
    public void testSCALocalNewDataHandlerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewDataHandlerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewSource.
     */
    @Test
    public void testSCALocalNewSource() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewSource(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewSourceArray.
     */
    @Test
    public void testSCALocalNewSourceArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewSourceArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewUUID.
     */
    @Test
    public void testSCALocalNewUUID() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewUUID(serviceClient);
    }

    /**
     * Invokes the StandardTypesLocalService service using SCA binding.
     * Service method invoked is getNewUUIDArray.
     */
    @Test
    public void testSCALocalNewUUIDArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            domain.getService(StandardTypesServiceClient.class, "StandardTypesLocalServiceClientSCAComponent");
        performTestNewUUIDArray(serviceClient);
    }

    private void performTestNewBigInteger(StandardTypesServiceClient serviceClient) {
        BigInteger bi = new BigInteger("1234567890123456789012345678901234");
        BigInteger expected = bi.negate();
        BigInteger actual = serviceClient.getNewBigIntegerForward(bi);
        Assert.assertEquals(expected, actual);
    }

    private void performTestNewBigIntegerArray(StandardTypesServiceClient serviceClient) {
        BigInteger[] bia = new BigInteger[2];
        bia[0] = new BigInteger("1234567890123456789012345678901234");
        bia[1] = new BigInteger("-98765432109876543210987654321");
        BigInteger[] actual = serviceClient.getNewBigIntegerArrayForward(bia);
        Assert.assertEquals(bia.length, actual.length);
        for (int i = 0; i < bia.length; ++i) {
            Assert.assertEquals(bia[i].negate(), actual[i]);
        }
    }

    private void performTestNewBigDecimal(StandardTypesServiceClient serviceClient) {
        BigDecimal bd = new BigDecimal("12345678901234567890.12345678901234");
        BigDecimal expected = bd.negate();
        BigDecimal actual = serviceClient.getNewBigDecimalForward(bd);
        Assert.assertEquals(expected, actual);
    }

    private void performTestNewBigDecimalArray(StandardTypesServiceClient serviceClient) {
        BigDecimal[] bda = new BigDecimal[2];
        bda[0] = new BigDecimal("1234567890123456.789012345678901234");
        bda[1] = new BigDecimal("-987654321098765.43210987654321");
        BigDecimal[] actual = serviceClient.getNewBigDecimalArrayForward(bda);
        Assert.assertEquals(bda.length, actual.length);
        for (int i = 0; i < bda.length; ++i) {
            Assert.assertEquals(bda[i].negate(), actual[i]);
        }
    }

    private void performTestNewCalendar(StandardTypesServiceClient serviceClient) {
        Calendar[] ca = new Calendar[3];
        String[] tz = {"GMT+05:30", "GMT+00:00", "GMT-05:00"};
        for (int i = 0; i < ca.length; ++i) {
            ca[i] = Calendar.getInstance(TimeZone.getTimeZone(tz[i]));
            ca[i].set(Calendar.DAY_OF_MONTH, i + 1);
        }
        for (int i = 0; i < ca.length; ++i) {
            Calendar actual = serviceClient.getNewCalendarForward(ca[i]);
            ca[i].add(Calendar.DAY_OF_MONTH, 5);
            if (actual instanceof GregorianCalendar && ca[i] instanceof GregorianCalendar) {
                // FIXME: Is this a problem?
                // The instance returned by service method invoked over binding.ws seems to have a gregorianCutover
                // different from the instance passed.  Adjust the gregorianCutover as per the input instance.
                ((GregorianCalendar)actual).setGregorianChange(((GregorianCalendar)ca[i]).getGregorianChange());
            }
            Assert.assertEquals(ca[i], actual);
        }
    }

    private void performTestNewCalendarArray(StandardTypesServiceClient serviceClient) {
        Calendar[] ca = new Calendar[3];
        String[] tz = {"GMT+05:30", "GMT+00:00", "GMT-05:00"};
        for (int i = 0; i < ca.length; ++i) {
            ca[i] = Calendar.getInstance(TimeZone.getTimeZone(tz[i]));
            ca[i].set(Calendar.DAY_OF_MONTH, i + 1);
        }
        Calendar[] actual = serviceClient.getNewCalendarArrayForward(ca);
        Assert.assertEquals(ca.length, actual.length);
        for (int i = 0; i < ca.length; ++i) {
            ca[i].add(Calendar.DAY_OF_MONTH, 5);
            if (actual[i] instanceof GregorianCalendar && ca[i] instanceof GregorianCalendar) {
                // FIXME: Is this a problem?
                // The instance returned by service method invoked over binding.ws seems to have a gregorianCutover
                // different from the instance passed.  Adjust the gregorianCutover as per the input instance.
                ((GregorianCalendar)actual[i]).setGregorianChange(((GregorianCalendar)ca[i]).getGregorianChange());
            }
            Assert.assertEquals(ca[i], actual[i]);
        }
    }

    private void performTestNewDate(StandardTypesServiceClient serviceClient) {
        Date d = new Date();
        Date expected = new Date(d.getTime() + 5 * 24 * 60 * 60 * 1000);
        Date actual = serviceClient.getNewDateForward(d);
        Assert.assertEquals(expected, actual);
    }

    private void performTestNewDateArray(StandardTypesServiceClient serviceClient) {
        Date[] d = new Date[2];
        Date[] expected = new Date[d.length];
        for (int i = 0; i < d.length; ++i) {
            d[i] = new Date();
            d[i].setTime(d[i].getTime() + i * 24 * 60 * 60 * 1000);
            expected[i] = new Date(d[i].getTime() + 5 * 24 * 60 * 60 * 1000);
        }
        Date[] actual = serviceClient.getNewDateArrayForward(d);
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; ++i) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }

    private void performTestNewQName(StandardTypesServiceClient serviceClient) {
        QName[] qnames = new QName[3];
        qnames[0] = new QName("localPart");
        qnames[1] = new QName("namespaceUri", "localPart");
        qnames[2] = new QName("namespaceUri", "localPart", "prefix");
        QName[] expected = new QName[qnames.length];
        for (int i = 0; i < qnames.length; ++i) {
            expected[i] =
                new QName(qnames[i].getNamespaceURI() + "q", qnames[i].getLocalPart() + "q",
                          qnames[i].getPrefix() + "q");
        }
        for (int i = 0; i < qnames.length; ++i) {
            QName actual = serviceClient.getNewQNameForward(qnames[i]);
            Assert.assertEquals(expected[i], actual);
        }
    }

    private void performTestNewQNameArray(StandardTypesServiceClient serviceClient) {
        QName[] qnames = new QName[4];
        qnames[0] = new QName("localPart");
        qnames[1] = new QName("namespaceUri", "localPart");
        qnames[2] = new QName("namespaceUri", "localPart", "prefix");
        qnames[3] = new QName("localPart2");
        QName[] expected = new QName[qnames.length];
        for (int i = 0; i < qnames.length; ++i) {
            expected[i] =
                new QName(qnames[i].getNamespaceURI() + "q", qnames[i].getLocalPart() + "q",
                          qnames[i].getPrefix() + "q");
        }
        QName[] actual = serviceClient.getNewQNameArrayForward(qnames);
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < qnames.length; ++i) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }

    private void performTestNewURI(StandardTypesServiceClient serviceClient) {
        URI[] uris = new URI[4];
        uris[0] = URI.create("a/b/c");
        uris[1] = URI.create("http://abc/");
        uris[2] = URI.create("ftp://a/b");
        uris[3] = URI.create("http://abc/").resolve("xyz");

        for (int i = 0; i < uris.length; ++i) {
            URI expected = uris[i].resolve("uri");
            URI actual = serviceClient.getNewURIForward(uris[i]);
            Assert.assertEquals(expected, actual);
        }
    }

    private void performTestNewURIArray(StandardTypesServiceClient serviceClient) {
        URI[] uris = new URI[4];
        uris[0] = URI.create("a/b/c");
        // [rfeng] We need to have a trialign / to avoid the resolving problem
        // FIXME: [vamsi] This is actually a data transformation problem. The array being returned from the service method is
        // not making to the caller intact.
        uris[1] = URI.create("http://abc/");
        uris[2] = URI.create("ftp://a/b");
        uris[3] = URI.create("http://abc/").resolve("xyz");

        URI[] expected = new URI[uris.length];
        for (int i = 0; i < uris.length; ++i) {
            expected[i] = uris[i].resolve("uri");
        }

        URI[] actual = serviceClient.getNewURIArrayForward(uris);
        Assert.assertEquals(expected.length, actual.length);
        for (int i = 0; i < uris.length; ++i) {
            Assert.assertEquals(expected[i], actual[i]);
        }
    }

    private void performTestNewXMLGregorianCalendar(StandardTypesServiceClient serviceClient)
        throws DatatypeConfigurationException {
        DatatypeFactory df = DatatypeFactory.newInstance();
        XMLGregorianCalendar[] xgcals = new XMLGregorianCalendar[3];
        xgcals[0] = df.newXMLGregorianCalendar(new GregorianCalendar(1974, GregorianCalendar.APRIL, 19));
        xgcals[1] = df.newXMLGregorianCalendar(new GregorianCalendar(1978, GregorianCalendar.OCTOBER, 13));
        xgcals[2] = df.newXMLGregorianCalendar(new GregorianCalendar(2006, GregorianCalendar.JUNE, 16));

        for (int i = 0; i < xgcals.length; ++i) {
            XMLGregorianCalendar actual = serviceClient.getNewXMLGregorianCalendarForward(xgcals[i]);
            xgcals[i].setDay(xgcals[i].getDay() + 5);
            Assert.assertEquals(xgcals[i], actual);
        }
    }

    private void performTestNewXMLGregorianCalendarArray(StandardTypesServiceClient serviceClient)
        throws DatatypeConfigurationException {
        DatatypeFactory df = DatatypeFactory.newInstance();
        XMLGregorianCalendar[] xgcals = new XMLGregorianCalendar[3];
        xgcals[0] = df.newXMLGregorianCalendar(new GregorianCalendar(1974, GregorianCalendar.APRIL, 19));
        xgcals[1] = df.newXMLGregorianCalendar(new GregorianCalendar(1978, GregorianCalendar.OCTOBER, 13));
        xgcals[2] = df.newXMLGregorianCalendar(new GregorianCalendar(2006, GregorianCalendar.JUNE, 16));

        XMLGregorianCalendar[] actual = serviceClient.getNewXMLGregorianCalendarArrayForward(xgcals);
        Assert.assertEquals(xgcals.length, actual.length);
        for (int i = 0; i < xgcals.length; ++i) {
            xgcals[i].setDay(xgcals[i].getDay() + 5);
            Assert.assertEquals(xgcals[i], actual[i]);
        }
    }

    private void performTestNewDuration(StandardTypesServiceClient serviceClient) throws DatatypeConfigurationException {
        DatatypeFactory df = DatatypeFactory.newInstance();
        Duration[] da = new Duration[3];
        da[0] = df.newDuration(1000000000000L);
        da[1] = df.newDurationDayTime(1000000000000L);
        da[2] = df.newDurationYearMonth(true, 1, 3);

        for (int i = 0; i < da.length; ++i) {
            Assert.assertEquals(da[i].negate(), serviceClient.getNewDurationForward(da[i]));
        }
    }

    private void performTestNewObject(StandardTypesServiceClient serviceClient) {
        Object[] objs = new Object[6];
        objs[0] = "Hello";
        objs[1] = 10;
        objs[2] = -1.0;
        objs[3] = URI.create("http://tuscany");
        objs[4] = null;
        objs[5] = UUID.randomUUID();

        for (int i = 0; i < objs.length; ++i) {
            Object expected = StandardTypesTransformer.getNewObject(objs[i]);
            Object actual = serviceClient.getNewObjectForward(objs[i]);
            Assert.assertEquals(expected, actual);
        }
    }

    private void performTestNewObjectArray(StandardTypesServiceClient serviceClient) {
        Object[] objs = new Object[5];
        objs[0] = "Hello";
        objs[1] = 10;
        objs[2] = -1.0;
        objs[3] = URI.create("http://tuscany");
        objs[4] = null;

        Object[] actual = serviceClient.getNewObjectArrayForward(objs);
        for (int i = 0; i < objs.length; ++i) {
            Object expected = StandardTypesTransformer.getNewObject(objs[i]);
            Assert.assertEquals(expected, actual[i]);
        }
    }

    private void performTestNewImage(StandardTypesServiceClient serviceClient) {
        Image[] imgs = new Image[3];
        imgs[0] = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);
        imgs[1] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        imgs[2] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < imgs.length; ++i) {
            Image actual = serviceClient.getNewImageForward(imgs[i]);
            Assert.assertEquals(imgs[i], actual);
        }
    }

    private void performTestNewImageArray(StandardTypesServiceClient serviceClient) {
        Image[] imgs = new Image[3];
        imgs[0] = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);
        imgs[1] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        imgs[2] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        Image[] actual = serviceClient.getNewImageArrayForward(imgs);
        Assert.assertEquals(imgs.length, actual.length);
        for (int i = 0; i < imgs.length; ++i) {
            Assert.assertEquals(imgs[i], actual[i]);
        }
    }

    private void performTestNewDurationArray(StandardTypesServiceClient serviceClient)
        throws DatatypeConfigurationException {
        DatatypeFactory df = DatatypeFactory.newInstance();
        Duration[] da = new Duration[3];
        da[0] = df.newDuration(1000000000000L);
        da[1] = df.newDurationDayTime(1000000000000L);
        da[2] = df.newDurationYearMonth(true, 1, 3);

        Duration[] actual = serviceClient.getNewDurationArrayForward(da);
        Assert.assertEquals(da.length, actual.length);
        for (int i = 0; i < da.length; ++i) {
            Assert.assertEquals(da[i].negate(), actual[i]);
        }
    }

    private void performTestNewDataHandler(StandardTypesServiceClient serviceClient) throws IOException {
        DataHandler[] dha = new DataHandler[3];
        dha[0] = new DataHandler("Some data", "text/plain");
        dha[1] = new DataHandler(this.getClass().getClassLoader().getResource("standard-types-service.composite"));
        dha[2] = new DataHandler(new ByteArrayDataSource("Some data2".getBytes()));

        for (int i = 0; i < dha.length; ++i) {
            DataHandler actual = serviceClient.getNewDataHandlerForward(dha[i]);
            // Note: The DataHandler returned may use a different type of DataSource.
            // Compare the data content instead of using equals().
            Assert.assertTrue(compare(dha[i], actual));
        }
    }

    private void performTestNewDataHandlerArray(StandardTypesServiceClient serviceClient) throws IOException {
        DataHandler[] dha = new DataHandler[3];
        dha[0] = new DataHandler("Some data", "text/plain");
        dha[1] = new DataHandler(this.getClass().getClassLoader().getResource("standard-types-service.composite"));
        dha[2] = new DataHandler(new ByteArrayDataSource("Some data2".getBytes()));

        DataHandler[] actual = serviceClient.getNewDataHandlerArrayForward(dha);
        Assert.assertEquals(dha.length, actual.length);
        for (int i = 0; i < dha.length; ++i) {
            // Note: The DataHandler returned may use a different type of DataSource.
            // Compare the data content instead of using equals().
            Assert.assertTrue(compare(dha[i], actual[i]));
        }
    }

    private void performTestNewSource(StandardTypesServiceClient serviceClient) throws Exception {
        String xml = new String("<a>A<b>B</b><c>C</c></a>");
        Source[] srcs = new Source[3];
        srcs[0] = new DOMSource(new String2Node().transform(xml, null));
        srcs[1] = new SAXSource(new InputSource(new StringReader(xml)));
        srcs[2] = new StreamSource(new StringReader(xml));

        for (int i = 0; i < srcs.length; ++i) {
            Source expected = StandardTypesTransformer.getNewSource(srcs[i]);
            Source actual = serviceClient.getNewSourceForward(srcs[i]);
            // [rfeng] The data may come back as a different source
            Assert.assertEquals(sourceToString(expected), sourceToString(actual));
        }
    }

    private void performTestNewSourceArray(StandardTypesServiceClient serviceClient) throws Exception {
        String xml = new String("<a>A<b>B</b><c>C</c></a>");
        Source[] srcs = new Source[3];
        srcs[0] = new DOMSource(new String2Node().transform(xml, null));
        srcs[1] = new SAXSource(new InputSource(new StringReader(xml)));
        srcs[2] = new StreamSource(new StringReader(xml));

        Source[] actual = serviceClient.getNewSourceArrayForward(srcs);
        Source[] expected = new Source[srcs.length];
        for(int i = 0; i < srcs.length; ++i) {
            expected[i] = StandardTypesTransformer.getNewSource(srcs[i]);
        }
        Assert.assertEquals(srcs.length, actual.length);
        for (int i = 0; i < srcs.length; ++i) {
            // [rfeng] The data may come back as a different source
            Assert.assertEquals(sourceToString(expected[i]), sourceToString(actual[i]));
        }

    }

    private void performTestNewUUID(StandardTypesServiceClient serviceClient) {
        UUID[] uuids = new UUID[3];
        uuids[0] = UUID.nameUUIDFromBytes("ABCDEFGHJKLMNOPQRSTUVWXYZ".getBytes());
        uuids[1] = UUID.nameUUIDFromBytes("abcdefghjklmnopqrstuvwxyz".getBytes());
        uuids[2] = UUID.randomUUID();

        for (int i = 0; i < uuids.length; ++i) {
            UUID expected = UUID.fromString(uuids[i].toString() + "AAA");
            UUID actual = serviceClient.getNewUUIDForward(uuids[i]);
            Assert.assertEquals(expected, actual);
        }
    }

    private void performTestNewUUIDArray(StandardTypesServiceClient serviceClient) {
        UUID[] uuids = new UUID[3];
        uuids[0] = UUID.nameUUIDFromBytes("ABCDEFGHJKLMNOPQRSTUVWXYZ".getBytes());
        uuids[1] = UUID.nameUUIDFromBytes("abcdefghjklmnopqrstuvwxyz".getBytes());
        uuids[2] = UUID.randomUUID();

        UUID[] actual = serviceClient.getNewUUIDArrayForward(uuids);
        for (int i = 0; i < uuids.length; ++i) {
            UUID expected = UUID.fromString(uuids[i].toString() + "AAA");
            Assert.assertEquals(expected, actual[i]);
        }
    }
    
    /**
     * This method compares two DataHandlers.
     * @return true if the data in the two handlers is the same.
     */
    private boolean compare(DataHandler dh1, DataHandler dh2) throws IOException {
        InputStream inp1 = dh1.getInputStream();
        InputStream inp2 = dh2.getInputStream();
        for(;;) {
            int i1 = inp1.read();
            int i2 = inp2.read();
            if(i1 == -1 && i2 == -1) {
                return true;
            } else if(i1 != -1 && i2 != -1) {
                if(i1 != i2) {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
    
    /**
     * This method returns the content of a source object as String.
     */
    private String sourceToString(Source s) throws Exception {
        StringWriter sw = new StringWriter();
        Result r  = new StreamResult(sw);
        TransformerFactory.newInstance().newTransformer().transform(s, r);
        sw.close();
        return sw.toString();
    }
}
