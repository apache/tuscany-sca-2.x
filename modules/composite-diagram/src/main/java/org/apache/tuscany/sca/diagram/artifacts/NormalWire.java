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

public class NormalWire extends WireArtifact {

    @Override
    public Element addElement(Document document, String svgNs, Object a, Object b, int changingFactor, String color) {

        ReferenceArtifact aReference = (ReferenceArtifact)a;
        ServiceArtifact aService = (ServiceArtifact)b;

        Element polyline = document.createElementNS(svgNs, "polyline");
        int x1 = aReference.getxCoordinate() + aReference.getHeight() * 3 / 2;
        int y1 = aReference.getyCoordinate() + aReference.getHeight() / 2;

        int x2 = aService.getxCoordinate() + aService.getHeight() / 2;
        int y2 = aService.getyCoordinate() + aService.getHeight() / 2;

        polyline = setWireAttributes(x1, y1, x2, y2, polyline, changingFactor, color);
        polyline.setAttributeNS(null, "class", "wire normalWire");

        return polyline;
    }

}
