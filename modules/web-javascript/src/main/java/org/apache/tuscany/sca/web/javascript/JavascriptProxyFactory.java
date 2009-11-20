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
import java.io.InputStream;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.ComponentReference;

/**
 * Javascript Proxy Factory used to allow Web related bindings to generate 
 * client js proxyies
 * 
 * @version $Rev$ $Date$
 */
public interface JavascriptProxyFactory {

    /**
     * The binding model type associated with this factory
     * @return the binding model type
     */
    Class<?> getModelType(); 
    
    /**
     * The binding qname associated with this factory
     * @return the binding qname
     */
    QName getQName();
    
    /**
     * Get the Javascript proxy client file name
     * @return the javascript file name
     */
    String getJavascriptProxyFile();
    
    /**
     * Get the Javascript proxy client contents as a stream
     * @return
     */
    InputStream getJavascriptProxyFileAsStream() throws IOException;

    
    /**
     * Create a JavaScript Proxy for a given reference
     * @param componentReference The reference
     * @return the javascript proxy code
     * @throws IOException
     */
    String createJavascriptReference(ComponentReference componentReference) throws IOException;
}
