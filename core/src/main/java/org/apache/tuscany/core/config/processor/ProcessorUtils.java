/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.config.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.extension.config.ImplementationProcessor;
import org.apache.tuscany.model.assembly.AssemblyFactory;

/**
 * Temporary class to create bootstrap {@link ImplementationProcessor}s
 *
 * @version $$Rev$$ $$Date$$
 */
public class ProcessorUtils {

    private ProcessorUtils() {
    }

    public static List<ImplementationProcessor> createCoreProcessors(AssemblyFactory factory) {
        List<ImplementationProcessor> processors = new ArrayList<ImplementationProcessor>();
        processors.add(new PropertyProcessor(factory));
        processors.add(new ReferenceProcessor(factory));
        processors.add(new ScopeProcessor(factory));
        processors.add(new ServiceProcessor(factory));
        processors.add(new InitProcessor(factory));
        processors.add(new DestroyProcessor(factory));
        processors.add(new ContextProcessor(factory));
        processors.add(new ComponentNameProcessor(factory));
        processors.add(new DefaultProcessor(factory));
        return processors;
    }

}
