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

/**
 * @version $Rev$ $Date$
 */
public interface Constants {

    String NOTIFICATION_NS = "http://docs.oasis-open.org/wsn/b-2";
    String NOTIFICATION_PREFIX = "wsnt";
    String ADDRESSING_NS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    String ADDRESSING_PREFIX = "wsa";
    String Subscribe = "Subscribe";
    String ConsumerReference = "ConsumerReference";
    String Address = "Address";
    String ReferenceProperties = "ReferenceProperties";
    String EndpointReference = "EndpointReference";
    String BrokerID = "BrokerID";
    String NewConsumer = "NewConsumer";
    String NewProducer = "NewProducer";
    String NewConsumerResponse = "NewConsumerResponse";
    String NewProducerResponse = "NewProducerResponse";
    String ConsumerSequenceType = "ConsumerSequenceType";
    String EndConsumers = "EndConsumers";
    String BrokerConsumers = "BrokerConsumers";
    String NoConsumers = "NoConsumers";
    String ProducerSequenceType = "ProducerSequenceType";
    String EndProducers = "EndProducers";
    String BrokerProducers = "BrokerProducers";
    String NoProducers = "NoProducers";
    String Broker = "Broker";
    String NewBroker = "NewBroker";
    String NewBrokerAck = "NewBrokerAck";
    String BrokerConsumerReference = "BrokerConsumerReference";
    String BrokerProducerReference = "BrokerProducerReference";
    String NewBrokerResponse = "NewBrokerResponse";
    String FirstBroker = "FirstBroker";
    String Brokers = "Brokers";
    String ConnectionOverride = "ConnectionOverride";
    String ConnectionOverrideResponse = "ConnectionOverrideResponse";
    String NeighborBrokerConsumers = "NeighborBrokerConsumers";
    String RemoveBroker = "RemoveBroker";
    String RemovedBroker = "RemovedBroker";
    String Neighbors = "Neighbors";
    String ReplaceBrokerConnection = "ReplaceBrokerConnection";
    
    String SUBSCRIBE_OP = "subscribe";
    String CONNECTION_OVERRIDE_OP = "connectionOverride";
    String NEW_CONSUMER_OP = "newConsumer";
    String NEW_PRODUCER_OP = "newProducer";
    String NEW_BROKER_OP = "newBroker";
    String NEW_BROKER_ACK_OP = "newBrokerAck";
    String REMOVE_BROKER_OP = "removeBroker";
    String REPLACE_BROKER_CONNECTION_OP = "replaceBrokerConnection";

    String Broker_ID = "brokerID";
}
