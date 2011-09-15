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

package org.apache.tuscany.sca.diagram.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class HTMLWrapper {

    private File htmlFilePath;
    private String compositeName;
    private String svg;

    /**
     * 
     * @param svg
     * @param compositeName
     * @param htmlFilePath
     */
    public HTMLWrapper(String svg, String compositeName, File htmlFilePath) {

        this.svg = svg;
        this.compositeName = compositeName;
        this.htmlFilePath = htmlFilePath;
    }

    public String buildHTML() throws Exception {
        String content =
            "" + "<html>\n"
                + "<head>\n"
                + "<h1 align='center'>Apache Tuscany - Composite Diagram Generator</h1>\n"
                + "<h2 align='center'>"
                + compositeName
                + "</h2>\n"
                + "</br>\n"
                +
                //				"<script type=\"text/javascript\" src=\""+CANVG_LIB_DIR+RGB_FILE+"\"></script>\n" +
                //				"<script type=\"text/javascript\" src=\""+CANVG_LIB_DIR+CANVG_FILE+"\"></script>\n" +
                //				"<script type=\"text/javascript\">\n" +
                //				"window.onload = function() {\n" +
                //				"//load '../path/to/your.svg' in the canvas with id = 'canvas'\n" +
                //				"canvg('canvas', '"+svgFileName+"')\n" +
                //				"//load a svg snippet in the canvas with id = 'drawingArea'\n" +
                //				"canvg(document.getElementById('drawingArea'), '<svg>...</svg>')\n" +
                //				"canvg('canvas', '"+svgFileName+"', {})\n" +
                //				"}\n" +
                //				"</script>\n" +
                "</head>\n"
                + "<body>\n"
                + svg
                + "\n"
                +
                //				"<canvas id=\"canvas\" width=\""+compositeWidth+"px\" height=\""+compositeHeight+"px\">\n" +
                //				"</canvas>\n" +
                "</body>\n"
                + "</html>";

        fileWriter(content);

        return content;

    }

    private void fileWriter(String content) throws Exception {

        FileWriter fileWriter = new FileWriter(htmlFilePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.append(content);
        bufferedWriter.close();

    }
}
