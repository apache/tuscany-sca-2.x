/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.core.loader.assembly;

import javax.xml.namespace.QName;

/**
 * @version $Rev$ $Date$
 */
public final class AssemblyConstants {
    public static final String SCA_NAMESPACE = "http://www.osoa.org/xmlns/sca/0.9";

    public static final QName COMPONENT = new QName(SCA_NAMESPACE, "component");
    public static final QName COMPONENT_TYPE = new QName(SCA_NAMESPACE, "componentType");
    public static final QName ENTRY_POINT = new QName(SCA_NAMESPACE, "entryPoint");
    public static final QName EXTERNAL_SERVICE = new QName(SCA_NAMESPACE, "externalService");
    public static final QName IMPORT_WSDL = new QName(SCA_NAMESPACE, "import.wsdl");
    public static final QName INTERFACE_JAVA = new QName(SCA_NAMESPACE, "interface.java");
    public static final QName INTERFACE_WSDL = new QName(SCA_NAMESPACE, "interface.wsdl");
    public static final QName MODULE = new QName(SCA_NAMESPACE, "module");
    public static final QName MODULE_FRAGMENT = new QName(SCA_NAMESPACE, "moduleFragment");
    public static final QName PROPERTY = new QName(SCA_NAMESPACE, "property");
    public static final QName PROPERTIES = new QName(SCA_NAMESPACE, "properties");
    public static final QName REFERENCE = new QName(SCA_NAMESPACE, "reference");
    public static final QName REFERENCES = new QName(SCA_NAMESPACE, "references");
    public static final QName SERVICE = new QName(SCA_NAMESPACE, "service");
    public static final QName WIRE = new QName(SCA_NAMESPACE, "wire");
    public static final QName WIRE_SOURCE = new QName(SCA_NAMESPACE, "source.uri");
    public static final QName WIRE_TARGET = new QName(SCA_NAMESPACE, "target.uri");

    private AssemblyConstants() {
    }
}
