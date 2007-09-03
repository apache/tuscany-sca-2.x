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
package org.apache.tuscany.sca.implementation.notification;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.implementation.notification.DefaultNotificationImplementationFactory;
import org.apache.tuscany.sca.implementation.notification.NotificationImplementationProcessor;
import org.easymock.EasyMock;

/**
 * 
 * @version $Rev$ $Date$
 *
 */
public class NotificationImplementationLoaderTestCase extends TestCase {

    public void testRead() throws Exception {
        try {
        NotificationImplementationProcessor implementationLoader =
            new NotificationImplementationProcessor(new DefaultNotificationImplementationFactory());

        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getName()).andReturn(NotificationImplementationProcessor.IMPLEMENTATION_NOTIFICATION).times(2);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("TrafficAdvisoryNotificationTestCase");
        EasyMock.expect(reader.getAttributeValue(null, "type")).andReturn(null);
        EasyMock.expect(reader.hasNext()).andReturn(true);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        
        Implementation impl = implementationLoader.read(reader);
        Assert.assertNotNull(impl);
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
