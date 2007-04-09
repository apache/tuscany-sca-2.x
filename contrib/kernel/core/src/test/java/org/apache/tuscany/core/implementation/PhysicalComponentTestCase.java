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
package org.apache.tuscany.core.implementation;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.core.component.InstanceFactory;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.core.component.scope.InstanceWrapperBase;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInitializationException;

/**
 * @version $Rev$ $Date$
 */
public class PhysicalComponentTestCase extends TestCase {
    public void testSomething() {

    }
    
    /**
     * This is the class supplied by the user.
     */
    public static class UserImplementation {
        @Init
        void init() {
        }

        @Destroy
        void destroy() {
        }
    }

    /**
     * This is the generated wrapper class.
     */
    public static class UserWrapper extends InstanceWrapperBase<UserImplementation> {
        public UserWrapper(UserImplementation instance) {
            super(instance);
        }

        public void start() throws TargetInitializationException {
            instance.init();
            super.start();
        }

        public void stop() throws TargetDestructionException {
            super.stop();
            instance.destroy();
        }
    }

    /**
     * This is the generated factory class.
     */
    public static class UserFactory implements InstanceFactory<UserImplementation> {
        public InstanceWrapper<UserImplementation> newInstance() {
            UserImplementation instance = new UserImplementation();
            return new UserWrapper(instance);
        }
    }
}
