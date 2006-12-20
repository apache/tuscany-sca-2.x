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
package org.apache.tuscany.container.javascript.rhino;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.Scope;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;

/**
 * Represents the variable defining the SCA aspects of the script  
 * <code>
 * SCA = {
 *     javaInterface  : "my.pkg.ClassName",
 *     wsdlPortType   : "wsdlPortTypeName",
 *     wsdlNameSpace  : "http://my.namespace.com",
 *     wsdlLocation   : "\wsdl\mywsdl.txt",
 *     properties     : { "foo" : ["java.lang.String", "defaultValue"],},
 *     references     : {},
 *     scope          : 'stateless'|'request'|'conversational'|'composite',
 * }
 * </code>
 * The config must define the service with either javaInterface or wsdl. When
 * using wsdl the three parameters are optional. If wsdlLocation is used that is the 
 * WSDL document used, and the namespace and portType parameters are only required if 
 * the WSDL definition defines multiple portTypes.
 */
public class RhinoSCAConfig {

    private boolean hasSCAConfig;

    private String javaInterface;

    private String wsdlLocation;

    private String wsdlNamespace;

    private String wsdlPortType;

    private Map properties;

    private Map references;
    
    private Scope scope;

    public RhinoSCAConfig(Scriptable scriptScope) {
        Object o = scriptScope.get("SCA", scriptScope);
        if (o != null && UniqueTag.NOT_FOUND != o) {
            hasSCAConfig = true;
            Scriptable scaVar = (Scriptable) o;
            o = scaVar.get("javaInterface", scriptScope);
            if (o != null && UniqueTag.NOT_FOUND != o) {
                this.javaInterface = o.toString();
            }
            o = scaVar.get("wsdlLocation", scriptScope);
            if (o != null && UniqueTag.NOT_FOUND != o) {
                this.wsdlLocation = o.toString();
            }
            o = scaVar.get("wsdlPortType", scriptScope);
            if (o != null && UniqueTag.NOT_FOUND != o) {
                this.wsdlPortType = o.toString();
            }
            o = scaVar.get("wsdlNamespace", scriptScope);
            if (o != null && UniqueTag.NOT_FOUND != o) {
                this.wsdlNamespace = o.toString();
            }
            if (javaInterface != null) {
                if (wsdlLocation != null || wsdlPortType != null || wsdlNamespace != null) {
                    throw new IllegalArgumentException("script SCA config defines both Java and WSDL service interface");
                }
            } else {
                if (wsdlLocation == null && wsdlPortType == null && wsdlNamespace == null) {
                    throw new IllegalArgumentException("script SCA config must define either Java or WSDL service interface");
                }
            }

            this.properties = new HashMap();
            o = scaVar.get("properties", scriptScope);
            if (o != null && UniqueTag.NOT_FOUND != o) {
                // TODO parse properties
            }

            this.references = new HashMap();
            o = scaVar.get("references", scriptScope);
            if (o != null && UniqueTag.NOT_FOUND != o) {
                // TODO parse references
            }
            
            o = scaVar.get("scope", scriptScope);
            if (o != null && UniqueTag.NOT_FOUND != o) {
                if ("stateless".equalsIgnoreCase(String.valueOf(o))) {
                    this.scope = Scope.STATELESS;
                } else if ("request".equalsIgnoreCase(String.valueOf(o))) {
                    this.scope = Scope.REQUEST;
                } else if ("conversational".equalsIgnoreCase(String.valueOf(o))) {
                    this.scope = Scope.SESSION; // TODO: where's CONVERSATION?
                } else if ("composite".equalsIgnoreCase(String.valueOf(o))) {
                    this.scope = Scope.COMPOSITE; // TODO: composite = MODULE for now?
                } else {
                    throw new IllegalArgumentException("invalid scope value: " + o);
                }
            }
            
        }
    }

    public boolean hasSCAConfig() {
        return hasSCAConfig;
    }

    public String getJavaInterface() {
        return javaInterface;
    }

    public Map getProperties() {
        return properties;
    }

    public Map getReferences() {
        return references;
    }

    public String getWSDLLocation() {
        return wsdlLocation;
    }

    public String getWSDLNamespace() {
        return wsdlNamespace;
    }

    public String getWSDLPortType() {
        return wsdlPortType;
    }

    public Scope getScope() {
        return scope;
    }

}
