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

package org.apache.tuscany.sca.implementation.widget;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Reference;

class WidgetImplementationIntrospector {
    private static final String WEB_REFERENCE_ANNOTATION = "//@Reference";
    
    private AssemblyFactory assemblyFactory;
    private WidgetImplementation widgetImplementation;
    
    WidgetImplementationIntrospector(AssemblyFactory assemblyFactory, WidgetImplementation widgetImplementation) {
        this.widgetImplementation = widgetImplementation;
        this.assemblyFactory = assemblyFactory;
    }
    
    
    /**
     * Introspect the References of a given htmlWidget
     * @return
     */
    List<Reference> getReferences() {
        List<Reference> references = new ArrayList<Reference>();
        URL htmlWidget = widgetImplementation.getLocationURL();
        
        try {
            Scanner scanner = new Scanner(htmlWidget.openStream());
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains(WEB_REFERENCE_ANNOTATION)) {
                    //process the next line, as it has the reference info
                    if(scanner.hasNextLine()) {
                        Reference reference = processReferenceScript(scanner.nextLine());
                        if(reference != null){
                            references.add(reference);
                        }
                        
                    }
                }
            }
            
        } catch(Exception e) {
            
        }
        
        return references;
        
    }
    
    /**
     * Process Reference declaration in JavaScript code
     * Supported ways :
     *    //@Reference
     *    var catalog = new Reference("catalog");
     *    
     *    //@Reference
     *    catalog = new Reference("catalog");
     *    
     * @param scriptContent
     * @return
     */
    private Reference processReferenceScript(String scriptContent) {
        Reference reference = null;
        String referenceName = null;
        
        String tokens[] = scriptContent.split("=");
        tokens = tokens[0].split(" ");
        referenceName = tokens[tokens.length -1];
        
        if(referenceName != null) {
            reference = assemblyFactory.createReference();
            reference.setName(referenceName);            
        }
        
        return reference;
    }
    
    


}
