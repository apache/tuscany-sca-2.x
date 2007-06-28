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
package org.apache.tuscany.sca.binding.notification.encoding;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.binding.notification.encoding.BrokerConsumerReferenceEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerIDEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerProducerReferenceEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.BrokersEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.ConsumerReferenceEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.DefaultEncodingRegistry;
import org.apache.tuscany.sca.binding.notification.encoding.EndConsumersEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.EndProducersEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointAddressEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointReference;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointReferenceEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NeighborBrokerConsumers;
import org.apache.tuscany.sca.binding.notification.encoding.NeighborBrokerConsumersEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.Neighbors;
import org.apache.tuscany.sca.binding.notification.encoding.NeighborsEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewBroker;
import org.apache.tuscany.sca.binding.notification.encoding.NewBrokerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewBrokerResponse;
import org.apache.tuscany.sca.binding.notification.encoding.NewBrokerResponseEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewConsumerResponse;
import org.apache.tuscany.sca.binding.notification.encoding.NewConsumerResponseEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.NewProducerResponse;
import org.apache.tuscany.sca.binding.notification.encoding.NewProducerResponseEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.ReferencePropertiesEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.RemoveBroker;
import org.apache.tuscany.sca.binding.notification.encoding.RemoveBrokerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.RemovedBrokerEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.ReplaceBrokerConnection;
import org.apache.tuscany.sca.binding.notification.encoding.ReplaceBrokerConnectionEnDeCoder;
import org.apache.tuscany.sca.binding.notification.encoding.Subscribe;
import org.apache.tuscany.sca.binding.notification.encoding.SubscribeEnDeCoder;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class EncodingTestCase extends TestCase {
    
    private static String wsnt = "http://docs.oasis-open.org/wsn/b-2";
    private static String wsa = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    private static String testUrl = "http://localhost:8080/test";
    private static String testUrl1 = "http://localhost:8081/test";
    private static String testUrl2 = "http://localhost:8082/test";
    private static String bid1 = "UUID1";
    private static String bid2 = "UUID2";
    private static String testSubscribe =
        "<wsnt:Subscribe xmlns:wsnt=\"" + wsnt + "\">" +
            "<wsnt:ConsumerReference xmlns:wsnt=\"" + wsnt + "\">" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl + "</wsa:Address>" +
                "</wsa:EndpointReference>" +
            "</wsnt:ConsumerReference>" +
        "</wsnt:Subscribe>";
    private static String testNewConsumerResponse =
        "<wsnt:NewConsumerResponse xmlns:wsnt=\"" + wsnt + "\" ProducerSequenceType=\"EndProducers\">" +
            "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl + "</wsa:Address>" +
            "</wsa:EndpointReference>" +
        "</wsnt:NewConsumerResponse>";
    private static String testNewProducerResponse =
        "<wsnt:NewProducerResponse xmlns:wsnt=\"" + wsnt + "\" ConsumerSequenceType=\"EndConsumers\">" +
            "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl1 + "</wsa:Address>" +
            "</wsa:EndpointReference>" +
            "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl2 + "</wsa:Address>" +
            "</wsa:EndpointReference>" +
        "</wsnt:NewProducerResponse>";
    private static String testNoProducersResponse =
        "<wsnt:NewConsumerResponse xmlns:wsnt=\"" + wsnt + "\" ProducerSequenceType=\"NoProducers\" />";
    private static String testNewBroker =
        "<wsnt:NewBroker xmlns:wsnt=\"" + wsnt + "\">" +
            "<wsnt:BrokerConsumerReference xmlns:wsnt=\"" + wsnt + "\">" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl1 + "</wsa:Address>" +
                    "<wsa:ReferenceProperties xmlns:wsa=\"" + wsa + "\">" +
                        "<wsnt:BrokerID xmlns:wsnt=\"" + wsnt + "\">" + bid1 + "</wsnt:BrokerID>" +
                    "</wsa:ReferenceProperties>" +
                "</wsa:EndpointReference>" +
            "</wsnt:BrokerConsumerReference>" +
            "<wsnt:BrokerProducerReference xmlns:wsnt=\"" + wsnt + "\">" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl2 + "</wsa:Address>" +
                    "<wsa:ReferenceProperties xmlns:wsa=\"" + wsa + "\">" +
                        "<wsnt:BrokerID xmlns:wsnt=\"" + wsnt + "\">" + bid2 + "</wsnt:BrokerID>" +
                    "</wsa:ReferenceProperties>" +
                "</wsa:EndpointReference>" +
            "</wsnt:BrokerProducerReference>" +
        "</wsnt:NewBroker>";
    private static String testNewBrokerResponse1 =
        "<wsnt:NewBrokerResponse xmlns:wsnt=\"" + wsnt + "\" FirstBroker=\"true\">" +
            "<wsnt:EndConsumers xmlns:wsnt=\"" + wsnt + "\" ConsumerSequenceType=\"EndConsumers\">" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl1 + "</wsa:Address>" +
                "</wsa:EndpointReference>" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl2 + "</wsa:Address>" +
                "</wsa:EndpointReference>" +
            "</wsnt:EndConsumers>" +
            "<wsnt:EndProducers xmlns:wsnt=\"" + wsnt + "\" ProducerSequenceType=\"NoProducers\" />" +
        "</wsnt:NewBrokerResponse>";
    private static String testNewBrokerResponse2 =
        "<wsnt:NewBrokerResponse xmlns:wsnt=\"" + wsnt + "\" FirstBroker=\"false\">" +
            "<wsnt:Brokers xmlns:wsnt=\"" + wsnt + "\">" +
                "<wsnt:Broker xmlns:wsnt=\"" + wsnt + "\">" +
                    "<wsnt:BrokerConsumerReference xmlns:wsnt=\"" + wsnt + "\">" +
                        "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                            "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl1 + "</wsa:Address>" +
                            "<wsa:ReferenceProperties xmlns:wsa=\"" + wsa + "\">" +
                                "<wsnt:BrokerID xmlns:wsnt=\"" + wsnt + "\">" + bid1 + "</wsnt:BrokerID>" +
                            "</wsa:ReferenceProperties>" +
                        "</wsa:EndpointReference>" +
                    "</wsnt:BrokerConsumerReference>" +
                    "<wsnt:BrokerProducerReference xmlns:wsnt=\"" + wsnt + "\">" +
                        "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                            "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl2 + "</wsa:Address>" +
                            "<wsa:ReferenceProperties xmlns:wsa=\"" + wsa + "\">" +
                                "<wsnt:BrokerID xmlns:wsnt=\"" + wsnt + "\">" + bid2 + "</wsnt:BrokerID>" +
                            "</wsa:ReferenceProperties>" +
                        "</wsa:EndpointReference>" +
                    "</wsnt:BrokerProducerReference>" +
                "</wsnt:Broker>" +
            "</wsnt:Brokers>" +
        "</wsnt:NewBrokerResponse>";
    private static String testRemoveBroker =
        "<wsnt:RemoveBroker xmlns:wsnt=\"" + wsnt + "\">" +
            "<wsnt:BrokerConsumerReference xmlns:wsnt=\"" + wsnt + "\">" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl + "</wsa:Address>" +
                    "<wsa:ReferenceProperties xmlns:wsa=\"" + wsa + "\">" +
                        "<wsnt:BrokerID xmlns:wsnt=\"" + wsnt + "\">" + bid1 + "</wsnt:BrokerID>" +
                    "</wsa:ReferenceProperties>" +
                "</wsa:EndpointReference>" +
            "</wsnt:BrokerConsumerReference>" +
            "<wsnt:NeighborBrokerConsumers xmlns:wsnt=\"" + wsnt + "\" ConsumerSequenceType=\"BrokerConsumers\">" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl1 + "</wsa:Address>" +
                "</wsa:EndpointReference>" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl2 + "</wsa:Address>" +
                "</wsa:EndpointReference>" +
            "</wsnt:NeighborBrokerConsumers>" +
        "</wsnt:RemoveBroker>";
    private static String testReplaceBrokerConnection =
        "<wsnt:ReplaceBrokerConnection xmlns:wsnt=\"" + wsnt + "\">" +
            "<wsnt:RemovedBroker xmlns:wsnt=\"" + wsnt + "\">" +
                "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                    "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl + "</wsa:Address>" +
                    "<wsa:ReferenceProperties xmlns:wsa=\"" + wsa + "\">" +
                        "<wsnt:BrokerID xmlns:wsnt=\"" + wsnt + "\">" + bid1 + "</wsnt:BrokerID>" +
                    "</wsa:ReferenceProperties>" +
                "</wsa:EndpointReference>" +
            "</wsnt:RemovedBroker>" +
            "<wsnt:Neighbors xmlns:wsnt=\"" + wsnt + "\">" +
                "<wsnt:Broker xmlns:wsnt=\"" + wsnt + "\">" +
                    "<wsnt:BrokerConsumerReference xmlns:wsnt=\"" + wsnt + "\">" +
                        "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                            "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl1 + "</wsa:Address>" +
                            "<wsa:ReferenceProperties xmlns:wsa=\"" + wsa + "\">" +
                                "<wsnt:BrokerID xmlns:wsnt=\"" + wsnt + "\">" + bid1 + "</wsnt:BrokerID>" +
                            "</wsa:ReferenceProperties>" +
                        "</wsa:EndpointReference>" +
                    "</wsnt:BrokerConsumerReference>" +
                    "<wsnt:BrokerProducerReference xmlns:wsnt=\"" + wsnt + "\">" +
                        "<wsa:EndpointReference xmlns:wsa=\"" + wsa + "\">" +
                            "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl2 + "</wsa:Address>" +
                            "<wsa:ReferenceProperties xmlns:wsa=\"" + wsa + "\">" +
                                "<wsnt:BrokerID xmlns:wsnt=\"" + wsnt + "\">" + bid2 + "</wsnt:BrokerID>" +
                            "</wsa:ReferenceProperties>" +
                        "</wsa:EndpointReference>" +
                    "</wsnt:BrokerProducerReference>" +
                "</wsnt:Broker>" +
            "</wsnt:Neighbors>" +
        "</wsnt:ReplaceBrokerConnection>";
    
    public void testSubscribe() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        SubscribeEnDeCoder sed = new SubscribeEnDeCoder(der);
        sed.start();
        ConsumerReferenceEnDeCoder cred = new ConsumerReferenceEnDeCoder(der);
        cred.start();
        EndpointReferenceEnDeCoder epred = new EndpointReferenceEnDeCoder(der);
        epred.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testSubscribe));
        reader.next();
        Subscribe subscribe = (Subscribe)der.decode(reader);
        Assert.assertEquals(subscribe.getConsumerReference().getReference().getEndpointAddress().getAddress().toString(), testUrl);
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(subscribe, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testSubscribe);
    }
    
    public void testNewConsumerResponse() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        NewConsumerResponseEnDeCoder ncred = new NewConsumerResponseEnDeCoder(der);
        ncred.start();
        EndpointReferenceEnDeCoder epred = new EndpointReferenceEnDeCoder(der);
        epred.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testNewConsumerResponse));
        reader.next();
        NewConsumerResponse newConsumerResponse = (NewConsumerResponse)der.decode(reader);
        Assert.assertEquals(newConsumerResponse.getSequenceType(), "EndProducers");
        Assert.assertEquals(newConsumerResponse.getReferenceSequence().iterator().next().getEndpointAddress().getAddress().toString(),
                            testUrl);
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(newConsumerResponse, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testNewConsumerResponse);
    }
    
    public void testNoProducersResponse() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        NewConsumerResponseEnDeCoder ncred = new NewConsumerResponseEnDeCoder(der);
        ncred.start();
        EndpointReferenceEnDeCoder epred = new EndpointReferenceEnDeCoder(der);
        epred.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testNoProducersResponse));
        reader.next();
        NewConsumerResponse newConsumerResponse = (NewConsumerResponse)der.decode(reader);
        Assert.assertEquals(newConsumerResponse.getSequenceType(), "NoProducers");
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(newConsumerResponse, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testNoProducersResponse);
    }
    
    public void testNewProducerResponse() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        NewProducerResponseEnDeCoder npred = new NewProducerResponseEnDeCoder(der);
        npred.start();
        EndpointReferenceEnDeCoder epred = new EndpointReferenceEnDeCoder(der);
        epred.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testNewProducerResponse));
        reader.next();
        NewProducerResponse newProducerResponse = (NewProducerResponse)der.decode(reader);
        Assert.assertEquals(newProducerResponse.getSequenceType(), "EndConsumers");
        Iterator<EndpointReference> it = newProducerResponse.getReferenceSequence().iterator();
        it.next();
        Assert.assertEquals(it.next().getEndpointAddress().getAddress().toString(), testUrl2);
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(newProducerResponse, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testNewProducerResponse);
    }

    public void testNewBroker() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        NewBrokerEnDeCoder nbed = new NewBrokerEnDeCoder(der);
        nbed.start();
        BrokerConsumerReferenceEnDeCoder bcred = new BrokerConsumerReferenceEnDeCoder(der);
        bcred.start();
        EndpointReferenceEnDeCoder epred = new EndpointReferenceEnDeCoder(der);
        epred.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        ReferencePropertiesEnDeCoder rped = new ReferencePropertiesEnDeCoder(der); 
        rped.start();
        BrokerIDEnDeCoder bied = new BrokerIDEnDeCoder(der);
        bied.start();
        BrokerProducerReferenceEnDeCoder bpred = new BrokerProducerReferenceEnDeCoder(der);
        bpred.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testNewBroker));
        reader.next();
        NewBroker newBroker = (NewBroker)der.decode(reader);
        Assert.assertEquals(newBroker.getBrokerConsumerReference().getReference().getEndpointAddress().getAddress().toString(),
                            testUrl1);
        Assert.assertEquals(newBroker.getBrokerProducerReference().getReference().getEndpointAddress().getAddress().toString(),
                            testUrl2);
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(newBroker, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testNewBroker);
    }

    public void testNewBrokerRespnse1() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        NewBrokerResponseEnDeCoder nbred = new NewBrokerResponseEnDeCoder(der);
        nbred.start();
        EndProducersEnDeCoder epred = new EndProducersEnDeCoder(der);
        epred.start();
        EndConsumersEnDeCoder ecred = new EndConsumersEnDeCoder(der);
        ecred.start();
        EndpointReferenceEnDeCoder ered = new EndpointReferenceEnDeCoder(der);
        ered.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testNewBrokerResponse1));
        reader.next();
        NewBrokerResponse newBrokerResponse = (NewBrokerResponse)der.decode(reader);
        Assert.assertFalse(!newBrokerResponse.isFirstBroker());
        Assert.assertEquals(newBrokerResponse.getEndProducers().getSequenceType(), "NoProducers");
        Assert.assertEquals(newBrokerResponse.getEndConsumers().getSequenceType(), "EndConsumers");
        Assert.assertEquals(newBrokerResponse.getEndConsumers().getReferenceSequence().get(0).getEndpointAddress().getAddress().toString(),
                            testUrl1);
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(newBrokerResponse, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testNewBrokerResponse1);
    }

    public void testNewBrokerRespnse2() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        NewBrokerResponseEnDeCoder nbred = new NewBrokerResponseEnDeCoder(der);
        nbred.start();
        BrokersEnDeCoder bsed = new BrokersEnDeCoder(der);
        bsed.start();
        BrokerEnDeCoder bed = new BrokerEnDeCoder(der);
        bed.start();
        BrokerConsumerReferenceEnDeCoder bcred = new BrokerConsumerReferenceEnDeCoder(der);
        bcred.start();
        BrokerProducerReferenceEnDeCoder bpred = new BrokerProducerReferenceEnDeCoder(der);
        bpred.start();
        EndpointReferenceEnDeCoder epred = new EndpointReferenceEnDeCoder(der);
        epred.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        ReferencePropertiesEnDeCoder rped = new ReferencePropertiesEnDeCoder(der); 
        rped.start();
        BrokerIDEnDeCoder bied = new BrokerIDEnDeCoder(der);
        bied.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testNewBrokerResponse2));
        reader.next();
        NewBrokerResponse newBrokerResponse = (NewBrokerResponse)der.decode(reader);
        Assert.assertFalse(newBrokerResponse.isFirstBroker());
        Assert.assertEquals(newBrokerResponse.getBrokers().getBrokerSequence().get(0)
                            .getBrokerConsumerReference().getReference().getEndpointAddress().getAddress().toString(),
                            testUrl1);
        Assert.assertEquals(newBrokerResponse.getBrokers().getBrokerSequence().get(0)
                            .getBrokerProducerReference().getReference().getEndpointAddress().getAddress().toString(),
                            testUrl2);
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(newBrokerResponse, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testNewBrokerResponse2);
    }

    public void testRemoveBroker() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        RemoveBrokerEnDeCoder rbed = new RemoveBrokerEnDeCoder(der);
        rbed.start();
        BrokerConsumerReferenceEnDeCoder bcred = new BrokerConsumerReferenceEnDeCoder(der);
        bcred.start();
        EndpointReferenceEnDeCoder epred = new EndpointReferenceEnDeCoder(der);
        epred.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        ReferencePropertiesEnDeCoder rped = new ReferencePropertiesEnDeCoder(der); 
        rped.start();
        BrokerIDEnDeCoder bied = new BrokerIDEnDeCoder(der);
        bied.start();
        NeighborBrokerConsumersEnDeCoder nbced = new NeighborBrokerConsumersEnDeCoder(der);
        nbced.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testRemoveBroker));
        reader.next();
        RemoveBroker removeBroker = (RemoveBroker)der.decode(reader);
        Assert.assertEquals(removeBroker.getBrokerConsumerReference().getReference().getEndpointAddress().getAddress().toString(),
                            testUrl);
        NeighborBrokerConsumers neighborBrokerConsumers = removeBroker.getNeighborBrokerConsumers();
        Assert.assertEquals(neighborBrokerConsumers.getSequenceType(), "BrokerConsumers");
        Iterator<EndpointReference> it = neighborBrokerConsumers.getReferenceSequence().iterator();
        it.next();
        Assert.assertEquals(it.next().getEndpointAddress().getAddress().toString(), testUrl2);
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(removeBroker, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testRemoveBroker);
    }

    public void testReplaceBrokerConnection() throws Exception {
        DefaultEncodingRegistry der = new DefaultEncodingRegistry();
        ReplaceBrokerConnectionEnDeCoder rbced = new ReplaceBrokerConnectionEnDeCoder(der);
        rbced.start();
        RemovedBrokerEnDeCoder rbed = new RemovedBrokerEnDeCoder(der);
        rbed.start();
        EndpointReferenceEnDeCoder epred = new EndpointReferenceEnDeCoder(der);
        epred.start();
        EndpointAddressEnDeCoder eaed = new EndpointAddressEnDeCoder(der);
        eaed.start();
        ReferencePropertiesEnDeCoder rped = new ReferencePropertiesEnDeCoder(der); 
        rped.start();
        BrokerIDEnDeCoder bied = new BrokerIDEnDeCoder(der);
        bied.start();
        BrokerEnDeCoder bed = new BrokerEnDeCoder(der);
        bed.start();
        BrokerConsumerReferenceEnDeCoder bcred = new BrokerConsumerReferenceEnDeCoder(der);
        bcred.start();
        BrokerProducerReferenceEnDeCoder bpred = new BrokerProducerReferenceEnDeCoder(der);
        bpred.start();
        NeighborsEnDeCoder nced = new NeighborsEnDeCoder(der);
        nced.start();
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader reader = xif.createXMLStreamReader(new StringReader(testReplaceBrokerConnection));
        reader.next();
        ReplaceBrokerConnection replaceBrokerConnection = (ReplaceBrokerConnection)der.decode(reader);
        Assert.assertEquals(replaceBrokerConnection.getRemovedBroker().getReference().getEndpointAddress().getAddress().toString(),
                            testUrl);
        Neighbors neighbors = replaceBrokerConnection.getNeighbors();
        Assert.assertEquals(neighbors.getBrokerSequence().get(0)
                            .getBrokerConsumerReference().getReference().getEndpointAddress().getAddress().toString(),
                            testUrl1);
        Assert.assertEquals(neighbors.getBrokerSequence().get(0)
                            .getBrokerProducerReference().getReference().getEndpointAddress().getAddress().toString(),
                            testUrl2);
        
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        StringWriter testWriter = new StringWriter();
        XMLStreamWriter writer = xof.createXMLStreamWriter(testWriter);
        der.encode(replaceBrokerConnection, writer);
        writer.flush();
        String encoded = testWriter.toString();
        Assert.assertEquals(encoded, testReplaceBrokerConnection);
    }
}
