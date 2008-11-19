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

package org.apache.tuscany.sca.implementation.web;

import java.util.ArrayList;
import java.util.List;


public class DefaultContextScriptProcessorExtensionPoint implements ContextScriptProcessorExtensionPoint {

    protected ComponentContextServlet componentContextServlet;
    protected List<ContextScriptProcessor> tempCSPHolder = new ArrayList<ContextScriptProcessor>();
    
    public DefaultContextScriptProcessorExtensionPoint() {
    }

    public void addContextScriptProcessor(ContextScriptProcessor csp) {
        if (componentContextServlet != null) {
            componentContextServlet.addContextScriptProcessor(csp);
        } else {
            tempCSPHolder.add(csp); 
        }
    }
    
    public void setComponentContextServlet(ComponentContextServlet servlet) {
        componentContextServlet = servlet;
        if (tempCSPHolder.size() > 0) {
            for (ContextScriptProcessor csp : tempCSPHolder) {
                componentContextServlet.addContextScriptProcessor(csp);
            }
        }
    }

}
