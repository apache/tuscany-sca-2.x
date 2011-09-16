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

public class DashedWire extends WireArtifact {

    @Override
    public Element addElement(Document document, String svgNs, Object a, Object b, int changingFactor, String color) {

        Element polyline = document.createElementNS(svgNs, "polyline");
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;

        if (a instanceof ServiceArtifact && b instanceof ServiceArtifact) {

            ServiceArtifact aService1 = (ServiceArtifact)a;
            ServiceArtifact aService2 = (ServiceArtifact)b;

            x1 = aService1.getxCoordinate() + aService1.getHeight() * 3 / 2;
            y1 = aService1.getyCoordinate() + aService1.getHeight() / 2;

            x2 = aService2.getxCoordinate() + aService2.getHeight() / 2;
            y2 = aService2.getyCoordinate() + aService2.getHeight() / 2;

        }

        else if (a instanceof ReferenceArtifact && b instanceof ReferenceArtifact) {

            ReferenceArtifact aReference1 = (ReferenceArtifact)a;
            ReferenceArtifact aReference2 = (ReferenceArtifact)b;

            x1 = aReference1.getxCoordinate() + aReference1.getHeight() * 3 / 2;
            y1 = aReference1.getyCoordinate() + aReference1.getHeight() / 2;

            x2 = aReference2.getxCoordinate() + aReference2.getHeight() / 2;
            y2 = aReference2.getyCoordinate() + aReference2.getHeight() / 2;

        }

        polyline = setWireAttributes(x1, y1, x2, y2, polyline, changingFactor, color);

//        polyline.setAttributeNS(null, "stroke-dasharray", "3 3");
        polyline.setAttributeNS(null, "class", "wire dashedWire");


        return polyline;
    }

    /**
     * <script type="text/ecmascript"><![CDATA[
    	<SCRIPT LANGUAGE="JavaScript">

    var test = document.open("store_diagram.svg");

    </SCRIPT>
    ]]></script>

    <!--rect x="0" y="0" alignment-baseline="central" fill="#E5E5D0" width="1400" rx="20" ry="20" height="560" stroke="#919191"/><rect x="200" y="100" alignment-baseline="central" fill="#E5E5E5" width="1000" rx="20" ry="20" height="360" stroke="#919191"/><text x="700" font-size="20" dominant-baseline="mathematical" y="120" text-anchor="middle">

    <a xlink:href="store_diagram.svg" xlink:show="new">MyValueComposite2</a></text--> 

     */

}
