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
package workpool;

import java.io.StringReader;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.MetaComponent;
import org.apache.tuscany.sca.assembly.impl.DefaultMetaComponent;

public class MetaComponentWorker extends DefaultMetaComponent {

    private SecureRandom prng;
    private String componentName;
    private String scdl;
    private String javaClass;
    private boolean loadedFromString = false;
    private Logger log = Logger.getLogger(MetaComponentWorker.class.getName());

    public MetaComponentWorker() {
        componentName = "WorkerComponent"
                + java.util.UUID.randomUUID().toString();
    }

    public void setWorkerName(String componentName) {
        this.componentName = componentName;
    }

    public void setWorkerClass(String javaClass) {
        this.javaClass = javaClass;
    }

    private String generateSCDL() {
        StringBuffer buffer = new StringBuffer(512);
        buffer
                .append("<component xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" name=\"");
        buffer.append(this.componentName);
        buffer.append("\">\n");
        buffer.append("<implementation.java class=\"");
        buffer.append(this.javaClass);
        buffer.append("\"/>");
        buffer.append("<property name=\"workerName\">");
        buffer.append(this.componentName);
        buffer.append("</property>\n</component>");
        return buffer.toString();
    }

    @Override
    public XMLStreamReader build() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        if (!loadedFromString)
            scdl = generateSCDL();
        return factory.createXMLStreamReader(new StringReader(scdl));

    }

    public String getName() {

        return componentName;
    }

}
