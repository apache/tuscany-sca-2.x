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
package org.apache.tuscany.core.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.loader.assembly.ComponentTypeLoader;
import org.apache.tuscany.core.loader.assembly.ComponentLoader;
import org.apache.tuscany.core.loader.assembly.EntryPointLoader;
import org.apache.tuscany.core.loader.assembly.ExternalServiceLoader;
import org.apache.tuscany.core.loader.assembly.ModuleFragmentLoader;
import org.apache.tuscany.core.loader.assembly.ModuleLoader;
import org.apache.tuscany.core.loader.assembly.PropertyLoader;
import org.apache.tuscany.core.loader.assembly.ReferenceLoader;
import org.apache.tuscany.core.loader.assembly.ServiceLoader;
import org.apache.tuscany.core.loader.assembly.InterfaceWSDLLoader;
import org.apache.tuscany.core.loader.system.SystemImplementationLoader;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.AssemblyModelContext;

/**
 * @version $Rev$ $Date$
 */
public final class StAXUtil {
    private static final Map<String, Multiplicity> MULTIPLICITY = new HashMap<String, Multiplicity>(4);
    private static final Map<String, OverrideOption> OVERRIDE_OPTIONS = new HashMap<String, OverrideOption>(3);

    static {
        MULTIPLICITY.put("0..1", Multiplicity.ZERO_ONE);
        MULTIPLICITY.put("1..1", Multiplicity.ONE_ONE);
        MULTIPLICITY.put("0..n", Multiplicity.ZERO_N);
        MULTIPLICITY.put("1..n", Multiplicity.ONE_N);

        OVERRIDE_OPTIONS.put("no", OverrideOption.NO);
        OVERRIDE_OPTIONS.put("may", OverrideOption.MAY);
        OVERRIDE_OPTIONS.put("must", OverrideOption.MUST);
    }

    private StAXUtil() {
    }

    public static void skipToEndElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 0;
        while (true) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (depth == 0) {
                    return;
                }
                depth--;
            }
        }
    }

    public static Multiplicity multiplicity(String multiplicity, Multiplicity def) {
        return multiplicity == null ? def : MULTIPLICITY.get(multiplicity);
    }

    public static OverrideOption overrideOption(String overrideOption, OverrideOption def) {
        return overrideOption == null ? def : OVERRIDE_OPTIONS.get(overrideOption);
    }

    public static ModuleComponent bootstrapLoader(String name, AssemblyModelContext context) {
        SystemAssemblyFactory factory = new SystemAssemblyFactoryImpl();
        Module module = factory.createModule();
        module.setName("org.apache.tuscany.core.system.loader");
        List<Component> components = module.getComponents();

        components.add(bootstrapLoader(factory, ComponentLoader.class));
        components.add(bootstrapLoader(factory, ComponentTypeLoader.class));
        components.add(bootstrapLoader(factory, EntryPointLoader.class));
        components.add(bootstrapLoader(factory, ExternalServiceLoader.class));
        components.add(bootstrapLoader(factory, InterfaceWSDLLoader.class));
        components.add(bootstrapLoader(factory, ModuleFragmentLoader.class));
        components.add(bootstrapLoader(factory, ModuleLoader.class));
        components.add(bootstrapLoader(factory, PropertyLoader.class));
        components.add(bootstrapLoader(factory, ReferenceLoader.class));
        components.add(bootstrapLoader(factory, ServiceLoader.class));

        components.add(bootstrapLoader(factory, SystemImplementationLoader.class));

        ModuleComponent mc = factory.createModuleComponent();
        mc.setName(name);
        mc.setModuleImplementation(module);

        mc.initialize(context);
        return mc;
    }

    private static Component bootstrapLoader(SystemAssemblyFactory factory, Class<?> loaderClass) {
        SystemImplementation implementation = factory.createSystemImplementation();
        implementation.setImplementationClass(loaderClass);

        Component component = factory.createSimpleComponent();
        component.setName(loaderClass.getName());
        component.setComponentImplementation(implementation);
        return component;
    }
}
