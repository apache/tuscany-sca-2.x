/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.model.assembly;

import javax.wsdl.Definition;

/**
 * Model object that represents the import of an external WSDL definition.
 *
 * @version $Rev$ $Date$
 */
public interface ImportWSDL extends AssemblyModelObject {
    /**
     * Returns the location where the WSDL definition can be found.
     * @return the location where the WSDL definition can be found
     */
    String getLocation();

    /**
     * Set the location where the WSDL definition can be found.
     * @param uri the location where the WSDL definition can be found
     */
    void setLocation(String uri);

    /**
     * Returns the namespace URI for this import.
     * @return the namespace URI for this import
     */
    String getNamespace();

    /**
     * Sets the namespace URI for this import.
     * @param uri the namespace URI for this import
     */
    void setNamespace(String uri);

    /**
     * Returns the WSDL Definition.
     * @return the WSDL Definition
     */
    Definition getDefinition();

    /**
     * Sets the WSDL Definition.
     * @param definition the WSDL Definition
     */
    void setDefinition(Definition definition);
}
