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

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * Generates javascript proxies for the comet services.
 */
public class JavascriptGenerator {

    /**
     * Namespace for the Tuscany Comet Javascript toolkit.
     */
    public static final String JS_NAMESPACE = "SCA";

    /**
     * Name for the SCA component context.
     */
    private static final String COMPONENT_CONTEXT = "this.CometComponentContext";

    /**
     * Name for the object performing comet specific tasks.
     */
    private static final String TUSCANY_COMET = "SCA.TuscanyComet";

    private static StringBuffer javascript = new StringBuffer();

    private JavascriptGenerator() {
    }

    public static StringBuffer getJavascript() {
        return JavascriptGenerator.javascript;
    }

    /**
     * Generates the javascript proxy for a service.
     * 
     * @param service
     *            the service for which generation is performed
     */
    public static void generateServiceProxy(final ComponentService service) {
        JavascriptGenerator.javascript.append(JavascriptGenerator.COMPONENT_CONTEXT + "." + service.getName()
                + " = new Object();\n");
    }

    /**
     * Generates the method inside the service proxy for the specified
     * operation.
     * 
     * @param service
     *            the service containing the operation
     * @param operation
     *            the operation
     */
    public static void generateMethodProxy(final ComponentService service, final Operation operation) {
        JavascriptGenerator.javascript.append(JavascriptGenerator.COMPONENT_CONTEXT + "." + service.getName() + "."
                + operation.getName() + " = function(");
        for (int i = 0; i < operation.getInputType().getLogical().size(); i++) {
            JavascriptGenerator.javascript.append("p" + i + ", ");
        }
        JavascriptGenerator.javascript.append("callbackMethod) {\n");
        // send method argumets as JSON array
        JavascriptGenerator.javascript.append("  var params = [];\n");
        for (int i = 0; i < operation.getInputType().getLogical().size(); i++) {
            JavascriptGenerator.javascript.append("  params.push(p" + i + ");\n");
        }
        JavascriptGenerator.javascript.append("  " + JavascriptGenerator.TUSCANY_COMET + ".callAsync('"
                + service.getName() + "/" + operation.getName() + "', $.toJSON(params), callbackMethod);\n");
        JavascriptGenerator.javascript.append("}\n");
    }

}
