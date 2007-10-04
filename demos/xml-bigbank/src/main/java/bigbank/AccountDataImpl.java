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

package bigbank;

import java.io.IOException;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service(AccountData.class)
public class AccountDataImpl implements AccountData {
    private XMLInputFactory factory = XMLInputFactory.newInstance();

    public XMLStreamReader getAccounts() throws IOException {
        URL doc = getClass().getResource("/accounts.xml");
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(doc.openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reader;
    }

}
