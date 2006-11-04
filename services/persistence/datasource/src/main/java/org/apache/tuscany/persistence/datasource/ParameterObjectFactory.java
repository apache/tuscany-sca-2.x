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

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Instantiates a new parameter using a <code>PropertyEditor</code>
 *
 * @version $Rev$ $Date$
 */
public class ParameterObjectFactory<T> implements ObjectFactory<T> {
    private PropertyEditor editor;
    private String val;

    public ParameterObjectFactory(PropertyEditor editor, String val) {
        this.editor = editor;
        this.val = val;
    }

    @SuppressWarnings("unchecked")
    public synchronized T getInstance() throws ObjectCreationException {
        try {
            editor.setAsText(val);
            return (T) editor.getValue();
        } catch (IllegalArgumentException e) {
            throw new ObjectCreationException(e);
        }
    }
}
