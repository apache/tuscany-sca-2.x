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

public class Text {

    public static Element addTextElement(Document document, String svgNs, int x, int y, String content) {
        Element text = document.createElementNS(svgNs, "text");
        text.setAttributeNS(null, "x", x + "");
        text.setAttributeNS(null, "y", y + "");
        //text.setAttributeNS(null, "text-anchor", "middle");
        text.setAttributeNS(null, "dominant-baseline", "mathematical");
        text.setAttributeNS(null, "font-size", "15");
        text.setTextContent(content);
        text.setAttributeNS(null, "class", "name");
        return text;
    }
}
