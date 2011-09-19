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

package org.apache.tuscany.sca.diagram.artifacts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Artifact {

    private String name;
    private String containerName;
    private int height;
    private int width;
    private int xCoordinate;
    private int yCoordinate;
    private Document doc;
    private String svgNs;
    private static final String ROUND_CORNER = "20";

    /**
     * Abstract method
     * @param document DOM document
     * @param svgNs namespace URI 
     * @param x x-coordinate
     * @param y y-coordinate
     * @param height height of the shape
     * @param width width of the shape
     * @return DOM Element
     */
    public abstract Element addElement(Document document, String svgNs, int x, int y, int height, int width);

    //	public abstract Element addElement(Document document, String svgNs, 
    //			int x, int y);

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

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public String getSvgNs() {
        return svgNs;
    }

    public void setSvgNs(String svgNs) {
        this.svgNs = svgNs;
    }

    public static String getRoundCorner() {
        return ROUND_CORNER;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getContainerName() {
        return containerName;
    }

    /**
     * Parse the component/service|reference/binding names
     * @param compoundName
     * @return An array of names
     */
    public static String[] parseNames(String compoundName) {
        String[] names = new String[] {"", "", ""};
        if (compoundName != null) {
            String[] parts = compoundName.split("/");
            for (int i = 0; i < parts.length; i++) {
                names[i] = parts[i];
            }
        }
        return names;
    }

    public static boolean matches(String compoundName, String... parts) {
        String names[] = parseNames(compoundName);
        if (parts.length > names.length) {
            return false;
        }
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() > 0 && names[i].length() > 0 && !names[i].equals(parts[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Artifact [containerName=").append(containerName).append(", name=").append(name).append("]");
        return builder.toString();
    }

}
