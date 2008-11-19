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

package org.apache.tuscany.sca.databinding.jaxb;

import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.xml.Node2String;
import org.w3c.dom.Node;

/**
 * @version $Rev$ $Date$
 */
public class JAXBReflectionTestCase extends TestCase {

    public void testGenerateSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance("com.example.ipo.jaxb");
        Node schema = JAXBContextHelper.generateSchema(context);
        System.out.println(new Node2String().transform(schema, null));
    }
}
