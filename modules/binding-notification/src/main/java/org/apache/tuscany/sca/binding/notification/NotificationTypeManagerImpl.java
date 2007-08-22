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
package org.apache.tuscany.sca.binding.notification;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import org.apache.tuscany.sca.binding.notification.encoding.Broker;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerConsumerReference;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerID;
import org.apache.tuscany.sca.binding.notification.encoding.BrokerProducerReference;
import org.apache.tuscany.sca.binding.notification.encoding.Brokers;
import org.apache.tuscany.sca.binding.notification.encoding.Constants;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingException;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingObject;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingRegistry;
import org.apache.tuscany.sca.binding.notification.encoding.EncodingUtils;
import org.apache.tuscany.sca.binding.notification.encoding.EndConsumers;
import org.apache.tuscany.sca.binding.notification.encoding.EndProducers;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointAddress;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointReference;
import org.apache.tuscany.sca.binding.notification.encoding.EndpointReferenceWrapper;
import org.apache.tuscany.sca.binding.notification.encoding.NeighborBrokerConsumers;
import org.apache.tuscany.sca.binding.notification.encoding.Neighbors;
import org.apache.tuscany.sca.binding.notification.encoding.NewBroker;
import org.apache.tuscany.sca.binding.notification.encoding.NewBrokerAck;
import org.apache.tuscany.sca.binding.notification.encoding.NewBrokerResponse;
import org.apache.tuscany.sca.binding.notification.encoding.NewConsumer;
import org.apache.tuscany.sca.binding.notification.encoding.NewConsumerResponse;
import org.apache.tuscany.sca.binding.notification.encoding.NewProducer;
import org.apache.tuscany.sca.binding.notification.encoding.NewProducerResponse;
import org.apache.tuscany.sca.binding.notification.encoding.RemoveBroker;
import org.apache.tuscany.sca.binding.notification.encoding.RemovedBroker;
import org.apache.tuscany.sca.binding.notification.encoding.ReplaceBrokerConnection;
import org.apache.tuscany.sca.binding.notification.util.IOUtils;
import org.apache.tuscany.sca.binding.notification.util.NotificationServlet;
import org.apache.tuscany.sca.binding.notification.util.URIUtil;
import org.apache.tuscany.sca.binding.notification.util.IOUtils.IOUtilsException;
import org.apache.tuscany.sca.binding.notification.util.IOUtils.ReadableContinuation;
import org.apache.tuscany.sca.binding.notification.util.IOUtils.Writeable;
import org.apache.tuscany.sca.binding.notification.util.NotificationServlet.NotificationServletStreamHandler;
import org.apache.tuscany.sca.host.http.ServletHost;

/**
 * A notification type manager serves as a registry of producers and consumers, or brokers, for
 * any notification type. This class implements an interface that allows a reference provider
 * (a producer), a service provider (a consumer), or both (a broker, via the provider factory),
 * to access locally the ntm for its notification type, regardless of whether the ntm resides
 * locally or remotely.
 * At a given host there is only one reference provider and/or one service provider for any given
 * notification type. So, if the ntm for a notification type resides locally, then it is invoked
 * exclusively by either a reference provider (newProducer), a service provider (newConsumer), or
 * a provider factory (newBroker). And since these invocations occur when the providers are being
 * created then all three of consumerLists, producerLists and brokerLists must be null when these
 * invocations occur.
 * 
 * @version $Rev$ $Date$
 */
public class NotificationTypeManagerImpl implements NotificationTypeManager {

    private static final String ntmPathBase = "/ntm";

    private ServletHost servletHost;
    private EncodingRegistry encodingRegistry;
    private Map<URI, NotificationTypeManagerHandler> ntmHandlers;
    
    public NotificationTypeManagerImpl() {
    }

    public void setServletHost(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    public void setEncodingRegistry(EncodingRegistry encodingRegistry) {
        this.encodingRegistry = encodingRegistry;
    }

    public void init() {
        ntmHandlers = new HashMap<URI, NotificationTypeManagerHandler>();
    }
    
    public String newConsumer(URI notificationType, URL consumerUrl, URL remoteNtmUrl, List<URL> producerListResult) {
        if (ntmUrlIsRemote(consumerUrl, remoteNtmUrl)) {
            try {
                WriteableEPW wEPW = new WriteableEPW(new NewConsumer(), consumerUrl);
                InputStreamDecoder isd = new InputStreamDecoder();
                NewConsumerResponse ncr =
                    (NewConsumerResponse)IOUtils.sendHttpRequest(remoteNtmUrl, Constants.NEW_CONSUMER_OP, wEPW, isd);
                String sequenceType = ncr.getSequenceType();
                if (Constants.EndProducers.equals(sequenceType) || Constants.BrokerProducers.equals(sequenceType)) {
                    for (EndpointReference epr : ncr.getReferenceSequence()) {
                        producerListResult.add(epr.getEndpointAddress().getAddress());
                    }
                }
                return sequenceType;
            } catch(Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            NotificationTypeManagerHandler ntmHandler = ntmHandlers.get(notificationType);
            if (ntmHandler != null) {
                throw new RuntimeException("Trying to deploy local consumer with existing local producer, consumer or broker");
            }
            
            createNtmHandler(consumerUrl.getAuthority(), notificationType, consumerUrl, null, null);
            
            return Constants.NoProducers;
        }
    }
    
    private void createNtmHandler(String ntmUriAuthority, URI notificationType, URL consumerUrl, URL producerUrl, BrokerStruct broker) {
        String ntmUri = "http://" + ntmUriAuthority + ntmPathBase + URIUtil.getPath(notificationType);
        NotificationTypeManagerHandler ntmh = new NotificationTypeManagerHandler(notificationType, consumerUrl, producerUrl, broker);
        ntmHandlers.put(notificationType, ntmh);
        servletHost.addServletMapping(ntmUri, new NotificationServlet(ntmh));
    }
    
    public String newProducer(URI notificationType, URL producerUrl, URL remoteNtmUrl, List<URL> consumerListResult) {
        if (ntmUrlIsRemote(producerUrl, remoteNtmUrl)) {
            try {
                WriteableEPW wEPW = new WriteableEPW(new NewProducer(), producerUrl);
                InputStreamDecoder isd = new InputStreamDecoder();
                NewProducerResponse npr =
                    (NewProducerResponse)IOUtils.sendHttpRequest(remoteNtmUrl, Constants.NEW_PRODUCER_OP, wEPW, isd);
                String sequenceType = npr.getSequenceType();
                if (Constants.EndConsumers.equals(sequenceType) || Constants.BrokerConsumers.equals(sequenceType)) {
                    for (EndpointReference epr : npr.getReferenceSequence()) {
                        consumerListResult.add(epr.getEndpointAddress().getAddress());
                    }
                }
                return sequenceType;
            } catch(Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            NotificationTypeManagerHandler ntmHandler = ntmHandlers.get(notificationType);
            if (ntmHandler != null) {
                throw new RuntimeException("Trying to deploy local producer with existing local producer, consumer or broker");
            }
            
            createNtmHandler(producerUrl.getAuthority(), notificationType, null, producerUrl, null);
            
            return Constants.NoConsumers;
        }
    }
    
    public boolean newBroker(URI notificationType,
                             URL consumerUrl,
                             URL producerUrl,
                             String brokerID,
                             URL remoteNtmUrl,
                             List<EndpointReference> consumerListResult,
                             List<EndpointReference> producerListResult) {
        String ntmUriAuthority = producerUrl.getAuthority();
        if (!ntmUriAuthority.equals(consumerUrl.getAuthority())) {
            throw new RuntimeException("Producer url and consumer url do not match");
        }
        if (ntmUrlIsRemote(producerUrl, remoteNtmUrl)) {
            try {
                WriteableNewBroker wnb = new WriteableNewBroker(consumerUrl, producerUrl, brokerID);
                InputStreamDecoder isd = new InputStreamDecoder();
                NewBrokerResponse nbr =
                    (NewBrokerResponse)IOUtils.sendHttpRequest(remoteNtmUrl, Constants.NEW_BROKER_OP, wnb, isd);
                if (nbr.isFirstBroker()) {
                    if (nbr.getEndConsumers().getSequenceType().equals(Constants.EndConsumers)) {
                        for (EndpointReference epr : nbr.getEndConsumers().getReferenceSequence()) {
                            consumerListResult.add(epr);
                        }
                    }
                    if (nbr.getEndProducers().getSequenceType().equals(Constants.EndProducers)) {
                        for (EndpointReference epr : nbr.getEndProducers().getReferenceSequence()) {
                            producerListResult.add(epr);
                        }
                    }
                }
                else {
                    for (Broker broker : nbr.getBrokers().getBrokerSequence()) {
                        consumerListResult.add(broker.getBrokerConsumerReference().getReference());
                        producerListResult.add(broker.getBrokerProducerReference().getReference());
                    }
                }
                return nbr.isFirstBroker();
            } catch(Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            NotificationTypeManagerHandler ntmHandler = ntmHandlers.get(notificationType);
            if (ntmHandler != null) {
                throw new RuntimeException("Trying to deploy local broker with existing local producer, consumer or broker");
            }
            
            BrokerStruct broker = new BrokerStruct(consumerUrl, producerUrl, brokerID);
            createNtmHandler(ntmUriAuthority, notificationType, null, null, broker);
            
            return true;
        }
    }
    
    private boolean ntmUrlIsRemote(URL localUrl, URL ntmUrl) {
        if (ntmUrl == null) {
            return false;
        }
        if (localUrl.getPort() != ntmUrl.getPort()) {
            return true;
        }
        String remoteNtmUrlAuthority = ntmUrl.getAuthority();
        if (remoteNtmUrlAuthority.indexOf("localhost") >= 0) {
            return false;
        }
        return !localUrl.getAuthority().equals(remoteNtmUrlAuthority);
    }
    
    public void newBrokerAck(URL remoteNtmUrl) {
        try {
            IOUtils.sendHttpRequest(remoteNtmUrl, Constants.NEW_BROKER_ACK_OP, new WriteableNewBrokerAck(), null);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void removeBroker(EndpointReference brokerConsumerEpr, List<EndpointReference> neighborBrokerConsumerEprs, URL remoteNtmUrl) {
        WriteableRemoveBroker wrb = new WriteableRemoveBroker(brokerConsumerEpr, neighborBrokerConsumerEprs);
        
        try {
            IOUtils.sendHttpRequest(remoteNtmUrl, Constants.REMOVE_BROKER_OP, wrb, null);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private class NotificationTypeManagerHandler implements NotificationServletStreamHandler {
        
        private URI notificationType;
        List<URL> consumerList;
        List<URL> producerList;
        List<BrokerStruct> brokerList;
        private NotificationTypeLock notificationTypeLock;
        private BrokerStruct pendingBroker;
        
        public NotificationTypeManagerHandler(URI notificationType) {
            this.notificationType = notificationType;
            this.notificationTypeLock = new NotificationTypeLock();
            this.pendingBroker = null;
        }
        
        public NotificationTypeManagerHandler(URI notificationType, URL consumerUrl, URL producerUrl, BrokerStruct broker) {
            this(notificationType);
            if (consumerUrl != null) {
                addConsumer(consumerUrl);
            }
            else if (producerUrl != null) {
                addProducer(producerUrl);
            }
            else if (broker != null) {
                addBroker(broker);
            }
        }
        
        private void addConsumer(URL consumerUrl) {
            if (consumerList == null) {
                consumerList = new ArrayList<URL>();
            }
            consumerList.add(consumerUrl);
        }
        
        private void addProducer(URL producerUrl) {
            if (producerList == null) {
                producerList = new ArrayList<URL>();
            }
            producerList.add(producerUrl);
        }
        
        private void addBroker(BrokerStruct broker) {
            if (brokerList == null) {
                brokerList = new ArrayList<BrokerStruct>();
            }
            brokerList.add(broker);
        }
        
        public void handle(Map<String, String> headers, ServletInputStream istream, int contentLength, ServletOutputStream ostream) {
            String opHeader = headers.get(IOUtils.Notification_Operation);
            EncodingObject eo = null;
            try {
                eo = EncodingUtils.decodeFromStream(encodingRegistry, istream);
            }
            catch(EncodingException e) {
                throw new RuntimeException(e);
            }
            
            if (Constants.NEW_CONSUMER_OP.equals(opHeader)) {
                handleNewConsumer((NewConsumer)eo, ostream);
            }
            else if(Constants.NEW_PRODUCER_OP.equals(opHeader)) {
                handleNewProducer((NewProducer)eo, ostream);
            }
            else if(Constants.NEW_BROKER_OP.equals(opHeader)) {
                handleNewBroker((NewBroker)eo, ostream);
            }
            else if (Constants.NEW_BROKER_ACK_OP.equals(opHeader)) {
                handleNewBrokerAck();
            }
            else if (Constants.REMOVE_BROKER_OP.equals(opHeader)) {
                handleRemoveBroker((RemoveBroker)eo);
            }
        }
        
        private void handleNewConsumer(NewConsumer nc, ServletOutputStream ostream) {
            synchronized(notificationTypeLock) {
                if (notificationTypeLock.isLocked) {
                    try { notificationTypeLock.wait(); } catch(InterruptedException e) {}
                }
                URL consumerUrl = nc.getReference().getEndpointAddress().getAddress();
                if (brokerList == null) {
                    addConsumer(consumerUrl);
                }
    
                NewConsumerResponse ncr = new NewConsumerResponse();
                if (producerList != null) {
                    ncr.setSequenceType(Constants.EndProducers);
                    for (URL producerUrl : producerList) {
                        ncr.addReferenceToSequence(EncodingUtils.createEndpointReference(producerUrl, null));
                    }
                }
                else if(brokerList != null) {
                    ncr.setSequenceType(Constants.BrokerProducers);
                    for (BrokerStruct broker : brokerList) {
                        ncr.addReferenceToSequence(EncodingUtils.createEndpointReference(broker.producerUrl, null));
                    }
                }
                else {
                    ncr.setSequenceType(Constants.NoProducers);
                }
                try {
                    EncodingUtils.encodeToStream(encodingRegistry, ncr, ostream);
                }
                catch(IOUtilsException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        private void handleNewProducer(NewProducer np, ServletOutputStream ostream) {
            synchronized(notificationTypeLock) {
                if (notificationTypeLock.isLocked) {
                    try { notificationTypeLock.wait(); } catch(InterruptedException e) {}
                }
                URL producerUrl = np.getReference().getEndpointAddress().getAddress();
                if (brokerList == null) {
                    addProducer(producerUrl);
                }
    
                NewProducerResponse npr = new NewProducerResponse();
                if (consumerList != null) {
                    npr.setSequenceType(Constants.EndConsumers);
                    for (URL consumerUrl : consumerList) {
                        npr.addReferenceToSequence(EncodingUtils.createEndpointReference(consumerUrl, null));
                    }
                }
                else if(brokerList != null) {
                    npr.setSequenceType(Constants.BrokerConsumers);
                    for (BrokerStruct broker : brokerList) {
                        npr.addReferenceToSequence(EncodingUtils.createEndpointReference(broker.consumerUrl, null));
                    }
                }
                else {
                    npr.setSequenceType(Constants.NoConsumers);
                }
                try {
                    EncodingUtils.encodeToStream(encodingRegistry, npr, ostream);
                }
                catch(IOUtilsException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        private void handleNewBroker(NewBroker nb, ServletOutputStream ostream) {
            synchronized(notificationTypeLock) {
                if (notificationTypeLock.isLocked) {
                    try { notificationTypeLock.wait(); } catch(InterruptedException e) {}
                }
               NewBrokerResponse nbr = new NewBrokerResponse();
                if (consumerList != null || producerList != null || brokerList == null) {
                    nbr.setFirstBroker(true);
                    EndConsumers endConsumers = new EndConsumers();
                    if (consumerList != null) {
                        endConsumers.setSequenceType(Constants.EndConsumers);
                        for (URL consumerUrl : consumerList) {
                            endConsumers.addReferenceToSequence(EncodingUtils.createEndpointReference(consumerUrl, null));
                        }
                    }
                    else {
                        endConsumers.setSequenceType(Constants.NoConsumers);
                    }
                    nbr.setEndConsumers(endConsumers);
                    EndProducers endProducers = new EndProducers();
                    if (producerList != null) {
                        endProducers.setSequenceType(Constants.EndProducers);
                        for (URL producerUrl : producerList) {
                            endProducers.addReferenceToSequence(EncodingUtils.createEndpointReference(producerUrl, null));
                        }
                    }
                    else {
                        endProducers.setSequenceType(Constants.NoProducers);
                    }
                    nbr.setEndProducers(endProducers);
                }
                else {
                    nbr.setFirstBroker(false);
                    Brokers brokers = new Brokers();
                    for (BrokerStruct brokerStruct : brokerList) {
                        Broker brokerElt = new Broker();
                        BrokerConsumerReference bcr = new BrokerConsumerReference();
                        bcr.setReference(EncodingUtils.createEndpointReference(brokerStruct.consumerUrl, brokerStruct.brokerID));
                        brokerElt.setBrokerConsumerReference(bcr);
                            
                        BrokerProducerReference bpr = new BrokerProducerReference();
                        bpr.setReference(EncodingUtils.createEndpointReference(brokerStruct.producerUrl, brokerStruct.brokerID));
                        brokerElt.setBrokerProducerReference(bpr);
                        brokers.addBrokerToSequence(brokerElt);
                    }
                    nbr.setBrokers(brokers);
                }
                EndpointReference consumerEPR = nb.getBrokerConsumerReference().getReference();
                URL consumerUrl = consumerEPR.getEndpointAddress().getAddress();
                BrokerID consumerBrokerID = consumerEPR.getReferenceProperties().getProperty(BrokerID.class);
                EndpointReference producerEPR = nb.getBrokerProducerReference().getReference(); 
                URL producerUrl = producerEPR.getEndpointAddress().getAddress();
                BrokerID producerBrokerID = producerEPR.getReferenceProperties().getProperty(BrokerID.class);
                if (consumerBrokerID == null ||
                        producerBrokerID == null ||
                            !consumerBrokerID.getID().equals(producerBrokerID.getID())) {
                    throw new RuntimeException("Producer and consumer broker ids do not match");
                }
                // only add broker if consumerList == null && producerList == null
                // otherwise, make it a pending broker and wait for ack
                // TODO block for a configurable amount of time
                BrokerStruct broker = new BrokerStruct(consumerUrl, producerUrl, consumerBrokerID.getID());
                if (consumerList == null && producerList == null) {
                    addBroker(broker);
                }
                else {
                    pendingBroker = broker;
                    notificationTypeLock.isLocked = true;
                }
                try {
                    EncodingUtils.encodeToStream(encodingRegistry, nbr, ostream);
                }
                catch(IOUtilsException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        private void handleNewBrokerAck() {
            synchronized(notificationTypeLock) {
                if (!notificationTypeLock.isLocked) {
                    notificationTypeLock.notifyAll();
                    throw new RuntimeException("Notification type should be locked");
                }
                if (brokerList != null) {
                    notificationTypeLock.isLocked = false;
                    notificationTypeLock.notifyAll();
                    throw new RuntimeException("Can't add pending broker to non-empty broker list");
                }
                if (pendingBroker == null) {
                    notificationTypeLock.isLocked = false;
                    notificationTypeLock.notifyAll();
                    throw new RuntimeException("Missing pending broker");
                }
                addBroker(pendingBroker);
                consumerList = null;
                producerList = null;
                pendingBroker = null;
                notificationTypeLock.isLocked = false;
                notificationTypeLock.notifyAll();
            }
        }
        
        private void handleRemoveBroker(RemoveBroker rb) {
            synchronized(notificationTypeLock) {
                if (notificationTypeLock.isLocked) {
                    try { notificationTypeLock.wait(); } catch(InterruptedException e) {}
                }
                
                if (brokerList == null) {
                    throw new RuntimeException("No broker to remove for [" + notificationType + "]");
                }
                
                NeighborBrokerConsumers nbcs = rb.getNeighborBrokerConsumers();
                EndpointReference rbEpr = rb.getBrokerConsumerReference().getReference();
                if (nbcs != null && nbcs.getReferenceSequence() != null) {
                    List<Broker> neighborBrokers = new ArrayList<Broker>();
                    for (EndpointReference neighborBrokerConsumerEpr : nbcs.getReferenceSequence()) {
                        BrokerStruct neighborBrokerStruct = null;
                        URL neighborBrokerConsumerEprUrl = neighborBrokerConsumerEpr.getEndpointAddress().getAddress();
                        for (BrokerStruct brokerStruct : brokerList) {
                            if (brokerStruct.consumerUrl.equals(neighborBrokerConsumerEprUrl)) {
                                neighborBrokerStruct = brokerStruct;
                                break;
                            }
                        }
                        if (neighborBrokerStruct == null) {
                            throw new RuntimeException("Can't find neighbor broker for consumer EPR [" +
                                                       neighborBrokerConsumerEprUrl + "]");
                        }
                        BrokerConsumerReference bcr = new BrokerConsumerReference();
                        bcr.setReference(EncodingUtils.createEndpointReference(neighborBrokerStruct.consumerUrl, neighborBrokerStruct.brokerID));
                        BrokerProducerReference bpr = new BrokerProducerReference();
                        bpr.setReference(EncodingUtils.createEndpointReference(neighborBrokerStruct.producerUrl, neighborBrokerStruct.brokerID));
                        Broker neighborBroker = new Broker();
                        neighborBroker.setBrokerConsumerReference(bcr);
                        neighborBroker.setBrokerProducerReference(bpr);
                        neighborBrokers.add(neighborBroker);
                    }
                    int lastIndex = neighborBrokers.size() - 1;
                    for (int index = lastIndex; index >= 0; index--) {
                        List<Broker> writeableNeighborBrokers = ((index > 0) ? neighborBrokers.subList(0, index) : null);
                        WriteableReplaceBrokerConnection wrbc = new WriteableReplaceBrokerConnection(rbEpr, writeableNeighborBrokers);
                        URL targetUrl =
                            neighborBrokers.get(index).getBrokerProducerReference().getReference().getEndpointAddress().getAddress();
                        try {
                            IOUtils.sendHttpRequest(targetUrl, Constants.REPLACE_BROKER_CONNECTION_OP, wrbc, null);
                        } catch(Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                }
                
                BrokerStruct removedBrokerStruct = null;
                URL rbEprUrl = rbEpr.getEndpointAddress().getAddress();
                for (BrokerStruct brokerSruct : brokerList) {
                    if (brokerSruct.consumerUrl.equals(rbEprUrl)) {
                        removedBrokerStruct = brokerSruct;
                        break;
                    }
                }
                if (removedBrokerStruct == null) {
                    throw new RuntimeException("Can't find broker to remove for EPR [" + rbEprUrl + "]");
                }
                if(!brokerList.remove(removedBrokerStruct)) {
                    throw new RuntimeException("Broker was not removed");
                }
            }
        }
    }
    
    class NotificationTypeLock {
        public boolean isLocked;
    }
    
    class WriteableEPW implements Writeable {
        private EndpointReferenceWrapper epw;
        
        public WriteableEPW(EndpointReferenceWrapper epw, URL url) {
            EndpointAddress epa = new EndpointAddress();
            epa.setAddress(url);
            EndpointReference epr = new EndpointReference();
            epr.setEndpointAddress(epa);
            epw.setReference(epr);
            this.epw = epw;
        }
        
        public void write(OutputStream os) throws IOUtilsException {
            EncodingUtils.encodeToStream(encodingRegistry, epw, os);
        }
    }
    
    class InputStreamDecoder implements ReadableContinuation {
        
        public Object read(InputStream istream) throws IOUtilsException {
            try {
                return EncodingUtils.decodeFromStream(encodingRegistry, istream);
            }
            catch(EncodingException e) {
                throw new IOUtilsException(e);
            }
        }
    }
    
    class BrokerStruct {
        public URL consumerUrl;
        public URL producerUrl;
        public String brokerID;
        
        public BrokerStruct(URL consumerUrl, URL producerUrl, String brokerID) {
            this.consumerUrl = consumerUrl;
            this.producerUrl = producerUrl;
            this.brokerID = brokerID;
        }
    }
    
    class WriteableNewBroker implements Writeable {
        private NewBroker newBroker;
        
        public WriteableNewBroker(URL consumerUrl, URL producerUrl, String brokerID) {
            newBroker = new NewBroker();
            BrokerConsumerReference bcr = new BrokerConsumerReference();
            bcr.setReference(EncodingUtils.createEndpointReference(consumerUrl, brokerID));
            newBroker.setBrokerConsumerReference(bcr);
            
            BrokerProducerReference bpr = new BrokerProducerReference();
            bpr.setReference(EncodingUtils.createEndpointReference(producerUrl, brokerID));
            newBroker.setBrokerProducerReference(bpr);
        }
        
        public void write(OutputStream os) throws IOUtilsException {
            EncodingUtils.encodeToStream(encodingRegistry, newBroker, os);
        }
    }

    class WriteableNewBrokerAck implements Writeable {
        public void write(OutputStream os) throws IOUtilsException {
            EncodingUtils.encodeToStream(encodingRegistry, new NewBrokerAck(), os);
        }
    }
    
    class WriteableRemoveBroker implements Writeable {
        private RemoveBroker removeBroker;
        
        public WriteableRemoveBroker(EndpointReference brokerConsumerEpr, List<EndpointReference> neighborBrokerConsumerEprs) {
            removeBroker = new RemoveBroker();
            BrokerConsumerReference brokerConsumerReference = new BrokerConsumerReference();
            brokerConsumerReference.setReference(brokerConsumerEpr);
            removeBroker.setBrokerConsumerReference(brokerConsumerReference);
            if (neighborBrokerConsumerEprs != null) {
                NeighborBrokerConsumers neighborBrokerConsumers = new NeighborBrokerConsumers();
                neighborBrokerConsumers.setReferenceSequence(neighborBrokerConsumerEprs);
                neighborBrokerConsumers.setSequenceType(Constants.BrokerConsumers);
                removeBroker.setNeighborBrokerConsumers(neighborBrokerConsumers);
            }
        }
        
        public void write(OutputStream os) throws IOUtilsException {
            EncodingUtils.encodeToStream(encodingRegistry, removeBroker, os);
        }
    }
    
    class WriteableReplaceBrokerConnection implements Writeable {
        private ReplaceBrokerConnection replaceBrokerConnection;
        
        public WriteableReplaceBrokerConnection(EndpointReference removedBrokerEpr, List<Broker> brokerSequence) {
            replaceBrokerConnection = new ReplaceBrokerConnection();
            RemovedBroker removedBroker = new RemovedBroker();
            removedBroker.setReference(removedBrokerEpr);
            replaceBrokerConnection.setRemovedBroker(removedBroker);
            if (brokerSequence != null) {
                Neighbors neighbors = new Neighbors();
                neighbors.setBrokerSequence(brokerSequence);
                replaceBrokerConnection.setNeighbors(neighbors);
            }
        }
        
        public void write(OutputStream os) throws IOUtilsException {
            EncodingUtils.encodeToStream(encodingRegistry, replaceBrokerConnection, os);
        }
    }
}
