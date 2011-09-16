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

public class PropertyArtifact extends Artifact {

    /**
     * Property is a square.
     * 			 ___
     * 			|	|
     * 			|___|
     * 
     */
    public Element addElement(Document document, String svgNs, int x, int y, int height, int width) {

        this.setHeight(height);
        this.setWidth(width);
        this.setxCoordinate(x);
        this.setyCoordinate(y);

        Element rectangle = document.createElementNS(svgNs, "rect");
        rectangle.setAttributeNS(null, "x", x + "");
        rectangle.setAttributeNS(null, "y", y + "");
        rectangle.setAttributeNS(null, "width", height + "");
        rectangle.setAttributeNS(null, "height", height + "");
        rectangle.setAttributeNS(null, "fill", "#EEEE00");
        rectangle.setAttributeNS(null, "stroke", "#EEC900");
        //rectangle.setAttributeNS(null, "fill-opacity", "0.75");
        
        rectangle.setAttributeNS(null, "class", "property");

        return rectangle;
    }

    public Element addElement(Document document, String svgNs, int x, int y, int height) {

        return addElement(document, svgNs, x, y, height, height);
    }

}
