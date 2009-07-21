/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements. See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership. The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied. See the License for the
  * specific language governing permissions and limitations
  * under the License.
  */
package org.apache.tuscany.sca.dosgi.discovery;

import static org.osgi.service.discovery.ServicePublication.ENDPOINT_ID;
import static org.osgi.service.discovery.ServicePublication.ENDPOINT_LOCATION;
import static org.osgi.service.discovery.ServicePublication.SERVICE_INTERFACE_NAME;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.osgi.service.discovery.ServiceEndpointDescription;

public class ServiceEndpointDescriptionImpl implements ServiceEndpointDescription {

    private static final Logger logger = Logger.getLogger(ServiceEndpointDescriptionImpl.class.getName());

    private Set<String> interfaces;
    private Map<String, Object> properties;

    public ServiceEndpointDescriptionImpl(Collection<String> interfaceNames) {
        this(interfaceNames, Collections.<String, Object> singletonMap(SERVICE_INTERFACE_NAME,
                                                                       interfaceNames));
    }

    public ServiceEndpointDescriptionImpl(Collection<String> interfaceNames, Map<String, Object> remoteProperties) {
        this.interfaces = new HashSet<String>(interfaceNames);
        this.properties = remoteProperties;
    }

    public ServiceEndpointDescriptionImpl(String interfaceName) {
        this(Collections.singleton(interfaceName));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceEndpointDescriptionImpl other = (ServiceEndpointDescriptionImpl)obj;
        if (interfaces == null) {
            if (other.interfaces != null)
                return false;
        } else if (!interfaces.equals(other.interfaces))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }

    public String getEndpointID() {
        Object val = properties.get(ENDPOINT_ID);
        if (val == null) {
            return null;
        } else {
            return val.toString();
        }
    }

    public String getEndpointInterfaceName(String interfaceName) {
        return interfaceName;
    }

    public URI getLocation() {
        Object value = properties.get(ENDPOINT_LOCATION);
        if (value == null) {
            return null;
        }

        try {
            return new URI(value.toString());
        } catch (URISyntaxException ex) {
            logger.warning("Service document URL is malformed : " + value.toString());
        }

        return null;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Collection<String> getPropertyKeys() {
        return getProperties().keySet();
    }

    public Collection<String> getProvidedInterfaces() {
        return interfaces;
    }

    public String getVersion(String interfaceName) {
        return "0.0";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((interfaces == null) ? 0 : interfaces.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }
}
