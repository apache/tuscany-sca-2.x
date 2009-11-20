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

package org.apache.tuscany.sca.web.javascript;

import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.runtime.RuntimeComponent;


/**
 * Widget component script generator interface
 * This generates the necessary JavaScript client code into a single JavaScript per component
 * 
 * @version $Rev$ $Date$
 */
public interface ComponentJavaScriptGenerator {
    
    /**
     * Return the QName that identify the Component script generator
     * This is used to identify different generators supporting different JavaScript frameworks
     * 
     * @return The QName
     */
    QName getQName();
    
    /**
     * Generate the Java Script code for a given component
     *   - generate/append client proxyies as needed
     *   - generate tuscany namespace as needed
     *   - generate properties for JavaScript injection
     *   - generate references for JavaScript injection
     *     
     * @param component The SCA Component to be used 
     * @param pw A Print Writer where the script should be written to 
     * @throws IOException
     */
    void generateJavaScriptCode(RuntimeComponent component, PrintWriter pw) throws IOException;

}
