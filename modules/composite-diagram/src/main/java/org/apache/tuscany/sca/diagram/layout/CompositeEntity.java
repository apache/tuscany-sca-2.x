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

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.tuscany.sca.diagram.artifacts.Constant;

/**
 * Represents an unit (a component including its references, services, properties
 * and adjacent units) in the diagram.
 *
 */
public class CompositeEntity extends Entity {

    //	private String componentName;
    //	private int X, Y, level=-1, lane=-1, refHeight, serHeight, propLength;
    //	private final int height= Component.DEFAULT_HEIGHT, width= Component.DEFAULT_WIDTH;
    //	public static final int defaultNoOfSers= Component.DEFAULT_HEIGHT / (Service.MAXIMUM_HEIGHT+Service.SPACING);
    //	public static final int defaultNoOfRefs= Component.DEFAULT_HEIGHT / (Reference.MAXIMUM_HEIGHT+Reference.SPACING); //same value for defaultNoOfSers
    //	public static final int defaultNoOfProps= Component.DEFAULT_WIDTH / (Property.MAXIMUM_HEIGHT+Property.SPACING); 

    private final String fileNameSuffix = "_diagram";
    private int maxInternalLevel = -1;
    private int maxInternalLane = -1;
    private ComponentEntity[] componentList;
    private int[][] connections;
    private HashMap<String, String> promoteAService = new HashMap<String, String>();
    private HashMap<String, ArrayList<String>> promoteAReference = new HashMap<String, ArrayList<String>>();
    private ArrayList<String> includedComposites = new ArrayList<String>();

    //private HashSet<String> connectedEntities = new HashSet<String>();

    public CompositeEntity(String name) {
        setStartPosition(200);
        setLevel(0);
        setLane(0);

        setX(getStartPosition());
        setY(getStartPosition() / 2);

        setName(name);

        setRefHeight(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPOSITE_OF_REFERENCE);
        setSerHeight(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPOSITE_OF_SERVICE);
        setPropWidth(Constant.DEFAULT_MAXIMUM_HEIGHT_FOR_COMPOSITE_OF_PROPERTY);
    }

    public void build() {
        int h = height;
        int w = width;

        // Find the services height
        int size1 = services.size();
        int total1 = size1 * serHeight + (size1 + 1) * Constant.SPACING_FOR_COMPOSITE_OF_SERVICE;

        // Find the references height
        int size2 = references.size();
        int total2 = size2 * refHeight + (size2 + 1) * Constant.SPACING_FOR_COMPOSITE_OF_REFERENCE;

        int total = Math.max(total1, total2);

        if (!includedComposites.isEmpty()) {
            height = Math.max(total, h) + 80 + getY();
        } else {
            height = Math.max(total, h) + getY();
        }

        // Find the properties width
        int size3 = properties.size();
        int total3 = size3 * propWidth + (size3 + 1) * Constant.SPACING_FOR_COMPOSITE_OF_PROPERTY;

        width = Math.max(w, total3) + getX();
    }

    public int getMaxInternalLevel() {
        return maxInternalLevel;
    }

    public int getMaxInternalLane() {
        return maxInternalLane;
    }

    public boolean addToPromoteAService(String compositeSer, String componentSer) {
        //ref = ref.toLowerCase();
        //ser = ser.toLowerCase();

        if (promoteAService.containsKey(compositeSer))
            return false;

        promoteAService.put(compositeSer, componentSer);
        return true;
    }

    public void setPromoteAService(HashMap<String, String> promoteAService) {
        this.promoteAService = promoteAService;
    }

    public HashMap<String, String> getPromoteAService() {
        return promoteAService;
    }

    public boolean addToPromoteAReference(String compositeRef, String componentRef) {
        ArrayList<String> list;

        if (promoteAReference.containsKey(compositeRef)) {
            list = promoteAReference.get(compositeRef);
        } else {
            list = new ArrayList<String>();
        }

        list.add(componentRef);
        promoteAReference.put(compositeRef, list);
        return true;
    }

    public boolean addToIncludedComposites(String composite) {

        includedComposites.add(composite);

        return true;
    }

    public void setPromoteAReference(HashMap<String, ArrayList<String>> promoteAReference) {
        this.promoteAReference = promoteAReference;
    }

    public HashMap<String, ArrayList<String>> getPromoteAReference() {
        return promoteAReference;
    }

    public ComponentEntity[] getComponentList() {
        return componentList;
    }

    public void setComponentList(ComponentEntity[] componentList) {
        this.componentList = componentList;
    }

    public void setConnections(int[][] connections) {
        this.connections = connections;
    }

    public int[][] getConnections() {
        return connections;
    }

    public ArrayList<String> getIncludedComposites() {
        return includedComposites;
    }

    public String getFileNameSuffix() {
        return fileNameSuffix;
    }

    public void setMaxInternalLevel(int maxInternalLevel) {
        this.maxInternalLevel = maxInternalLevel;
    }

    public void setMaxInternalLane(int maxInternalLane) {
        this.maxInternalLane = maxInternalLane;
    }

}
