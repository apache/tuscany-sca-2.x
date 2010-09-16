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

package org.apache.tuscany.sca.binding.comet.runtime.javascript;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Class serving the calls performed to retrieve the Javascript toolkit.
 */
@Path("/")
@Produces("text/javascript")
public class JavascriptResource {

    /**
     * Dependencies for the Tuscany Comet Javascript API.
     */
    private static final String[] DEPENDENCIES = {"/jquery.atmosphere.js", "/jquery.json-2.2.min.js",
                                                  "/cometComponentContext.js"};

    /**
     * Method called when the Javascript toolkit is requested.
     * 
     * @return InputStream containing the Javascript code.
     */
    @GET
    public InputStream getJavascript() {
        InputStream stream = null;
        // add dependencies in the specified order
        for (final String dependency : JavascriptResource.DEPENDENCIES) {
            if (stream == null) {
                stream = this.getClass().getResourceAsStream(dependency);
            } else {
                stream = new SequenceInputStream(stream, this.getClass().getResourceAsStream(dependency));
            }
        }
        // add generated proxies
        final String generatedJs = JavascriptGenerator.getJavascript().toString() + "\n}";
        return new SequenceInputStream(stream, new ByteArrayInputStream(generatedJs.getBytes()));
    }
}
