package org.apache.tuscany.sca.diagram.layout;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Entity {

    private int id = -1; //a unique integer id (0..n)
    private String name; // a unique name
    private int spaceFactor = 2; //which determines the free space surrounded by this
    private int x; // x coordinate
    private int y; // y coordinate
    private int level = -1; // corresponding row which this entity is placed
    private int lane = -1; // corresponding column which this entity is placed
    private boolean isPossitionSet = false;
    private int height; // height of the entity
    private int width; // width of the entity
    private int refHeight; // height of a reference element
    private int serHeight; // height of a service element
    private int propLength; // length of a property element
    private int defaultNoOfSers; // default # of service elements
    private int defaultNoOfRefs; // default # of reference elements
    private int defaultNoOfProps; // default # of property elements
    private int startPosition = 0;
    private Entity parent = null;

    private ArrayList<String> references = new ArrayList<String>();

    private ArrayList<String> services = new ArrayList<String>();

    private ArrayList<String> properties = new ArrayList<String>();

    private HashSet<String> adjacentEntities = new HashSet<String>();
    
    private String implementation;

    public abstract void referenceHeight();

    public abstract void serviceHeight();

    public abstract void propertyLength();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int init) {
        this.x = init + width * spaceFactor * lane;
    }

    public int getY() {
        return y;
    }

    public void setY(int init) {
        this.y = init + height * spaceFactor * level;
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

    public int getPropLength() {
        return propLength;
    }

    public void setPropLength(int propLength) {
        this.propLength = propLength;
    }

    public int getDefaultNoOfSers() {
        return defaultNoOfSers;
    }

    public void setDefaultNoOfSers(int defaultNoOfSers) {
        this.defaultNoOfSers = defaultNoOfSers;
    }

    public int getDefaultNoOfRefs() {
        return defaultNoOfRefs;
    }

    public void setDefaultNoOfRefs(int defaultNoOfRefs) {
        this.defaultNoOfRefs = defaultNoOfRefs;
    }

    public int getDefaultNoOfProps() {
        return defaultNoOfProps;
    }

    public void setDefaultNoOfProps(int defaultNoOfProps) {
        this.defaultNoOfProps = defaultNoOfProps;
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

    public void setPossitionSet(boolean isPossitionSet) {
        this.isPossitionSet = isPossitionSet;
    }

    public boolean isPossitionSet() {
        return isPossitionSet;
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


}
