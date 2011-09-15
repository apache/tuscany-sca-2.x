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

import org.apache.tuscany.sca.diagram.artifacts.Constant;

/**
 * Represents an unit (a component including its references, services, properties
 * and adjacent units) in the diagram.
 *
 */
public class ComponentEntity extends Entity {

    //	private String componentName;
    //	private int X, Y, level=-1, lane=-1, refHeight, serHeight, propLength;
    //	private final int height= Component.DEFAULT_HEIGHT, width= Component.DEFAULT_WIDTH;
    //	public static final int defaultNoOfSers= Component.DEFAULT_HEIGHT / (Service.MAXIMUM_HEIGHT+Service.SPACING);
    //	public static final int defaultNoOfRefs= Component.DEFAULT_HEIGHT / (Reference.MAXIMUM_HEIGHT+Reference.SPACING); //same value for defaultNoOfSers
    //	public static final int defaultNoOfProps= Component.DEFAULT_WIDTH / (Property.MAXIMUM_HEIGHT+Property.SPACING); 

    private HashMap<String, String> referenceToServiceMap = new HashMap<String, String>();

    //private HashSet<String> connectedEntities = new HashSet<String>();

    public ComponentEntity() {

        setStartPosition(200);
        setHeight(Constant.COMPONENT_DEFAULT_HEIGHT);
        setWidth(Constant.COMPONENT_DEFAULT_WIDTH);

        setDefaultNoOfSers(Constant.COMPONENT_DEFAULT_HEIGHT / (Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_SERVICE + Constant.SPACING_FOR_COMPONENT_OF_SERVICE));
        setDefaultNoOfRefs(Constant.COMPONENT_DEFAULT_HEIGHT / (Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_REFERENCE + Constant.SPACING_FOR_COMPONENT_OF_REFERENCE));
        setDefaultNoOfProps(Constant.COMPONENT_DEFAULT_WIDTH / (Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_PROPERTY + Constant.SPACING_FOR_COMPONENT_OF_PROPERTY));
    }

    public void referenceHeight() {
        if (getDefaultNoOfRefs() < getNoOfRefs()) {

            setRefHeight((Constant.COMPONENT_DEFAULT_HEIGHT / getNoOfRefs()) - Constant.SPACING_FOR_COMPONENT_OF_REFERENCE);
        } else
            setRefHeight(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_REFERENCE);
    }

    public void serviceHeight() {
        if (getDefaultNoOfSers() < getNoOfSers()) {

            setSerHeight((Constant.COMPONENT_DEFAULT_HEIGHT / getNoOfSers()) - Constant.SPACING_FOR_COMPONENT_OF_SERVICE);
        } else
            setSerHeight(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_SERVICE);
    }

    public void propertyLength() {
        if (getDefaultNoOfProps() < getNoOfProps()) {

            setPropLength((Constant.COMPONENT_DEFAULT_WIDTH / getNoOfProps()) - Constant.SPACING_FOR_COMPONENT_OF_PROPERTY);
        } else
            setPropLength(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPONENT_OF_PROPERTY);
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

    public HashMap<String, String> getReferenceToServiceMap() {
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
