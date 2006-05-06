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
package org.apache.tuscany.container.rhino.assembly;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.assembly.impl.AtomicImplementationImpl;

import commonj.sdo.helper.TypeHelper;

/**
 * Default implementation of a JavScript component implementation type
 *
 * @version $Rev$ $Date$
 */
public class JavaScriptImplementation extends AtomicImplementationImpl {

    private String scriptFile;

    private String script;

    private ResourceLoader resourceLoader;

    private TypeHelper typeHelper;

    public JavaScriptImplementation() {
        super();
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    public void setTypeHelper(TypeHelper typeHelper) {
        this.typeHelper = typeHelper;
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String fn) {
        scriptFile = fn;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
