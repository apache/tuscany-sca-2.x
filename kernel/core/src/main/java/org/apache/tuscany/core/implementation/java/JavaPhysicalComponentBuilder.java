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
package org.apache.tuscany.core.implementation.java;

import org.apache.tuscany.core.component.JavaPhysicalComponentDefinition;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;

/**
 * Java physical component builder.
 * 
 * @version $Rev$ $Date$
 *
 */
public class JavaPhysicalComponentBuilder implements
    PhysicalComponentBuilder<JavaPhysicalComponentDefinition, JavaAtomicComponent> {

    /**
     * Builds a component from its physical component definition.
     * 
     * @param componentDefinition Physical component definition of the component
     *            to be built.
     * @return A component instance that is ready to go live.
     * @throws BuilderException If unable to build the component.
     */
    public JavaAtomicComponent build(JavaPhysicalComponentDefinition componentDefinition) throws BuilderException {
        
        //byte[] instanceFactoryByteCode = componentDefinition.getInstanceFactoryByteCode();
        
        // TODO I am sure this is not the right classloader.
        // commented out for PMD
        //InstanceFactoryLoader cl = new InstanceFactoryLoader();
        //Class instanceFactoryClass = cl.loadClass(instanceFactoryByteCode);
        
        
        // TODO Do the rest
        return null;
    }
    
    /*
     * Allows to load the class from byte code.
     * 
     * TODO Is there a better way to do this?
     */
// commented out for PMD
//    private class InstanceFactoryLoader extends ClassLoader {
//        public Class loadClass(byte[] byteCode) {
//            return defineClass(null, byteCode, 0, byteCode.length);
//        }
//    }

}
