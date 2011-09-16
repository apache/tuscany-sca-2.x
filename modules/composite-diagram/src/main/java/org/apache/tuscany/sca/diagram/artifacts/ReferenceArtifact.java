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

public class ReferenceArtifact extends Artifact {

    /**
     * In a Reference the (x,y) coordinates refers to the top corner edge of the polygon
     * 		       (x,y)______
     * 				  	\     \ 
     * 					 \     \
     * 					 /     /
     * 					/____ /
     */
    public Element addElement(Document document, String svgNs, int x, int y, int height, int width) {

        this.setHeight(height);
        this.setWidth(width);
        this.setxCoordinate(x);
        this.setyCoordinate(y);

        int halfOfHeight = height / 2;

        Element polygon = document.createElementNS(svgNs, "polygon");
        polygon.setAttributeNS(null, "points", "" + x
            + ","
            + y
            + " "
            + ""
            + (x + 2 * halfOfHeight)
            + ","
            + (y)
            + " "
            + ""
            + (x + 3 * halfOfHeight)
            + ","
            + (y + halfOfHeight)
            + " "
            + ""
            + (x + 2 * halfOfHeight)
            + ","
            + (y + 2 * halfOfHeight)
            + " "
            + ""
            + (x)
            + ","
            + (y + 2 * halfOfHeight)
            + " "
            + ""
            + (x + halfOfHeight)
            + ","
            + (y + halfOfHeight)
            + " ");

        polygon.setAttributeNS(null, "fill", "#BF3EFF");
        polygon.setAttributeNS(null, "stroke", "#68228B");
        polygon.setAttributeNS(null, "class", "reference");


        return polygon;
    }

    public Element addElement(Document document, String svgNs, int x, int y, int height) {

        return this.addElement(document, svgNs, x, y, height, height * 3 / 2);
    }

}
