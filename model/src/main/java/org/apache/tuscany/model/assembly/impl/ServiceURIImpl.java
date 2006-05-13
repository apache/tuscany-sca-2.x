/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.model.assembly.impl;

import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceURI;

/**
 * An implementation of ServiceURI.
 */
public class ServiceURIImpl implements ServiceURI {

    private String address;
    private Boolean isSCAScheme;
    private boolean isParsed;
    private String moduleComponentName;
    private String partName;
    private String serviceName;

    /**
     * Constructs a new ServiceURIImpl.
     * @param address
     */
    protected ServiceURIImpl(String address) {
        this.address = address;
    }

    /**
     * Constructs a new ServiceURIImpl.
     * @param moduleComponent
     * @param partName
     * @param portName
     */
    protected ServiceURIImpl(ModuleComponent moduleComponent, String partName, String portName) {
         if (moduleComponent != null)
             moduleComponentName = moduleComponent.getName();
         else
             moduleComponentName = "";
        this.partName = partName;
        this.serviceName = portName;
    }

    /**
     * Constructs a new ServiceURIImpl.
     * @param moduleComponent
     * @param configuredPort
     */
    protected ServiceURIImpl(ModuleComponent moduleComponent, Part part, ConfiguredPort configuredPort) {
        if (moduleComponent != null)
            moduleComponentName = moduleComponent.getName();
        else
            moduleComponentName = "";
        if (configuredPort instanceof ConfiguredService) {
            partName = part.getName();
            ConfiguredService configuredService = (ConfiguredService) configuredPort;
            Service service = configuredService.getPort();
            if (service != null) {
                serviceName = configuredService.getPort().getName();
                address = "sca:///" + moduleComponentName + '/' + partName + '/' + serviceName;
            } else {
                address = "sca:///" + moduleComponentName + '/' + partName;
            }

        } else if (configuredPort instanceof ConfiguredReference) {
            ConfiguredReference configuredReference = (ConfiguredReference) configuredPort;
            partName = part.getName();
            serviceName = configuredReference.getPort().getName();
            if (serviceName!=null)
                address = "sca:///" + moduleComponentName + '/' + partName + '/' + serviceName;
            else
                address = "sca:///" + moduleComponentName + '/' + partName;
        }

        isSCAScheme = Boolean.TRUE;
        isParsed = true;
    }

    /**
     * Constructor
     *
     * @param moduleComponent
     * @param targetServiceName
     */
    protected ServiceURIImpl(ModuleComponent moduleComponent, String targetServiceName) {
        if (moduleComponent != null)
            moduleComponentName = moduleComponent.getName();
        else
            moduleComponentName = "";
        int s = targetServiceName.indexOf('/');
        if (s == -1) {
            partName = targetServiceName;
            address = "sca:///" + moduleComponentName + '/' + partName;
        } else {
            partName = targetServiceName.substring(0, s);
            this.serviceName = targetServiceName.substring(s + 1);
            address = "sca:///" + moduleComponentName + '/' + partName + '/' + this.serviceName;
        }
        isSCAScheme = Boolean.TRUE;
        isParsed = true;
    }

    public boolean isSCAScheme() {
        if (isSCAScheme == null) {
            if (address != null && address.startsWith("sca://")) {
                isSCAScheme = Boolean.TRUE;
            } else {
                isSCAScheme = Boolean.FALSE;
            }
        }
        return isSCAScheme.booleanValue();
    }

    public String getAddress() {
        return address;
    }

    public String getPath() {
        StringBuffer path = new StringBuffer();
        String mc = getModuleComponentName();
        if (mc != null) {
            path.append(mc);
        }
        String p = getPartName();
        if (p !=null) {
            if (path.length()!=0)
                path.append('/');
            path.append(p);
        }
        String s = getServiceName();
        if (s !=null) {
            if (path.length()!=0)
                path.append('/');
            path.append(s);
        }
        return path.toString();
    }
    
    /**
     * Parse the address.
     */
    private void parse() {
        isParsed = true;
        if (isSCAScheme()) {
            int s1 = address.indexOf('/', 6);
            if (s1 == -1)
                return;
            s1++;
            int s2 = address.indexOf('/', s1);
            if (s2 == -1)
                return;
            moduleComponentName = address.substring(s1, s2);
            s2++;
            int s3 = address.indexOf('/', s2);
            if (s3 == -1) {
                partName = address.substring(s2);
                return;
            }
            partName = address.substring(s2, s3);
            s3++;
            serviceName = address.substring(s3);
        }
    }

    /**
     * Returns the module component name
     */
    public String getModuleComponentName() {
        if (!isParsed)
            parse();
        return moduleComponentName;
    }

    /**
     * Returns the part name
     */
    public String getPartName() {
        if (!isParsed)
            parse();
        return partName;
    }

    /**
     * Returns the service name
     */
    public String getServiceName() {
        if (!isParsed)
            parse();
        return serviceName;
	}

}
