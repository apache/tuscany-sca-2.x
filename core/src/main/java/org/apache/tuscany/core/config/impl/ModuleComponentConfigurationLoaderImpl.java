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
package org.apache.tuscany.core.config.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;

/**
 * @version $Rev$ $Date$
 */
public class ModuleComponentConfigurationLoaderImpl extends AbstractModuleComponentConfigurationLoader {
    private AssemblyModelLoader modelLoader;
    
    public ModuleComponentConfigurationLoaderImpl(AssemblyModelContext modelContext) {
        super(modelContext);
        this.modelLoader=modelContext.getAssemblyLoader();
    }

    public Module loadModule(URL url) {
        return modelLoader.loadModule(url.toString());
    }

    public ModuleFragment loadModuleFragment(URL url) {
        return modelLoader.loadModuleFragment(url.toString());
    }
}
