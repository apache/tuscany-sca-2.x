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
package org.apache.tuscany.sca.contribution.updater.impl;

import java.io.StringReader;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.MetaComponent;

public class MetaComponentHello implements MetaComponent {
    private String componentName;
    private String scdl;
    private boolean loadedFromString = false;
    private Logger log = Logger.getLogger(MetaComponentHello.class.getName());

    public MetaComponentHello() {
    }

    private MetaComponentHello(String scdl, boolean loadedFromString) {
        this.scdl = scdl;
        this.loadedFromString = loadedFromString;
    }

    public MetaComponent fromSCDL(String scdl) {
        return new MetaComponentHello(scdl, true);
    }

    public XMLStreamReader build() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        return factory.createXMLStreamReader(new StringReader(scdl));
    }

    public String getName() {

        return componentName;
    }

}
