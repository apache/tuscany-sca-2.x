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
package org.apache.tuscany.persistence.datasource;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

import org.apache.tuscany.spi.ObjectCreationException;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ParameterObjectFactoryTestCase extends TestCase {

    public void testGetValue() throws Exception {
        PropertyEditor editor = PropertyEditorManager.findEditor(Integer.TYPE);
        ParameterObjectFactory<Integer> factory = new ParameterObjectFactory<Integer>(editor, "1");
        assertEquals(new Integer(1), factory.getInstance());
    }

    public void testGetInvalidValue() throws Exception {
        PropertyEditor editor = PropertyEditorManager.findEditor(Integer.TYPE);
        try {
            ParameterObjectFactory<Integer> factory = new ParameterObjectFactory<Integer>(editor, "D");
            factory.getInstance();
            fail();
        } catch (ObjectCreationException e) {
            // expected
        }
    }

}
