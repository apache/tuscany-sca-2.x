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

package org.apache.tuscany.sca.diagram.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.tuscany.sca.diagram.artifacts.Artifact;
import org.apache.tuscany.sca.diagram.artifacts.ComponentArtifact;
import org.apache.tuscany.sca.diagram.artifacts.CompositeArtifact;
import org.apache.tuscany.sca.diagram.artifacts.Constant;
import org.apache.tuscany.sca.diagram.artifacts.DashedWire;
import org.apache.tuscany.sca.diagram.artifacts.ImplementationArtifact;
import org.apache.tuscany.sca.diagram.artifacts.Layer;
import org.apache.tuscany.sca.diagram.artifacts.Link;
import org.apache.tuscany.sca.diagram.artifacts.NormalWire;
import org.apache.tuscany.sca.diagram.artifacts.PropertyArtifact;
import org.apache.tuscany.sca.diagram.artifacts.ReferenceArtifact;
import org.apache.tuscany.sca.diagram.artifacts.ServiceArtifact;
import org.apache.tuscany.sca.diagram.artifacts.Style;
import org.apache.tuscany.sca.diagram.artifacts.Text;
import org.apache.tuscany.sca.diagram.artifacts.WireArtifact;
import org.apache.tuscany.sca.diagram.layout.ComponentEntity;
import org.apache.tuscany.sca.diagram.layout.CompositeEntity;
import org.apache.tuscany.sca.diagram.layout.Entity;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DiagramGenerator {

    private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
    private CompositeEntity comp;
    private Document doc;
    private String svgNS;
    private Element svgRoot;
    private ArrayList<ReferenceArtifact> refs = new ArrayList<ReferenceArtifact>();
    private ArrayList<ServiceArtifact> sers = new ArrayList<ServiceArtifact>();
    private int diagramHeight, diagramWidth;

    private boolean isHtml;
    private final String baseURL;

    private int lastUsedChangingFactor = 0;

    enum ChangingFactor {
        a(20), b(40), c(60), d(80), e(100), f(120), g(140), h(160);

        private final int val;

        private ChangingFactor(int x) {
            val = x;
        }

        public int getVal() {
            return val;
        }

    };

    enum Color {
        black
    }//, violet, red, green};

    private int previousWireColor = 0;

    /**
     * Constructor to generate a SVG diagram for a given CompositeEntity
     * Object.
     */
    public DiagramGenerator(CompositeEntity comp, boolean isHtml, String baseURL) {

        this.comp = comp;
        this.isHtml = isHtml;
        this.baseURL = baseURL;

    }

    /**
     * Draws the diagram.
     */
    public Document buildSVGDocument() {

        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        doc = impl.createDocument(svgNS, "svg", null);

        // Get the root element (the 'svg' element).
        svgRoot = doc.getDocumentElement();

        svgRoot.setAttribute("xmlns:xlink", XLINK_NS);

        setDiagramHeight(comp.getHeight() + 200);
        setDiagramWidth(comp.getWidth() + 400);

        svgRoot.setAttributeNS(null, "viewBox", "0 0 " + getDiagramWidth() + " " + getDiagramHeight());

        svgRoot.appendChild(new Style().addElement(doc, svgNS, null));

        addLayer();
        addComposite();

        for (ComponentEntity ent : comp.getComponentList()) {

            addComponent(ent);
            addComponentProperties(ent);
            addComponentService(ent);
            addComponentReference(ent);
        }

        addCompositeProperties();
        addCompositeService();
        addCompositeReference();

        addComponentConnections();
        addReferencePromotion();
        addServicePromotion();

        addInclusions();

        return doc;
    }

    private void addLayer() {

        Layer outerLayer = new Layer();
        Element layerElt = outerLayer.addElement(doc, svgNS, 0, 0, getDiagramHeight(), getDiagramWidth());
        svgRoot.appendChild(layerElt);
    }

    private void addReferencePromotion() {

        for (Iterator<Entry<String, ArrayList<String>>> it = comp.getPromoteAReference().entrySet().iterator(); it
            .hasNext();) {
            Entry<String, ArrayList<String>> entry = it.next();
            String compositeRef = entry.getKey();
            ArrayList<String> componentRef = entry.getValue();

            ReferenceArtifact r1 = getRef(comp.getName() + "/" + compositeRef);

            for (String ref : componentRef) {

                ReferenceArtifact r2 = getRef(ref);

                if (r1 != null && r2 != null) {
                    addWire(r2, r1);
                }
            }

        }
    }

    private void addServicePromotion() {

        for (Iterator<Entry<String, String>> it = comp.getPromoteAService().entrySet().iterator(); it.hasNext();) {
            Entry<String, String> entry = it.next();
            String compositeSer = entry.getKey();
            String componentSer = entry.getValue();

            ServiceArtifact s1 = getSer(comp.getName() + "/" + compositeSer);
            ServiceArtifact s2 = getSer(componentSer);

            if (s1 != null && s2 != null) {
                addWire(s1, s2);
            }
        }
    }

    private void addInclusions() {

        if (!comp.getIncludedComposites().isEmpty()) {

            Layer inclusionLayer = new Layer();
            int constant = 10;
            int x0 = comp.getX() + constant;
            int y0 = comp.getY() + comp.getHeight() - (80 + constant);
            int height = 80;
            int width = comp.getWidth() - constant * 2;

            Element layerElt = inclusionLayer.addElement(doc, svgNS, x0, y0, height, width);

            Element text = Text.addTextElement(doc, svgNS, x0 + constant, y0 + constant * 2, "Included Composites: ");

            svgRoot.appendChild(layerElt);
            svgRoot.appendChild(text);

            text = Text.addTextElement(doc, svgNS, x0 + constant, y0 + constant * 7, "");

            String ext;

            if (isHtml) {
                ext = ".html";
            } else {
                ext = ".svg";
            }

            for (String includedComposite : comp.getIncludedComposites()) {

                Link link = new Link();
                Element aLink =
                    link.addElement(doc, svgNS, includedComposite + comp.getFileNameSuffix() + ext, includedComposite);
                text.appendChild(aLink);

            }
            svgRoot.appendChild(text);
        }
    }

    /**
     * Connects references to services.
     */
    private void addComponentConnections() {

        for (Entity ent : comp.getComponentList()) {
            //if(ent instanceof ComponentEntity){

            for (Iterator<Entry<String, String>> it =
                ((ComponentEntity)ent).getReferenceToServiceMap().entrySet().iterator(); it.hasNext();) {
                Entry<String, String> entry = it.next();
                String ref = entry.getKey();
                String ser = entry.getValue();

                ReferenceArtifact r = getRef(ent.getName() + "/" + ref);
                ServiceArtifact s = getSer(ser);

                if (r != null && s != null) {
                    addWire(r, s);
                }
            }
            //}
        }
    }

    /**
     * This is to remove overlapping of wire elements
     */
    private String getColor() {

        previousWireColor = previousWireColor % Color.values().length;
        return Color.values()[previousWireColor++].toString();
    }

    /**
     * This is to remove overlapping of wire elements
     */
    private int getChangingFactor() {

        lastUsedChangingFactor = lastUsedChangingFactor % ChangingFactor.values().length;
        return ChangingFactor.values()[lastUsedChangingFactor++].getVal();

    }

    private void addWire(ReferenceArtifact r, ServiceArtifact s) {

        WireArtifact edge = new NormalWire();
        Element wire = edge.addElement(doc, svgNS, r, s, getChangingFactor(), getColor());
        svgRoot.appendChild(wire);
    }

    private void addWire(ServiceArtifact s1, ServiceArtifact s2) {

        DashedWire edge = new DashedWire();
        Element wire = edge.addElement(doc, svgNS, s1, s2, getChangingFactor(), getColor());
        svgRoot.appendChild(wire);
    }

    private void addWire(ReferenceArtifact r1, ReferenceArtifact r2) {

        DashedWire edge = new DashedWire();
        Element wire = edge.addElement(doc, svgNS, r1, r2, getChangingFactor(), getColor());
        svgRoot.appendChild(wire);
    }

    private ServiceArtifact getSer(String ser) {

        for (ServiceArtifact s : sers) {
            if (Artifact.matches(ser, s.getContainerName(), s.getName())) {
                return s;
            }
        }
        return null;
    }

    private ReferenceArtifact getRef(String ref) {

        for (ReferenceArtifact r : refs) {

            if (Artifact.matches(ref, r.getContainerName(), r.getName())) {
                return r;
            }
        }
        return null;
    }

    private void addComponentProperties(ComponentEntity ent) {
        int propLen = ent.getPropWidth();
        int x = ent.getX() + Constant.SPACING_FOR_COMPONENT_OF_PROPERTY;
        int y = ent.getY() - propLen / 2;

        for (String prop : ent.getProperties()) {
            PropertyArtifact pro = new PropertyArtifact();
            Element property = pro.addElement(doc, svgNS, x, y, propLen);
            Element text = Text.addTextElement(doc, svgNS, x, y - Constant.SPACING_FOR_TEXT, prop);

            Element child = property;
            if (baseURL != null) {
                String url = baseURL + "/components/" + ent.getName() + "/properties/" + prop;
                child = createLink(property, url);
            }

            svgRoot.appendChild(child);
            svgRoot.appendChild(text);

            x += (propLen + Constant.SPACING_FOR_COMPONENT_OF_PROPERTY);

            pro.setName(prop);
            pro.setContainerName(ent.getName());
        }
    }

    private void addComponentReference(ComponentEntity ent) {

        int refHeight = ent.getRefHeight();
        int x = (ent.getX() + ent.getWidth()) - (refHeight * 2 / 3);
        int y = ent.getY() + Constant.SPACING_FOR_COMPONENT_OF_REFERENCE;

        for (String ref : setRefOrder(ent)) {
            ReferenceArtifact refer = new ReferenceArtifact();
            Element polygon = refer.addElement(doc, svgNS, x, y, refHeight);

            Element child = polygon;
            if (baseURL != null) {
                String url = baseURL + "/components/" + ent.getName() + "/services/" + ref;
                child = createLink(polygon, url);
            }

            Element text = Text.addTextElement(doc, svgNS, x, y - Constant.SPACING_FOR_TEXT, ref);
            svgRoot.appendChild(child);
            svgRoot.appendChild(text);

            y += (refHeight + Constant.SPACING_FOR_COMPONENT_OF_REFERENCE);

            refer.setName(ref);
            refer.setContainerName(ent.getName());
            refs.add(refer);

        }
    }

    /**
     * This method is used to organize the Component References,
     * in a manner where they are stay approximately in-line with the
     * connecting Service. This would enhance the clearness of the diagram.
     */
    private String[] setRefOrder(ComponentEntity e) {

        ArrayList<String> refs = e.getReferences();
        ArrayList<String> sers = new ArrayList<String>();
        String[] orderedRefs = new String[refs.size()];

        //sers = new ArrayList<String>(e.getReferenceToServiceMap().values());

        sers = buildReferenceToMappingServiceList(refs, e);

        for (String eName : e.getAdjacentEntities()) {
            for (Entity ent : comp.getComponentList()) {

                if (ent.getName().equals(eName)) {
                    for (String s : sers) {
                        for (String s1 : ent.getServices()) {
                            if (s1.equals(s) || s.equals(ent.getName())) {
                                if (orderedRefs[ent.getLevel()] == null) {
                                    orderedRefs[ent.getLevel()] = refs.get(sers.indexOf(s));
                                    break;
                                } else {
                                    for (int i = ent.getLevel(); i < orderedRefs.length; i++) {
                                        if (orderedRefs[i] == null) {

                                            orderedRefs[i] = refs.get(sers.indexOf(s));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //adding references which are not connected to any service
        for (String ref : refs) {
            for (int i = 0; i < orderedRefs.length; i++) {
                if (ref.equals(orderedRefs[i])) {
                    break;
                } else if (orderedRefs[i] == null) {
                    orderedRefs[i] = ref;
                    break;
                }
            }
        }

        return orderedRefs;
    }

    private ArrayList<String> buildReferenceToMappingServiceList(ArrayList<String> refs, ComponentEntity e) {

        ArrayList<String> sers = new ArrayList<String>();

        for (int i = 0; i < refs.size(); i++) {
            sers.add(i, "");
        }

        for (Iterator<Entry<String, String>> it = e.getReferenceToServiceMap().entrySet().iterator(); it.hasNext();) {

            Entry<String, String> entry = it.next();
            String ref = entry.getKey();
            String ser = entry.getValue();
            //System.out.println("---------"+ref);
            int idx = refs.indexOf(ref);
            //System.out.println("---------"+sers.get(idx));
            sers.remove(idx);
            sers.add(idx, ser);
            //System.out.println(refs.get(idx)+"---"+sers.get(idx));

        }

        return sers;
    }

    private void addComponentService(ComponentEntity ent) {
        int serHeight = ent.getSerHeight();
        int x = ent.getX() - (serHeight * 2 / 3);
        int y = ent.getY() + Constant.SPACING_FOR_COMPONENT_OF_SERVICE;

        //System.out.println("''''''"+ent.getName() +" '''''' "+ ent.getServices().size());
        for (String ser : ent.getServices()) {

            ServiceArtifact serve = new ServiceArtifact();
            Element polygon = serve.addElement(doc, svgNS, x, y, serHeight);
            Element text;
            if (!ser.endsWith("Impl"))
                text = Text.addTextElement(doc, svgNS, x, y - Constant.SPACING_FOR_TEXT, ser);
            else
                text = Text.addTextElement(doc, svgNS, x, y - Constant.SPACING_FOR_TEXT, "");

            Element child = polygon;
            if (baseURL != null) {
                String url = baseURL + "/components/" + ent.getName() + "/services/" + ser;
                child = createLink(polygon, url);
            }

            svgRoot.appendChild(child);
            svgRoot.appendChild(text);

            y += (serHeight + Constant.SPACING_FOR_COMPONENT_OF_SERVICE);

            serve.setName(ser);
            serve.setContainerName(ent.getName());
            sers.add(serve);
        }
    }

    private void addCompositeService() {

        int serHeight = comp.getSerHeight();
        int x = comp.getX() - (serHeight * 2 / 3);
        int y =
            comp.getY() + getStartingPoint(comp.getHeight(), serHeight, Constant.SPACING_FOR_COMPOSITE_OF_SERVICE, comp
                .getServices().size());

        //int y= comp.getY() + Constant.SPACING_FOR_COMPOSITE_OF_SERVICE;

        //System.err.println(serHeight);
        //System.out.println("''''''"+((CompositeEntity)comp).getName() +" '''''' "+ comp.getServices().size());
        for (String ser : comp.getServices()) {

            ServiceArtifact serve = new ServiceArtifact();
            Element polygon = serve.addElement(doc, svgNS, x, y, serHeight);
            Element text;
            if (!ser.endsWith("Impl"))
                text = Text.addTextElement(doc, svgNS, x, y - Constant.SPACING_FOR_TEXT, ser);
            else
                text = Text.addTextElement(doc, svgNS, x, y - Constant.SPACING_FOR_TEXT, "");

            svgRoot.appendChild(polygon);
            svgRoot.appendChild(text);

            y += (serHeight + Constant.SPACING_FOR_COMPOSITE_OF_SERVICE);

            serve.setName(ser);
            serve.setContainerName(comp.getName());
            sers.add(serve);
        }
    }

    private void addCompositeReference() {

        int refHeight = comp.getRefHeight();

        int x = (comp.getX() + comp.getWidth()) - (refHeight * 2 / 3);
        int y =
            comp.getY() + getStartingPoint(comp.getHeight(),
                                           refHeight,
                                           Constant.SPACING_FOR_COMPOSITE_OF_REFERENCE,
                                           comp.getReferences().size());

        for (String ref : comp.getReferences()) {
            ReferenceArtifact refer = new ReferenceArtifact();
            Element polygon = refer.addElement(doc, svgNS, x, y, refHeight);
            Element text = Text.addTextElement(doc, svgNS, x, y - Constant.SPACING_FOR_TEXT, ref);
            svgRoot.appendChild(polygon);
            svgRoot.appendChild(text);

            y += (refHeight + Constant.SPACING_FOR_COMPOSITE_OF_REFERENCE);

            refer.setName(ref);
            refer.setContainerName(comp.getName());
            refs.add(refer);

        }

    }

    //Algorithm to position in center
    private int getStartingPoint(int compDim, int artifactDim, int gap, int artifactQty) {

        int x = (compDim - artifactQty * artifactDim - (artifactQty - 1) * gap) / 2;
        return x;
    }

    private void addCompositeProperties() {
        int propLen = comp.getPropWidth();

        int x =
            comp.getX() + getStartingPoint(comp.getWidth(), propLen, Constant.SPACING_FOR_COMPOSITE_OF_PROPERTY, comp
                .getProperties().size());
        //int x= comp.getX() + Constant.SPACING_FOR_COMPOSITE_OF_PROPERTY;
        int y = comp.getY() - propLen / 2;

        for (String prop : comp.getProperties()) {
            PropertyArtifact pro = new PropertyArtifact();
            Element property = pro.addElement(doc, svgNS, x, y, propLen);
            Element text = Text.addTextElement(doc, svgNS, x, y - Constant.SPACING_FOR_TEXT, prop);

            svgRoot.appendChild(property);
            svgRoot.appendChild(text);

            x += (propLen + Constant.SPACING_FOR_COMPOSITE_OF_PROPERTY);

            pro.setName(prop);
            pro.setContainerName(comp.getName());
        }
    }

    private void addComponent(Entity ent) {

        ComponentArtifact comp = new ComponentArtifact();
        //System.err.println(ent.getX());
        Element com = comp.addElement(doc, svgNS, ent.getX(), ent.getY(), ent.getHeight(), ent.getWidth());

        Element component = com;
        if (baseURL != null) {
            String url = baseURL + "/components/" + ent.getName();
            component = createLink(com, url);
        }

        Element text =
            Text.addTextElement(doc,
                                svgNS,
                                ent.getX(),
                                ent.getY() + (ent.getHeight() + Constant.COMPONENT_TEXT_SPACING),
                                ent.getName());

        svgRoot.appendChild(component);
        svgRoot.appendChild(text);

        comp.setName(ent.getName());

        if (ent.getImplementation() == null) {
            return;
        }
        // Add the implementation
        ImplementationArtifact impl = new ImplementationArtifact();
        impl.setName(ent.getImplementation());
        impl.setContainerName(ent.getName());
        Element implElement =
            impl.addElement(doc,
                            svgNS,
                            ent.getX() + ent.getWidth() / 4,
                            ent.getY() + ent.getHeight() / 4,
                            ent.getHeight() / 2,
                            ent.getWidth() / 2);

        Element text2 =
            Text.addTextElement(doc,
                                svgNS,
                                ent.getX() + ent.getWidth() / 4,
                                ent.getY() + (ent.getHeight() / 4 + Constant.COMPONENT_TEXT_SPACING),
                                ent.getImplementation());
        text2.setAttributeNS(null, "font-size", "10");
        if (baseURL != null) {
            String url = baseURL + "/components/" + ent.getName() + "/implementation";
            implElement = createLink(implElement, url);
        }
        svgRoot.appendChild(implElement);
        svgRoot.appendChild(text2);

    }

    private Element createLink(Element com, String url) {
        Element link = doc.createElementNS(svgNS, "a");
        link.setAttributeNS(XLINK_NS, "xlink:href", url);
        link.appendChild(com);
        return link;
    }

    private void addComposite() {

        CompositeArtifact composite = new CompositeArtifact();

        Element composi = composite.addElement(doc, svgNS, comp.getX(), comp.getY(), comp.getHeight(), comp.getWidth());
        Element text = Text.addTextElement(doc, svgNS, comp.getX() + 20, comp.getY() + 20, comp.getName());

        svgRoot.appendChild(composi);
        svgRoot.appendChild(text);

        composite.setName(comp.getName());

    }

    public void setDiagramHeight(int diagramHeight) {
        this.diagramHeight = diagramHeight;
    }

    public int getDiagramHeight() {
        return diagramHeight;
    }

    public void setDiagramWidth(int diagramWidth) {
        this.diagramWidth = diagramWidth;
    }

    public int getDiagramWidth() {
        return diagramWidth;
    }

}
