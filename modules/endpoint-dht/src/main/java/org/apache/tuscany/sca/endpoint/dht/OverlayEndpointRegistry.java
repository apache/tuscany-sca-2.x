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

package org.apache.tuscany.sca.endpoint.dht;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

import ow.dht.DHT;
import ow.dht.DHTConfiguration;
import ow.dht.DHTFactory;
import ow.dht.ValueInfo;
import ow.id.ID;

/**
 * A EndpointRegistry based on Overlay Weaver DHT
 */
public class OverlayEndpointRegistry implements EndpointRegistry, LifeCycleListener {
    private final static Logger logger = Logger.getLogger(OverlayEndpointRegistry.class.getName());
    private final static int DEFAULT_PORT = 3997;
    private final static int DEFAULT_TTL = 600 * 1000;
    private final static String DEFAULT_DOMAIN_URI = "http://tuscany.apache.org/sca/1.1/domains/default";
    private String domainURI = DEFAULT_DOMAIN_URI;
    private List<EndpointReference> endpointreferences = new CopyOnWriteArrayList<EndpointReference>();
    private List<EndpointListener> listeners = new CopyOnWriteArrayList<EndpointListener>();
    private DHT<Endpoint> map = null;
    private ConcurrentHashMap<String, Endpoint> publishedEndpoints = new ConcurrentHashMap<String,Endpoint>();
    private ExtensionPointRegistry registry;
    private  String  joinPort= null;
    private  String address  = null;
    private int port;

    public OverlayEndpointRegistry(ExtensionPointRegistry registry, Map<String, String> attributes) {
        this.registry = registry;
        String portStr = attributes.get("port");
        if (portStr != null) {
            port = DEFAULT_PORT;
        }
	/* This is the address that you need for join a DHT */
	address = attributes.get("address");
        if (address == null) {
            address = getBindAddress();
        }
    }

    public OverlayEndpointRegistry(String domainURI) {
        this.domainURI = domainURI;
        // start();
    }

    public void start() {
        if (map != null) {
            throw new IllegalStateException("The registry has already been started");
        }
	/* here you have to join the DHT */
	DHTConfiguration config = DHTFactory.getDefaultConfiguration();
	/* the DHT behaviour should be configurable */
	config.setRoutingStyle("Iterative");
	config.setRoutingAlgorithm("Pastry");
	config.setSelfPort(DEFAULT_PORT);
	try {
	    map = DHTFactory.<Endpoint>getDHT(config);
	}
	catch (Exception e) {
	    throw new IllegalStateException(e);
	}
	try {
	    map.joinOverlay(address, Integer.parseInt(joinPort));
	}
	catch (IOException e) {
	    throw new IllegalStateException(e);
	}

    }

    public void stop() {
        if (map != null) {
            map.stop();
            map = null;
        }
    }

    public void addEndpoint(Endpoint endpoint) {
	int idSize = map.getRoutingAlgorithmConfiguration().getIDSizeInByte();
	ID key = ID.getHashcodeBasedID(endpoint.getURI(), idSize);
	try {
	    map.put(key, endpoint);

	}
	catch (Exception e) {
	    throw new IllegalStateException(e);
	}	
	publishedEndpoints.put(endpoint.getURI(), endpoint);
        logger.info("Add endpoint - " + endpoint);
    }

    public void addEndpointReference(EndpointReference endpointReference) {
        endpointreferences.add(endpointReference);
        logger.info("Add endpoint reference - " + endpointReference);
    }

    public void addListener(EndpointListener listener) {
        listeners.add(listener);
    }

    /**
     * Parse the component/service/binding URI into an array of parts (componentURI, serviceName, bindingName)
     * @param uri
     * @return
     */
    private String[] parse(String uri) {
        String[] names = new String[3];
        int index = uri.lastIndexOf('#');
        if (index == -1) {
            names[0] = uri;
        } else {
            names[0] = uri.substring(0, index);
            String str = uri.substring(index + 1);
            if (str.startsWith("service-binding(") && str.endsWith(")")) {
                str = str.substring("service-binding(".length(), str.length() - 1);
                String[] parts = str.split("/");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid service-binding URI: " + uri);
                }
                names[1] = parts[0];
                names[2] = parts[1];
            } else if (str.startsWith("service(") && str.endsWith(")")) {
                str = str.substring("service(".length(), str.length() - 1);
                names[1] = str;
            } else {
                throw new IllegalArgumentException("Invalid component/service/binding URI: " + uri);
            }
        }
        return names;
    }

    private boolean matches(String target, String uri) {
        String[] parts1 = parse(target);
        String[] parts2 = parse(uri);
        for (int i = 0; i < parts1.length; i++) {
            if (parts1[i] == null || parts1[i].equals(parts2[i])) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public List<Endpoint> findEndpoint(EndpointReference endpointReference) {
        List<Endpoint> foundEndpoints = new ArrayList<Endpoint>();

        logger.info("Find endpoint for reference - " + endpointReference);

        if (endpointReference.getReference() != null) {
            Endpoint targetEndpoint = endpointReference.getTargetEndpoint();
	    int idSize = map.getRoutingAlgorithmConfiguration().getIDSizeInByte();
	    ID key = ID.getHashcodeBasedID(targetEndpoint.getURI(), idSize);
	    try {	
		Set<ValueInfo<Endpoint>> values = map.get(key);
		for (ValueInfo<Endpoint>v : values)
		    {
			Endpoint endpoint = v.getValue();
			if (matches(targetEndpoint.getURI(), endpoint.getURI())) {
                    
			    if (!isLocal(endpoint)) {
				endpoint.setRemote(true);
			    }
                   
			    if(endpoint instanceof RuntimeEndpoint) {
			        ((RuntimeEndpoint) endpoint).bind(registry, this);
			    }
                  
			    foundEndpoints.add(endpoint);
			    logger.info("Found endpoint with matching service  - " + endpoint);
			}
     
		    }
	
		
	    } catch (Exception e) 
		{
		    throw new IllegalStateException("Routing exception during resolving endpoint");
		}
	}

	return foundEndpoints;
    }

    private boolean isLocal(Endpoint entry) {
	Endpoint local;
	local = publishedEndpoints.get(entry.getURI());
	if (local != null)
	    return true;
	return false;
    }

    public List<EndpointReference> findEndpointReference(Endpoint endpoint) {
	return endpointreferences;
    }

    public Endpoint getEndpoint(String uri) {
	/* if is local there no need to go on the net*/
	Endpoint local = null;
	local = publishedEndpoints.get(uri);
	if (local != null)
	    return local;
	/* otherwise we should check on the net */
	int idSize = map.getRoutingAlgorithmConfiguration().getIDSizeInByte();
	ID key = ID.getHashcodeBasedID(uri, idSize);
	try {
	Set<ValueInfo<Endpoint>> values = map.get(key);
	for(ValueInfo <Endpoint> v: values)
	    {
		return v.getValue();
		    }
	} catch (Exception e) {
	     throw new IllegalStateException("Routing exception during resolving endpoint");
	}
	return local;
    }

    public List<EndpointReference> getEndpointRefereneces() {
	return endpointreferences;
    }

    public List<Endpoint> getEndpoints() {
	/*TODO*/
	return null;
    }

    public List<EndpointListener> getListeners() {
	return listeners;
    }

    public void removeEndpoint(Endpoint endpoint) {
	/*TODO*/
	publishedEndpoints.remove(endpoint.getURI());
	logger.info("Remove endpoint - " + endpoint);
    }

    public void removeEndpointReference(EndpointReference endpointReference) {
	endpointreferences.remove(endpointReference);
	logger.info("Remove endpoint reference - " + endpointReference);
    }

    public void removeListener(EndpointListener listener) {
	listeners.remove(listener);
    }

    public void updateEndpoint(String uri, Endpoint endpoint) {
	/* TODO*/
    }

    public void entryAdded(Object key, Object value) {
	/* TODO*/
    }

    public void entryRemoved(Object key, Object value) {
	/* TODO*/
    }

    public void entryUpdated(Object key, Object oldValue, Object newValue) {
	/* TODO*/   
    }
    private static String getBindAddress() {
	try {
	    Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
	    while (nis.hasMoreElements()) {
		NetworkInterface ni = nis.nextElement();
		// The following APIs require JDK 1.6
		/*
		  if (ni.isLoopback() || !ni.isUp() || !ni.supportsMulticast()) {
		  continue;
		  }
		*/
		Enumeration<InetAddress> ips = ni.getInetAddresses();
		if (!ips.hasMoreElements()) {
		    continue;
		}
		while (ips.hasMoreElements()) {
		    InetAddress addr = ips.nextElement();
		    if (addr.isLoopbackAddress()) {
			continue;
		    }
		    return addr.getHostAddress();
		}
	    }
	    return InetAddress.getLocalHost().getHostAddress();
	} catch (Exception e) {
	    logger.log(Level.SEVERE, e.getMessage(), e);
	    return null;
	}
    }

}
