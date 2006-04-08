/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.scdl.loader.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;

/**
 */
public class SCDLAssemblyModelLoaderImpl implements AssemblyModelLoader {
    private WSDLReader wsdlReader;
    private Map<String, Definition> definitions=new HashMap<String, Definition>();
    private Map<String, List<Definition>> definitionsByNamespace=new HashMap<String, List<Definition>>();

    public Definition loadDefinition(String uri) {
        Definition definition=definitions.get(uri);
        if (definition!=null)
            return definition;

        try {
            if (wsdlReader==null)
                wsdlReader=WSDLFactory.newInstance().newWSDLReader();
            definition = wsdlReader.readWSDL(uri);
        } catch (WSDLException e) {
            throw new IllegalArgumentException(e);
        }
        if (definition==null)
            throw new IllegalArgumentException("Could not load WSDL definition at "+uri);

        definitions.put(uri, definition);

        String namespace=definition.getTargetNamespace();
        List<Definition> list=definitionsByNamespace.get(namespace);
        if (list==null) {
            list=new ArrayList<Definition>();
            definitionsByNamespace.put(namespace, list);
        }
        list.add(definition);

        return definition;
    }

    public List<Definition> loadDefinitions(String namespace) {
        return definitionsByNamespace.get(namespace);
    }

}
