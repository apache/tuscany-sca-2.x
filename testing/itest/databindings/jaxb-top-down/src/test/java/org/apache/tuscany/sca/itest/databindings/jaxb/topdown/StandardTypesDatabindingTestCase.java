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

package org.apache.tuscany.sca.itest.databindings.jaxb.topdown;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
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
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.tuscany.sca.itest.databindings.jaxb.StandardTypesServiceClient;
import org.apache.tuscany.sca.itest.databindings.jaxb.impl.StandardTypesTransformer;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class StandardTypesDatabindingTestCase {

    private static Node node;

    /**
     * Runs once before the tests
     */
    @BeforeClass
    public static void setUp() throws Exception {
        NodeFactory factory = NodeFactory.newInstance();
        node = factory.createNode(new File("src/main/resources/wsdl/wrapped/standard-types-service.composite").toURI().toURL().toString(),
                new Contribution("TestContribution", new File("src/main/resources/wsdl/wrapped/").toURI().toURL().toString()));
        node.start();
    }

    /**
     * Runs once after the tests
     */
    @AfterClass
    public static void tearDown() {
        node.stop();
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigInteger.
     */
    @Test
    public void testW2WNewBigInteger() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewBigInteger(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigIntegerArray.
     */
    @Test
    public void testW2WNewBigIntegerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewBigIntegerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigDecimal.
     */
    @Test
    public void testW2WNewBigDecimal() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewBigDecimal(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigDecimalArray.
     */
    @Test
    public void testW2WNewBigDecimalArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewBigDecimalArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewCalendar.
     */
    @Test
    public void testW2WNewCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewCalendarArray.
     */
    @Test
    public void testW2WNewCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDate.
     */
    @Test
    public void testW2WNewDate() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewDate(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDateArray.
     */
    @Test
    public void testW2WNewDateArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewDateArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewQName.
     */
    @Test
    public void testW2WNewQName() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewQName(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewQNameArray.
     */
    @Test
    public void testW2WNewQNameArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewQNameArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewURI.
     */
    @Test
    public void testW2WNewURI() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewURI(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewURIArray.
     */
    @Test
    public void testW2WNewURIArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewURIArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewXMLGregorianCalendar.
     */
    @Test
    public void testW2WNewXMLGregorianCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewXMLGregorianCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewXMLGregorianCalendarArray.
     */
    @Test
    public void testW2WNewXMLGregorianCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewXMLGregorianCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDuration.
     */
    @Test
    public void testW2WNewDuration() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewDuration(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDurationArray.
     */
    @Test
    public void testW2WNewDurationArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewDurationArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewObject.
     */
    @Test
    public void testW2WNewObject() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewObject(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewObjectArray.
     */
    @Test
    public void testW2WNewObjectArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewObjectArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewImage.
     */
    @Test
    public void testW2WNewImage() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewImage(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewImageArray.
     */
    @Test
    public void testW2WNewImageArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewImageArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDataHandler.
     */
    @Test
    public void testW2WNewDataHandler() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewDataHandler(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDataHandlerArray.
     */
    @Test
    public void testW2WNewDataHandlerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewDataHandlerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewSource.
     */
    /*@Test
    //@Ignore("junit.framework.ComparisonFailure: null expected:<... encoding=\"UTF-8\"?><[a>A</a]>> but was:<... encoding=\"UTF-8\"?><[return xmlns=\"http://jaxb.databindings.itest.sca.tuscany.apache.org/\">A</return]>>")
    public void testW2WNewSource() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewSource(serviceClient);
    }*/

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewSourceArray.
     */
    /*@Test
    @Ignore("TUSCANY-2452")
    public void testW2WNewSourceArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewSourceArray(serviceClient);
    }*/

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewUUID.
     */
    @Test
    public void testW2WNewUUID() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewUUID(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewUUIDArray.
     */
    @Test
    public void testW2WNewUUIDArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2WComponent");
        performTestNewUUIDArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigInteger.
     */
    @Test
    public void testJ2WNewBigInteger() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewBigInteger(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigIntegerArray.
     */
    @Test
    public void testJ2WNewBigIntegerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewBigIntegerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigDecimal.
     */
    @Test
    public void testJ2WNewBigDecimal() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewBigDecimal(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigDecimalArray.
     */
    @Test
    public void testJ2WNewBigDecimalArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewBigDecimalArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewCalendar.
     */
    @Test
    public void testJ2WNewCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewCalendarArray.
     */
    @Test
    public void testJ2WNewCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDate.
     */
    @Test
    public void testJ2WNewDate() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewDate(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDateArray.
     */
    @Test
    public void testJ2WNewDateArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewDateArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewQName.
     */
    @Test
    public void testJ2WNewQName() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewQName(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewQNameArray.
     */
    @Test
    public void testJ2WNewQNameArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewQNameArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewURI.
     */
    @Test
    public void testJ2WNewURI() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewURI(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewURIArray.
     */
    @Test
    public void testJ2WNewURIArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewURIArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewXMLGregorianCalendar.
     */
    @Test
    public void testJ2WNewXMLGregorianCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewXMLGregorianCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewXMLGregorianCalendarArray.
     */
    @Test
    public void testJ2WNewXMLGregorianCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewXMLGregorianCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDuration.
     */
    @Test
    public void testJ2WNewDuration() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewDuration(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDurationArray.
     */
    @Test
    public void testJ2WNewDurationArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewDurationArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewObject.
     */
    @Test
    public void testJ2WNewObject() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewObject(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewObjectArray.
     */
    @Test
    public void testJ2WNewObjectArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewObjectArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewImage.
     */
    @Test
    public void testJ2WNewImage() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewImage(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewImageArray.
     */
    @Test
    public void testJ2WNewImageArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewImageArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDataHandler.
     */
    @Test
    public void testJ2WNewDataHandler() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewDataHandler(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDataHandlerArray.
     */
    @Test
    public void testJ2WNewDataHandlerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewDataHandlerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewSource.
     */
    /*@Test
    //@Ignore("junit.framework.ComparisonFailure: null expected:<... encoding=\"UTF-8\"?><[a>A</a]>> but was:<... encoding=\"UTF-8\"?><[return xmlns=\"http://jaxb.databindings.itest.sca.tuscany.apache.org/\">A</return]>>")
    public void testJ2WNewSource() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewSource(serviceClient);
    }*/

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewSourceArray.
     */
    /*@Test
    @Ignore("TUSCANY-2452")
    public void testJ2WNewSourceArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewSourceArray(serviceClient);
    }*/

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewUUID.
     */
    @Test
    public void testJ2WNewUUID() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewUUID(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewUUIDArray.
     */
    @Test
    public void testJ2WNewUUIDArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientJ2WComponent");
        performTestNewUUIDArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigInteger.
     */
    @Test
    public void testW2JNewBigInteger() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewBigInteger(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigIntegerArray.
     */
    @Test
    public void testW2JNewBigIntegerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewBigIntegerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigDecimal.
     */
    @Test
    public void testW2JNewBigDecimal() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewBigDecimal(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewBigDecimalArray.
     */
    @Test
    public void testW2JNewBigDecimalArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewBigDecimalArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewCalendar.
     */
    @Test
    public void testW2JNewCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewCalendarArray.
     */
    @Test
    public void testW2JNewCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDate.
     */
    @Test
    public void testW2JNewDate() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewDate(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDateArray.
     */
    @Test
    public void testW2JNewDateArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewDateArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewQName.
     */
    @Test
    public void testW2JNewQName() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewQName(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewQNameArray.
     */
    @Test
    public void testW2JNewQNameArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewQNameArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewURI.
     */
    @Test
    public void testW2JNewURI() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewURI(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewURIArray.
     */
    @Test
    public void testW2JNewURIArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewURIArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewXMLGregorianCalendar.
     */
    @Test
    public void testW2JNewXMLGregorianCalendar() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewXMLGregorianCalendar(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewXMLGregorianCalendarArray.
     */
    @Test
    public void testW2JNewXMLGregorianCalendarArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewXMLGregorianCalendarArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDuration.
     */
    @Test
    public void testW2JNewDuration() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewDuration(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDurationArray.
     */
    @Test
    public void testW2JNewDurationArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewDurationArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewObject.
     */
    @Test
    public void testW2JNewObject() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewObject(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewObjectArray.
     */
    @Test
    public void testW2JNewObjectArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewObjectArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewImage.
     */
    @Test
    public void testW2JNewImage() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewImage(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewImageArray.
     */
    @Test
    public void testW2JNewImageArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewImageArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDataHandler.
     */
    @Test
    public void testW2JNewDataHandler() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewDataHandler(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewDataHandlerArray.
     */
    @Test
    public void testW2JNewDataHandlerArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewDataHandlerArray(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewSource.
     */
    /*@Test
    //@Ignore("junit.framework.ComparisonFailure: null expected:<... encoding=\"UTF-8\"?><[a>A</a]>> but was:<... encoding=\"UTF-8\"?><[return xmlns=\"http://jaxb.databindings.itest.sca.tuscany.apache.org/\">A</return]>>")
    public void testW2JNewSource() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewSource(serviceClient);
    }*/

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewSourceArray.
     */
    /*@Test
    @Ignore("TUSCANY-2452")
    public void testW2JNewSourceArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewSourceArray(serviceClient);
    }*/

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewUUID.
     */
    @Test
    public void testW2JNewUUID() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
        performTestNewUUID(serviceClient);
    }

    /**
     * Invokes the StandardTypesService service using WS binding.
     * Service method invoked is getNewUUIDArray.
     */
    @Test
    public void testW2JNewUUIDArray() throws Exception {
        StandardTypesServiceClient serviceClient =
            node.getService(StandardTypesServiceClient.class, "StandardTypesServiceClientW2JComponent");
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
        Object[] objs = new Object[5];
        objs[0] = "Hello";
        objs[1] = 10;
        objs[2] = null;
        objs[3] = -1.0;
        objs[4] = null;

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
        objs[2] = null;
        objs[3] = -1.0;
        objs[4] = null;

        Object[] actual = serviceClient.getNewObjectArrayForward(objs);
        Assert.assertEquals(objs.length, actual.length);
        for (int i = 0; i < objs.length; ++i) {
            Object expected = StandardTypesTransformer.getNewObject(objs[i]);
            Assert.assertEquals(expected, actual[i]);
        }
    }

    private void performTestNewImage(StandardTypesServiceClient serviceClient) throws InterruptedException {
        // Create some images to test with.
        Image[] imgs = new Image[3];
        imgs[0] = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);
        imgs[1] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        imgs[2] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        imgs[0].getGraphics().drawLine(1, 1, 8, 8);
        imgs[1].getGraphics().drawLine(8, 1, 1, 8);
        imgs[2].getGraphics().drawLine(1, 8, 8, 1);
        
        Image[] copy = imgs;
        // Create the same images once again as the StandardTypesTransformer may manipulate the image passed.
        imgs = new Image[3];
        imgs[0] = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);
        imgs[1] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        imgs[2] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        imgs[0].getGraphics().drawLine(1, 1, 8, 8);
        imgs[1].getGraphics().drawLine(8, 1, 1, 8);
        imgs[2].getGraphics().drawLine(1, 8, 8, 1);
        
        // Make sure the images and copies are equal using ImageInfo
        for(int i = 0; i < imgs.length; ++i) {
            Assert.assertEquals(new ImageInfo(imgs[i]), new ImageInfo(copy[i]));
        }

        for (int i = 0; i < imgs.length; ++i) {
            Image actual = serviceClient.getNewImageForward(imgs[i]);
            Image expected = StandardTypesTransformer.getNewImage(copy[i]);
            // Compare using ImageInfo
            Assert.assertEquals(new ImageInfo(expected), new ImageInfo(actual));
        }
    }

    private void performTestNewImageArray(StandardTypesServiceClient serviceClient) throws InterruptedException {
        // Create some images to test with.
        Image[] imgs = new Image[3];
        imgs[0] = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);
        imgs[1] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        imgs[2] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        imgs[0].getGraphics().drawLine(1, 1, 8, 8);
        imgs[1].getGraphics().drawLine(8, 1, 1, 8);
        imgs[2].getGraphics().drawLine(1, 8, 8, 1);
        
        Image[] copy = imgs;
        // Create the same images once again as the StandardTypesTransformer may manipulate the image passed.
        imgs = new Image[3];
        imgs[0] = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);
        imgs[1] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        imgs[2] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        imgs[0].getGraphics().drawLine(1, 1, 8, 8);
        imgs[1].getGraphics().drawLine(8, 1, 1, 8);
        imgs[2].getGraphics().drawLine(1, 8, 8, 1);
        
        // Make sure the images and copies are equal using ImageInfo
        for(int i = 0; i < imgs.length; ++i) {
            Assert.assertEquals(new ImageInfo(imgs[i]), new ImageInfo(copy[i]));
        }

        Image[] actual = serviceClient.getNewImageArrayForward(imgs);
        Assert.assertEquals(imgs.length, actual.length);
        for (int i = 0; i < imgs.length; ++i) {
            Image expected = StandardTypesTransformer.getNewImage(copy[i]);
            // Compare using ImageInfo
            Assert.assertEquals(new ImageInfo(expected), new ImageInfo(actual[i]));
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
        dha[1] = new DataHandler(new URL("http://tuscany.apache.org/home.html"));
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
        dha[1] = new DataHandler(new URL("http://tuscany.apache.org/home.html"));
        dha[2] = new DataHandler(new ByteArrayDataSource("Some data2".getBytes()));

        DataHandler[] actual = serviceClient.getNewDataHandlerArrayForward(dha);
        Assert.assertEquals(dha.length, actual.length);
        for (int i = 0; i < dha.length; ++i) {
            // Note: The DataHandler returned may use a different type of DataSource.
            // Compare the data content instead of using equals().
            Assert.assertTrue(compare(dha[i], actual[i]));
        }
    }

    /*private void performTestNewSource(StandardTypesServiceClient serviceClient) throws Exception {
        String xml = "<a>A<b>B</b><c>C</c></a>";
        Source[] srcs = new Source[3];
        srcs[0] = new DOMSource(new String2Node(null).transform(xml, null));
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
        String xml = "<a>A<b>B</b><c>C</c></a>";
        Source[] srcs = new Source[3];
        srcs[0] = new DOMSource(new String2Node(null).transform(xml, null));
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

    }*/

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
    
    /**
     * This class initializes with the width, height and pixel data of a java.awt.Image object.
     */
    private static class ImageInfo {
        private int h, w, pixels[];
        public ImageInfo(Image img) throws InterruptedException {
            w = img.getWidth(null);
            h = img.getHeight(null);
            pixels = new int[w*h];
            PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
            pg.grabPixels();
        }
        
        public boolean equals(Object that) {
            if(that == null) {
                return false;
            } else if(!(that instanceof ImageInfo)) {
                return false;
            }
            
            ImageInfo that1 = (ImageInfo)that;
            if(w != that1.w || h != that1.h || pixels == null || that1.pixels == null || pixels.length != that1.pixels.length) {
                return false;
            }
            for(int i = 0; i < pixels.length; ++i) {
                if(pixels[i] != that1.pixels[i]) {
                    return false;
                }
            }
            return true;
        }
        
        public String toString() {
            return this.getClass().getSimpleName()+"[w = "+w+", h = "+h+", pixels = "+pixels+"]";
        }
    }
}
