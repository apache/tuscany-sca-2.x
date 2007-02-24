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
package org.apache.tuscany.sca.extensions.spring;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import junit.framework.TestCase;

/**
 * Verifies beans contained in a Spring composite are properly wired to as targets.
 *
 * @version $Rev$ $Date$
 */
@Service(WiringTestComponent.class)
public class WiringTestComponentImpl extends TestCase {
    private TestBean bean;

    /**
     * Constructor. Accepts a reference to a Spring Bean
     *
     * @param bean the Spring Bean
     */
    public WiringTestComponentImpl(@Reference(name = "bean")TestBean bean) {
        this.bean = bean;
    }

    /**
     * Invoke to a target Spring Bean
     */
    public void testTargetInvocation() {
        assertEquals("test", bean.echo("test"));
    }
}
