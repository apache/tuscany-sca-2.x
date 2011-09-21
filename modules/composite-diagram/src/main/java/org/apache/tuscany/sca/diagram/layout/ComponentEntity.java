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

package org.apache.tuscany.sca.diagram.layout;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.diagram.artifacts.Constant;

/**
 * Represents an unit (a component including its references, services, properties
 * and adjacent units) in the diagram.
 *
 */
public class ComponentEntity extends Entity {

    private Map<String, String> referenceToServiceMap = new HashMap<String, String>();

    public ComponentEntity() {
        setStartPosition(200);
        setHeight(Constant.COMPONENT_DEFAULT_HEIGHT);
        setWidth(Constant.COMPONENT_DEFAULT_WIDTH);

        setRefHeight(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_REFERENCE);
        setSerHeight(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_SERVICE);
        setPropWidth(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_PROPERTY);
    }

    public void build() {
        // Find the services height
        int size1 = services.size();
        int total1 = size1 * serHeight + (size1 + 1) * Constant.SPACING_FOR_COMPONENT_OF_SERVICE;

        // Find the references height
        int size2 = references.size();
        int total2 = size2 * refHeight + (size2 + 1) * Constant.SPACING_FOR_COMPONENT_OF_REFERENCE;

        int total = Math.max(total1, total2);
        height = Math.max(total, height);

        // Find the properties width
        int size3 = properties.size();
        int total3 = size3 * propWidth + (size3 + 1) * Constant.SPACING_FOR_COMPONENT_OF_PROPERTY;

        width = Math.max(width, total3);

    }

    /**
     * Put a value to referenceToServiceMap
     * @param ref
     * @param ser
     * @return successfully added or not
     */
    //assumption there can not be two services for the same reference
    public boolean addToRefToSerMap(String ref, String ser) {
        //ref = ref.toLowerCase();
        //ser = ser.toLowerCase();

        if (referenceToServiceMap.containsKey(ref))
            return false;

        referenceToServiceMap.put(ref, ser);
        return true;
    }

    /**
     * Retrieve a service name for a given reference
     * @param ref
     * @return service name
     */
    public String getSerOfRef(String ref) {
        //ref = ref.toLowerCase();

        if (!referenceToServiceMap.containsKey(ref))
            return null;

        return referenceToServiceMap.get(ref);
    }

    public Map<String, String> getReferenceToServiceMap() {
        return referenceToServiceMap;
    }

    public void setReferenceToServiceMap(HashMap<String, String> referenceToServiceMap) {
        this.referenceToServiceMap = referenceToServiceMap;
    }

    //	public int getNoOfRefs(){
    //		return references.size();
    //	}
    //	
    //	public int getNoOfSers(){
    //		return services.size();
    //	}
    //	
    //	public int getNoOfProps(){
    //		return properties.size();
    //	}
    //	
    //	public int getNoOfAdjacentUnits(){
    //		return adjacentEntities.size();
    //	}
    //	
    //	/**
    //	 * Put a value to referenceToServiceMap
    //	 * @param ref
    //	 * @param ser
    //	 * @return successfully added or not
    //	 */
    //	//assumption there can not be two services for the same reference
    //	public boolean addToRefToSerMap(String ref, String ser){
    //		//ref = ref.toLowerCase();
    //		//ser = ser.toLowerCase();
    //		
    //		if (referenceToServiceMap.containsKey(ref))
    //			return false;
    //		
    //		referenceToServiceMap.put(ref, ser);
    //		return true;
    //	}
    //	
    //	/**
    //	 * Retrieve a service name for a given reference
    //	 * @param ref
    //	 * @return service name
    //	 */
    //	public String getSerOfRef(String ref){
    //		//ref = ref.toLowerCase();
    //		
    //		if (!referenceToServiceMap.containsKey(ref))
    //			return null;
    //		
    //		return referenceToServiceMap.get(ref);
    //	}
    //	
    //	public void addAService(String serName){
    //		//serName = serName.toLowerCase();
    //		services.add(serName);
    //		
    //	}
    //	
    //	public void addAReference(String refName){
    //		//refName = refName.toLowerCase();
    //		references.add(refName);
    //		
    //	}
    //	
    //	public void addAProperty(String propName){
    //		//propName = propName.toLowerCase();
    //		properties.add(propName);
    //		
    //	}
    //	
    //	public void addAnAdjacentEntity(String x){
    ////		System.out.println("eee "+x);
    //		adjacentEntities.add(x);
    //		
    //	}
    //	
    //	public void addAnConnectedEntity(String x){
    ////		System.out.println("eee "+x);
    //		adjacentEntities.add(x);
    //		
    //	}
    //	
    //	public HashMap<String, String> getReferenceToServiceMap() {
    //		return referenceToServiceMap;
    //	}
    //	public void setReferenceToServiceMap(
    //			HashMap<String, String> referenceToServiceMap) {
    //		this.referenceToServiceMap = referenceToServiceMap;
    //	}
    //	public ArrayList<String> getProperties() {
    //		return properties;
    //	}
    //	public void setProperties(ArrayList<String> properties) {
    //		this.properties = properties;
    //	}
    //	public HashSet<String> getAdjacentEntities() {
    //		return adjacentEntities;
    //	}
    //	public void setAdjacentEntities(HashSet<String> adjacentEntities) {
    //		this.adjacentEntities = adjacentEntities;
    //	}
    //	public void setServices(ArrayList<String> services) {
    //		this.services = services;
    //	}
    //	
    //	public ArrayList<String> getServices() {
    //		return services;
    //	}
    //	
    //	public ArrayList<String> getReferences() {
    //		return references;
    //	}

    //	public void setConnectedEntities(HashSet<String> connectedEntities) {
    //		this.connectedEntities = connectedEntities;
    //	}
    //
    //	public HashSet<String> getConnectedEntities() {
    //		return connectedEntities;
    //	}

}
