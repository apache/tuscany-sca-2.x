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
package org.apache.tuscany.transaction.geronimo.jta.integration;

import javax.transaction.TransactionManager;

import static org.apache.tuscany.spi.bootstrap.ComponentNames.TUSCANY_SYSTEM;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;

import org.apache.tuscany.test.SCATestCase;

/**
 * @version $Rev$ $Date$
 */
public class BootstrapTestCase extends SCATestCase {
    private CompositeComponent jtaComposite;

    public void testTransactionManagerLocation() {
        AtomicComponent tmComponent = (AtomicComponent) jtaComposite.getSystemChild("TransactionManager");
        Object tm = tmComponent.getServiceInstance();
        assertTrue(tm instanceof TransactionManager);
    }

    public void testAutowire() {
        assertNotNull(jtaComposite.resolveSystemInstance(TransactionManager.class));
    }

    protected void setUp() throws Exception {
        addExtension("geronimo.jta", getClass().getClassLoader().getResource("META-INF/sca/geronimo.jta.scdl"));
        setApplicationSCDL(getClass().getClassLoader().getResource("META-INF/sca/empty.scdl"));
        super.setUp();
        RuntimeComponent runtime = (RuntimeComponent) component.getParent().getParent();
        CompositeComponent systemComposite = runtime.getSystemComponent();
        CompositeComponent topLevelComposite = (CompositeComponent) systemComposite.getChild(TUSCANY_SYSTEM);
        jtaComposite = (CompositeComponent) topLevelComposite.getChild("geronimo.jta");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
