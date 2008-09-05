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
package org.apache.tuscany.sca.binding.ws.axis2.policy.configuration;

import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.policy.Policy;

/**
 * Implementation for policies that could be injected as parameter
 * into the axis2config.
 *
 * @version $Rev$ $Date$
 */
public class Axis2ConfigParamPolicy implements Policy {
    public static final QName NAME = new QName(Constants.SCA10_TUSCANY_NS, "wsConfigParam");
    private Map<String, OMElement> paramElements = new Hashtable<String, OMElement>();
    
    public Map<String, OMElement> getParamElements() {
        return paramElements;
    }

    public QName getSchemaName() {
        return NAME;
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
    }
}
