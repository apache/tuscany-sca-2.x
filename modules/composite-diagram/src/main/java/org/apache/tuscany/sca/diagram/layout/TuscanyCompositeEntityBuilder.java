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
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;

public class TuscanyCompositeEntityBuilder {

    private Composite tuscanyComp;
    //components connected to each other are tracked using following map
    private HashMap<String, ArrayList<String>> connectedEntities = new HashMap<String, ArrayList<String>>();
    private int totalWidth = 0;
    private int totalHeight = 0;

    CompositeEntity composite = null;

    /**
     * Constructor which initiates the DOM document
     * @param aDom DOM document
     */
    public TuscanyCompositeEntityBuilder(Composite comp) {
        tuscanyComp = comp;
    }

    public CompositeEntity buildCompositeEntity() {

        String compositeName;
        compositeName = tuscanyComp.getName().getLocalPart();
        System.out.println("compositeName " + compositeName);

        ComponentEntity[] comps = buildComponentEntities();

        buildWires(tuscanyComp.getWires(), comps);

        composite = new CompositeEntity(compositeName);

        setParent(comps);

        System.out.println("ComponentEntity " + comps[0].getId());
        int[][] conns = buildConnectionMatrix(comps);
        System.out.println("ComponentEntity " + conns[0][0]);

        composite.setComponentList(comps);
        composite.setConnections(conns);

        LayoutBuilder buildLayout = new LayoutBuilder(comps, conns);
        buildLayout.placeEntities();

        System.out.println("conns " + conns[0][0]);

        buildCompositeService();
        buildCompositeReference();
        buildCompositeProperty();

        addInclusions();

        composite.setAttributes();

        return composite;
    }

    private void setParent(ComponentEntity[] comps) {

        for (ComponentEntity comp : comps) {
            comp.setParent(composite);
        }
    }

    private void buildCompositeService() {

        List<Service> sers = tuscanyComp.getServices();

        for (int i = 0; i < sers.size(); i++) {
            Service compositeSer = sers.get(i);
            composite.addAService(compositeSer.getName());
            String service = ((CompositeService)compositeSer).getPromotedService().getName();

            composite.addToPromoteAService(compositeSer.getName(), service);
        }

    }

    private void buildCompositeReference() {

        List<Reference> refs = tuscanyComp.getReferences();

        for (int i = 0; i < refs.size(); i++) {
            Reference compositeRef = refs.get(i);
            composite.addAReference(compositeRef.getName());

            List<ComponentReference> promotedRefs = ((CompositeReference)compositeRef).getPromotedReferences();

            for (ComponentReference ref : promotedRefs) {
                String reference = ref.getName();

                composite.addToPromoteAReference(compositeRef.getName(), reference);
            }

        }

    }

    private void buildCompositeProperty() {

        List<Property> props = tuscanyComp.getProperties();

        for (int i = 0; i < props.size(); i++) {
            Property compositeProp = props.get(i);
            composite.addAProperty(compositeProp.getName());
        }

    }

    private void addInclusions() {

        List<Composite> includes = tuscanyComp.getIncludes();

        for (int i = 0; i < includes.size(); i++) {
            Composite anInclude = includes.get(i);
            composite.addToIncludedComposites(anInclude.getName().getLocalPart());
        }

    }

    private int[][] buildConnectionMatrix(ComponentEntity[] comps) {

        int[][] connections = new int[comps.length][comps.length];
        connections = initConnections(connections);

        for (Entity ent : comps) {
            for (String name : ent.getAdjacentEntities()) {
                ComponentEntity e2 = findEntity(comps, name);
                if (ent != null && e2 != null) {
                    //System.out.println("^^^^^^^^^ "+e2.getName());
                    connections[ent.getId()][e2.getId()] = 1;
                }
            }

        }

        return connections;
    }

    private String extractComp(ComponentEntity[] elts, String str, boolean isReference) {

        if (isReference) {
            for (ComponentEntity elt : elts) {
                for (String ref : elt.getReferences()) {
                    if (ref.equals(str)) {
                        return elt.getName();
                    }
                }
            }
        } else {
            for (ComponentEntity elt : elts) {
                for (String ser : elt.getServices()) {
                    if (ser.equals(str)) {
                        return elt.getName();
                    }
                }
            }
        }
        return "";

    }

    private int[][] initConnections(int[][] connections) {

        for (int i = 0; i < connections.length; i++) {
            for (int j = 0; j < connections.length; j++) {
                connections[i][j] = 0;
            }
        }
        return connections;
    }

    public ComponentEntity[] buildComponentEntities() {

        ComponentEntity[] elts = null;

        List<Component> components = tuscanyComp.getComponents();

        elts = new ComponentEntity[components.size()];

        for (int i = 0; i < components.size(); i++) {
            Component aComp = components.get(i);

            elts[i] = new ComponentEntity();
            elts[i].setId(i);
            elts[i].setName(aComp.getName());

            setServices(aComp.getServices(), elts[i]);
            setReferences(aComp.getReferences(), elts[i]);
            setProperties(aComp.getProperties(), elts[i]);

            elts[i].referenceHeight();
            elts[i].serviceHeight();
            elts[i].propertyLength();
        }

        return elts;

    }

    private void buildWires(List<Wire> wires, ComponentEntity[] elts) {

        for (int i = 0; i < wires.size(); i++) {
            String service, serviceComp, reference, referenceComp;
            Wire aWire = wires.get(i);

            reference = aWire.getSource().getName();
            service = aWire.getTarget().getName();

            referenceComp = extractComp(elts, reference, true);
            serviceComp = extractComp(elts, service, false);

            ComponentEntity e1 = findEntity(elts, referenceComp);

            //System.out.println("^^^^^^^^^ "+e1.getName());
            if (e1 != null) {
                //System.out.println("^^^^^^^^^ "+e1.getId());
                //connections[e1.getId()][e2.getId()] = 1;
                createConnection(e1, reference, serviceComp, service);
            }
        }

    }

    private ComponentEntity findEntity(ComponentEntity[] elts, String componentName) {

        for (ComponentEntity e : elts) {
            if (e.getName().equals(componentName)) {
                return e;
            }
        }
        return null;
    }

    private void setReferences(List<ComponentReference> refs, ComponentEntity ent) {

        for (int i = 0; i < refs.size(); i++) {
            ent.addAReference(refs.get(i).getName());
        }

    }

    private void createConnection(ComponentEntity ent, String reference, String serviceComp, String service) {

        String referenceComp = ent.getName();

        if (reference != null && service != null) {

            ent.addToRefToSerMap(reference, service);
            ent.addAnAdjacentEntity(serviceComp);
            addToConnectedEntities(referenceComp, serviceComp);
            addToConnectedEntities(serviceComp, referenceComp);
        } else if (reference == null && service != null) {
            ent.addToRefToSerMap(referenceComp, service);
            ent.addAnAdjacentEntity(serviceComp);
            addToConnectedEntities(referenceComp, serviceComp);
            addToConnectedEntities(serviceComp, referenceComp);
        } else if (reference != null && service == null) {
            ent.addToRefToSerMap(reference, serviceComp);
            ent.addAnAdjacentEntity(serviceComp);
            addToConnectedEntities(referenceComp, serviceComp);
            addToConnectedEntities(serviceComp, referenceComp);
        } else {
            ent.addToRefToSerMap(referenceComp, serviceComp);
            ent.addAnAdjacentEntity(serviceComp);
            addToConnectedEntities(referenceComp, serviceComp);
            addToConnectedEntities(serviceComp, referenceComp);
        }
    }

    @SuppressWarnings("unused")
    private void print(ComponentEntity[] elts) {

        for (ComponentEntity ent : elts) {
            System.out.println(ent.getName() + " : "
                + ent.getLevel()
                + " : "
                + ent.getLane()
                + " : "
                + ent.getX()
                + " : "
                + ent.getY());
        }
    }

    private void addToConnectedEntities(String ent1, String ent2) {
        System.err.println(ent1 + " : " + ent2);
        ArrayList<String> list;
        if (connectedEntities.containsKey(ent1)) {
            list = connectedEntities.get(ent1);

        } else {
            list = new ArrayList<String>();

        }
        list.add(ent2);
        connectedEntities.put(ent1, list);
    }

    private void setServices(List<ComponentService> sers, ComponentEntity ent) {

        for (int i = 0; i < sers.size(); i++) {
            ent.addAService(sers.get(i).getName());
        }

    }

    private void setProperties(List<ComponentProperty> props, ComponentEntity ent) {

        for (int i = 0; i < props.size(); i++) {
            ent.addAProperty(props.get(i).getName());
        }

    }

    public int getTotalWidth() {
        return totalWidth;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

}
