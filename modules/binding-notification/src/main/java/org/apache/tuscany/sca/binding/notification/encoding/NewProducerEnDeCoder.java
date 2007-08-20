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

import javax.xml.namespace.QName;

/**
 * @version $Rev$ $Date$
 */
public class NewProducerEnDeCoder extends EndpointReferenceWrapperEnDeCoder<NewProducer> {

    // QName for the root element
    public static final QName QNAME = new QName(Constants.NOTIFICATION_NS, Constants.NewProducer);

    public NewProducerEnDeCoder(EncodingRegistry registry) {
        super(registry);
    }

    @Override
    protected QName getEncodingObjectQName() {
        return QNAME;
    }

    @Override
    protected Class<NewProducer> getEncodingObjectType() {
        return NewProducer.class;
    }
}
