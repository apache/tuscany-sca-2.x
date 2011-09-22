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
import java.util.HashSet;

public abstract class Entity {
    protected int id = -1; //a unique integer id (0..n)
    protected String name; // a unique name
    protected int spaceFactor = 2; //which determines the free space surrounded by this
    protected int x; // x coordinate
    protected int y; // y coordinate
    protected int level = -1; // corresponding row which this entity is placed
    protected int lane = -1; // corresponding column which this entity is placed
    protected boolean positionSet = false;
    protected int height; // height of the entity
    protected int width; // width of the entity
    protected int refHeight; // height of a reference element
    protected int serHeight; // height of a service element
    protected int propWidth; // length of a property element

    protected int startPosition = 0;
    protected Entity parent = null;

    protected ArrayList<String> references = new ArrayList<String>();

    protected ArrayList<String> services = new ArrayList<String>();

    protected ArrayList<String> properties = new ArrayList<String>();

    protected HashSet<String> adjacentEntities = new HashSet<String>();

    protected String implementation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getRefHeight() {
        return refHeight;
    }

    public void setRefHeight(int refHeight) {
        this.refHeight = refHeight;
    }

    public int getSerHeight() {
        return serHeight;
    }

    public void setSerHeight(int serHeight) {
        this.serHeight = serHeight;
    }

    public int getPropWidth() {
        return propWidth;
    }

    public void setPropWidth(int propLength) {
        this.propWidth = propLength;
    }

    public int getNoOfRefs() {
        return references.size();
    }

    public int getNoOfSers() {
        return services.size();
    }

    public int getNoOfProps() {
        return properties.size();
    }

    public int getNoOfAdjacentUnits() {
        return adjacentEntities.size();
    }

    public void addAService(String serName) {
        //serName = serName.toLowerCase();
        services.add(serName);

    }

    public void addAReference(String refName) {
        //refName = refName.toLowerCase();
        references.add(refName);

    }

    public void addAProperty(String propName) {
        //propName = propName.toLowerCase();
        properties.add(propName);

    }

    public void addAnAdjacentEntity(String x) {
        //		System.out.println("eee "+x);
        adjacentEntities.add(x);

    }

    public void addAnConnectedEntity(String x) {
        //		System.out.println("eee "+x);
        adjacentEntities.add(x);

    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        this.properties = properties;
    }

    public HashSet<String> getAdjacentEntities() {
        return adjacentEntities;
    }

    public void setAdjacentEntities(HashSet<String> adjacentEntities) {
        this.adjacentEntities = adjacentEntities;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPositionSet(boolean isPositionSet) {
        this.positionSet = isPositionSet;
    }

    public boolean isPositionSet() {
        return positionSet;
    }

    public int getSpaceFactor() {
        return spaceFactor;
    }

    public void setSpaceFactor(int spaceFactor) {
        this.spaceFactor = spaceFactor;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return parent;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Entity [id=").append(id).append(", name=").append(name).append("]");
        return builder.toString();
    }

    /**
     * Adjust the items and coordinates
     */
    public abstract void build();
}
