/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.sca.binding.rmi;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.PolicySet;


/**
 * Represents a binding to an RMI service.
 *
 * @version $Rev: 490475 $ $Date: 2006-12-27 15:53:42 +0530 (Wed, 27 Dec 2006) $
 */
public class RMIBindingImpl implements RMIBinding, RMIBindingConstants {
    private String name;
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private String host;
    private String port;
    private String serviceName;
    private List<Object> extensions = new ArrayList<Object>();
    
    protected RMIBindingImpl() {
    }

    public String getURI() {
        return host + COLON + port + serviceName;
    }

    public String getRmiHostName() {
        if (host == null) {
            extractFromUri();
        }
        return host;
    }

    public void setRmiHostName(String host) {
        this.host = host;
    }

    public String getRmiPort() {
        if (port == null) {
            extractFromUri();
        }
        return port;
    }

    public void setRmiPort(String port) {
        this.port = port;
    }

    public String getRmiServiceName() {
        if (serviceName == null) {
            extractFromUri();
        }
        return serviceName;
    }

    public void setRmiServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }
    
    public List<Object> getExtensions() {
        return extensions;
    }
    
    public boolean isUnresolved() {
        return false;
    }
    
    public void setUnresolved(boolean unresolved) {
    }

    private void extractFromUri() {
        if (getURI() != null && getURI().length() > 0) {
            int colonIndex = getURI().indexOf(COLON);
            if (colonIndex != -1) {
                setRmiHostName(getURI().substring(0, colonIndex));
                setRmiPort(getURI().substring(colonIndex + 1));
            } 
            
            int slashIndex = getURI().indexOf(FWD_SLASH);
            if (slashIndex != -1) {
                if (colonIndex == -1) {
                    setRmiHostName(getURI().substring(0, slashIndex));
                }
                setRmiServiceName(getURI().substring(slashIndex + 1));
            } else {
                setRmiServiceName(getURI());
            }
        }
    }
}
