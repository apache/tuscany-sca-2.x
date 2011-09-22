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

import org.apache.tuscany.sca.diagram.artifacts.Constant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EntityBuilder {

    private Document dom;

    private int totalWidth = 0;
    private int totalHeight = 0;

    private CompositeEntity composite = null;

    /**
     * Constructor which initiates the DOM document
     * @param aDom DOM document
     */
    public EntityBuilder(Document aDom) {
        dom = aDom;
    }

    public CompositeEntity buildCompositeEntity() {

        //get the root element
        Element docEle = dom.getDocumentElement();

        String compositeName;
        compositeName = docEle.getAttribute("name");
        //System.out.println("compositeName "+compositeName);

        ComponentEntity[] comps = buildComponentEntities(docEle);

        composite = new CompositeEntity(compositeName);

        setParent(comps);

        //System.out.println("ComponentEntity "+comps[0].getLevel());
        int[][] conns = buildConnectionMatrix(comps);

        composite.setComponentList(comps);
        composite.setConnections(conns);

        LayoutBuilder buildLayout = new LayoutBuilder(comps, conns, Constant.MAX_LEVELS);
        buildLayout.placeEntities();
        composite.setHeight(buildLayout.getTotalHeight());
        composite.setWidth(buildLayout.getTotalWidth());
        composite.setMaxInternalLane(buildLayout.getTotalLane());
        composite.setMaxInternalLevel(buildLayout.getTotalLevel());

        //System.out.println("conns "+conns[0][0]);

        buildCompositeService(docEle);
        buildCompositeReference(docEle);
        buildCompositeProperty(docEle);

        addInclusions(docEle);

        composite.build();

        return composite;
    }

    //	private void assignCoordinates() {
    //
    //		for(Entity ent: elts){
    //			ent.setX(ent.getParent().getX() + ent.getStartPosition());
    //			ent.setY(ent.getParent().getY() + ent.getStartPosition()/2);
    //		}
    //	}

    private void setParent(ComponentEntity[] comps) {

        for (ComponentEntity comp : comps) {
            comp.setParent(composite);
        }
    }

    private void buildCompositeService(Element docEle) {

        NodeList nl = docEle.getElementsByTagName("service");
        //System.err.println("^^^^^^^^^ "+nl.getLength());
        if (nl != null && nl.getLength() > 0) {

            for (int i = 0; i < nl.getLength(); i++) {

                Element elt = (Element)nl.item(i);

                if (elt.getParentNode().getNodeName().equals("composite")) {
                    String compositeSer = elt.getAttribute("name");
                    composite.addAService(compositeSer);

                    String target = elt.getAttribute("promote");

                    String service, serviceComp;
                    String[] arr1 = extractComp(target);
                    serviceComp = arr1[0];
                    service = arr1[1];

                    if (service == null) {
                        composite.addToPromoteAService(compositeSer, serviceComp);
                    } else {
                        composite.addToPromoteAService(compositeSer, serviceComp + "/" + service);
                    }
                }

            }
        }
    }

    private void buildCompositeReference(Element docEle) {

        NodeList nl = docEle.getElementsByTagName("reference");
        //System.out.println("^^^^^^^^^ "+nl.getLength());
        if (nl != null && nl.getLength() > 0) {

            for (int i = 0; i < nl.getLength(); i++) {

                Element elt = (Element)nl.item(i);

                if (elt.getParentNode().getNodeName().equals("composite")) {
                    String compositeRef = elt.getAttribute("name");
                    composite.addAReference(compositeRef);

                    String targetStr = elt.getAttribute("promote");

                    String[] targets = targetStr.split(" ");

                    for (String target : targets) {

                        String reference, referenceComp;
                        String[] arr1 = extractComp(target);
                        referenceComp = arr1[0];
                        reference = arr1[1];

                        if (reference == null) {
                            composite.addToPromoteAReference(compositeRef, referenceComp);
                        } else {
                            composite.addToPromoteAReference(compositeRef, referenceComp + "/" + reference);
                        }
                    }

                }
            }
        }
    }

    private void buildCompositeProperty(Element docEle) {

        NodeList nl = docEle.getElementsByTagName("property");
        //System.out.println("^^^^^^^^^ "+nl.getLength());
        if (nl != null && nl.getLength() > 0) {

            for (int i = 0; i < nl.getLength(); i++) {

                Element elt = (Element)nl.item(i);

                if (elt.getParentNode().getNodeName().equals("composite")) {
                    String compositeProp = elt.getAttribute("name");
                    composite.addAProperty(compositeProp);
                }
            }
        }
    }

    private void addInclusions(Element docEle) {

        NodeList nl = docEle.getElementsByTagName("include");
        //System.out.println("^^^^^^^^^ "+nl.getLength());
        if (nl != null && nl.getLength() > 0) {

            for (int i = 0; i < nl.getLength(); i++) {

                Element elt = (Element)nl.item(i);

                if (elt.getParentNode().getNodeName().equals("composite")) {
                    String compToBeIncluded = elt.getAttribute("name");
                    composite.addToIncludedComposites(compToBeIncluded);
                }
            }
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

    private String[] extractComp(String str) {

        String[] arr = new String[2];

        if (str.contains("/")) {
            arr = str.split("/");
        } else {
            arr[0] = str;
            arr[1] = null;
        }
        return arr;
    }

    private int[][] initConnections(int[][] connections) {

        for (int i = 0; i < connections.length; i++) {
            for (int j = 0; j < connections.length; j++) {
                connections[i][j] = 0;
            }
        }
        return connections;
    }

    public ComponentEntity[] buildComponentEntities(Element docEle) {

        ComponentEntity[] elts = null;

        //		//get the root element
        //		Element docEle = dom.getDocumentElement();
        //		compositeName = docEle.getAttribute("name");
        //		System.out.println("compositeName "+compositeName);

        //get a nodelist of elements
        NodeList nl = docEle.getElementsByTagName("component");
        if (nl != null && nl.getLength() > 0) {
            elts = new ComponentEntity[nl.getLength()];

            for (int i = 0; i < nl.getLength(); i++) {
                elts[i] = new ComponentEntity();
                Element nVal = (Element)nl.item(i);
                //System.out.println(nVal.hasAttribute("name"));
                elts[i].setId(i);
                elts[i].setName(nVal.getAttribute("name"));

                setImplementation(nVal, elts[i]);
                setServices(nVal, elts[i]);
                setReferences(nVal, elts[i]);
                setProperties(nVal, elts[i]);

                elts[i].build();
            }
        }

        buildWires(docEle, elts);
        //		//sec. 5.4 in the spec
        //		nl = docEle.getElementsByTagName("wire");
        //		System.out.println("^^^^^^^^^ "+nl.getLength());
        //		if(nl != null && nl.getLength() > 0 ) {
        //			for(int i = 0 ; i < nl.getLength();i++) {
        //				Element elt = (Element)nl.item(i);
        //				String source = elt.getAttribute("source");
        //				String target = elt.getAttribute("target");
        //				String service, serviceComp, reference, referenceComp;
        //				
        //				System.out.println("^^^^^^^^^ "+source+" ::: "+target);
        //				if(target.contains("/")){
        //					String[] arr = target.split("/");
        //					serviceComp = arr[0];
        //					service = arr[1];
        //				}
        //				else{
        //					serviceComp = target;
        //					service = null;
        //				}
        //				
        //				if(source.contains("/")){
        //					String[] arr = source.split("/");
        //					referenceComp = arr[0];
        //					reference = arr[1];
        //				}
        //				else{
        //					referenceComp = source;
        //					reference = null;
        //				}
        //				
        //				ComponentEntity e = findEntity(referenceComp);
        //				System.out.println("^^^^^^^^^ "+e.getName());
        //				if(e != null){
        //					createConnection(e, reference, serviceComp, service);
        //				}
        //			}
        //		}
        //
        //		positionEntities(elts);
        //
        //		calculateProperties(elts);
        //	print(elts);

        return elts;

    }

    private void buildWires(Element docEle, ComponentEntity[] elts) {

        //sec. 5.4 in the spec
        NodeList nl = docEle.getElementsByTagName("wire");
        //System.out.println("^^^^^^^^^ "+nl.getLength());
        if (nl != null && nl.getLength() > 0) {

            for (int i = 0; i < nl.getLength(); i++) {

                Element elt = (Element)nl.item(i);

                String source = elt.getAttribute("source");
                String target = elt.getAttribute("target");

                String service, serviceComp, reference, referenceComp;

                String[] arr1 = extractComp(target);
                serviceComp = arr1[0];
                service = arr1[1];

                String[] arr2 = extractComp(source);
                referenceComp = arr2[0];
                reference = arr2[1];

                //				//System.out.println("^^^^^^^^^ "+source+" ::: "+target);
                //				if(target.contains("/")){
                //					String[] arr = target.split("/");
                //					serviceComp = arr[0];
                //					service = arr[1];
                //				}
                //				else{
                //					serviceComp = target;
                //					service = null;
                //				}
                //				
                //				if(source.contains("/")){
                //					String[] arr = source.split("/");
                //					referenceComp = arr[0];
                //					reference = arr[1];
                //				}
                //				else{
                //					referenceComp = source;
                //					reference = null;
                //				}
                //				
                ComponentEntity e1 = findEntity(elts, referenceComp);
                //ComponentEntity e2 = findEntity(comps, serviceComp);

                //System.out.println("^^^^^^^^^ "+e1.getName());
                if (e1 != null) {
                    //System.out.println("^^^^^^^^^ "+e1.getId());
                    //connections[e1.getId()][e2.getId()] = 1;
                    createConnection(e1, reference, serviceComp, service);
                }
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

    private void setReferences(Element nVal, ComponentEntity ent) {

        NodeList nl = nVal.getElementsByTagName("reference");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element elt = (Element)nl.item(i);
                String target = elt.getAttribute("target");
                String ref = elt.getAttribute("name");
                if (target.contains("/")) {
                    String[] arr = target.split("/");
                    createConnection(ent, ref, arr[0], arr[1]);
                    //					ent.addToRefToSerMap(ref, arr[1]);
                    //					ent.addAnAdjacentEntity(arr[0]);
                    //					addToConnectedEntities(ent.getComponentName(), arr[0]);
                    //					addToConnectedEntities(arr[0], ent.getComponentName());
                } else if (!target.equals("")) {
                    createConnection(ent, ref, target, null);
                    //					ent.addToRefToSerMap(ref, target);
                    //					ent.addAnAdjacentEntity(target);
                    //					addToConnectedEntities(ent.getComponentName(), target);
                    //					addToConnectedEntities(target, ent.getComponentName());
                }

                ent.addAReference(ref);

            }
        }
    }

    private void createConnection(ComponentEntity ent, String reference, String serviceComp, String service) {

        String referenceComp = ent.getName();

        if (reference != null && service != null) {

            ent.addToRefToSerMap(reference, serviceComp + "/" + service);
            ent.addAnAdjacentEntity(serviceComp);
        } else if (reference == null && service != null) {
            ent.addToRefToSerMap(referenceComp, serviceComp + "/" + service);
            ent.addAnAdjacentEntity(serviceComp);
        } else if (reference != null && service == null) {
            ent.addToRefToSerMap(reference, serviceComp);
            ent.addAnAdjacentEntity(serviceComp);
        } else {
            ent.addToRefToSerMap(referenceComp, serviceComp);
            ent.addAnAdjacentEntity(serviceComp);
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

    private void setServices(Element nVal, ComponentEntity ent) {

        NodeList nl = nVal.getElementsByTagName("service");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element elt = (Element)nl.item(i);
                ent.addAService(elt.getAttribute("name"));
            }
        } else {

            NodeList nl1 = nVal.getElementsByTagName("implementation.java");
            if (nl1 != null && nl1.getLength() > 0) {
                for (int i = 0; i < nl1.getLength(); i++) {
                    Element elt = (Element)nl1.item(i);
                    //System.out.println(elt.getAttribute("class"));
                    String serName = extractServiceName(elt.getAttribute("class"));
                    ent.addAService(serName);
                }
            }

        }

    }

    private void setImplementation(Element nVal, ComponentEntity ent) {
        NodeList nodes = nVal.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element elt = (Element)nodes.item(i);
                String name = elt.getNodeName();
                if (name != null && name.contains(":")) {
                    name = name.substring(name.indexOf(':') + 1).trim();
                }
                if (name != null && name.startsWith("implementation.")) {
                    String type = name.substring("implementation.".length());
                    if ("implementation.java".equals(name)) {
                        String cls = elt.getAttribute("class");
                        ent.setImplementation(type + ":" + extractClassName(cls));
                    } else {
                        ent.setImplementation(type);
                    }
                    break;
                }
            }
        }
    }

    private String extractClassName(String classAttr) {
        if (classAttr == null) {
            return "";
        } else {
            int index = classAttr.lastIndexOf('.');
            return classAttr.substring(index + 1);
        }
    }

    /**
     * 
     * This will extract the service name part from the class attribute of 
     * implementation.java element. 
     * eg: if class = "NirmalServiceImpl", returning service name would be "NirmalService"
     */
    private String extractServiceName(String classAttr) {
        if (classAttr != null) {
            String[] x = classAttr.split("\\.");
            String name = x[x.length - 1];
            if (name.endsWith("Impl")) {
                return name.substring(0, name.length() - 4);
            } else {
                return name;
            }
        }
        return "";
    }

    private void setProperties(Element nVal, ComponentEntity ent) {

        NodeList nl = nVal.getElementsByTagName("property");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element elt = (Element)nl.item(i);
                ent.addAProperty(elt.getAttribute("name"));
            }
        }
    }

    //	public void setCompositeName(String compositeName) {
    //		this.compositeName = compositeName;
    //	}
    //
    //	public String getCompositeName() {
    //		return compositeName;
    //	}

    public int getTotalWidth() {
        return totalWidth;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

}
