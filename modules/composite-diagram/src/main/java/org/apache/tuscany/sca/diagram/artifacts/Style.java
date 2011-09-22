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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CSS style element
 */
public class Style {
    private static final String DEFAULT_CSS = "rect.composite {" + "          fill: #E5E5E5;"
        + "          stroke: #919191;"
        + "          alignment-baseline: middle;"
        + "      }\n"
        + "      rect.component {"
        + "          fill: #3D59AB;"
        + "          stroke: #104E8B;"
        + "          fill-opacity: 0.75;"
        + "      }\n"
        + "      rect.property {"
        + "          fill: #EEEE00;"
        + "          stroke: #EEC900;"
        + "      }\n"
        + "      polygon.reference {"
        + "          fill: #BF3EFF;"
        + "          stroke: #68228B;"
        + "      }\n"
        + "      polygon.service {"
        + "          fill: #00CD66;"
        + "          stroke: #008B45;"
        + "      }\n"
        + "      polyline.wire {"
        + "          stroke-width: 2;"
        + "          fill: none;"
        + "      }\n"
        + "      polyline.normalWire {"
        + "      }\n"
        + "      polyline.dashedWire {"
        + "          stroke-dasharray: 3,3;"
        + "      }\n"
        + "      rect.layer {"
        + "        fill: #E5E5D0;"
        + "        stroke: #919191;"
        + "        alignment-baseline: middle;"
        + "      }\n";

    private static volatile String css;

    public Element addElement(Document document, String svgNs, String styleSheet) {

        Element style = document.createElementNS(svgNs, "style");
        style.setAttributeNS(null, "type", "text/css");

        if (styleSheet == null) {
            try {
                if (css == null) {
                    css = readCSS();
                }
            } catch (IOException e) {
                // Ignore
                css = DEFAULT_CSS;
            }
            styleSheet = css;
        }

        CDATASection cdata = document.createCDATASection(styleSheet);
        style.appendChild(cdata);

        return style;
    }

    private static String readCSS() throws IOException {
        InputStream is = Style.class.getResourceAsStream("composite-diagram.css");
        InputStreamReader reader = new InputStreamReader(is, "UTF-8");

        StringWriter sw = new StringWriter();
        char[] buf = new char[4096];
        while (true) {
            int size = reader.read(buf);
            if (size < 0) {
                break;
            } else {
                sw.write(buf, 0, size);
            }
        }
        reader.close();
        String template = sw.toString();
        // return template.replaceFirst("/\\*(?:.|[\\n\\r])*?\\*/", ""); // Unfortunately it causes StackOverFlow in Apache Jenkins build
        int i1 = template.indexOf("/*");
        int i2 = template.indexOf("*/", i1);
        String result = template.substring(0, i1) + template.substring(i2 + 2, template.length());
        return result;
    }
}
