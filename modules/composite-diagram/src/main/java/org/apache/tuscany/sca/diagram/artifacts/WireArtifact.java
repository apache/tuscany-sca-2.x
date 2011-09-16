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

public abstract class WireArtifact {

    public abstract Element addElement(Document document,
                                       String svgNs,
                                       Object a,
                                       Object b,
                                       int changingFactor,
                                       String color);

    public Element setWireAttributes(int x1, int y1, int x2, int y2, Element polyline, int changingFactor, String color) {

        if (y1 == y2 && x2 > x1) {
            polyline.setAttributeNS(null, "points", x1 + "," + y1 + " " + x2 + "," + y2);
        } else if (y1 == y2 && x1 > x2) {
            polyline.setAttributeNS(null, "points", x1 + ","
                + y1
                + " "
                + (x1 + changingFactor)
                + ","
                + y1
                + " "
                + (x1 + changingFactor)
                + ","
                + (y1 - (changingFactor * 2))
                + " "
                + (x2 - changingFactor)
                + ","
                + (y1 - (changingFactor * 2))
                + " "
                + (x2 - changingFactor)
                + ","
                + (y1)
                + " "
                + (x2)
                + ","
                + (y1));

        } else {
            polyline.setAttributeNS(null, "points", x1 + ","
                + y1
                + " "
                + (x1 + changingFactor)
                + ","
                + y1
                + " "
                + (x1 + changingFactor)
                + ","
                + y2
                + " "
                + x2
                + ","
                + y2);
        }

        polyline.setAttributeNS(null, "stroke", color);
        polyline.setAttributeNS(null, "stroke-width", "2");
        polyline.setAttributeNS(null, "fill", "none");
        
        polyline.setAttributeNS(null, "class", "wire");

        return polyline;
    }

}
