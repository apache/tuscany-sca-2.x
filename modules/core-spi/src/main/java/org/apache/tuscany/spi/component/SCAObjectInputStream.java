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
package org.apache.tuscany.spi.component;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Deserializes an object based on the thread context classloader and provides special handling for {@link
 * SCAExternalizable}
 *
 * @version $Rev$ $Date$
 */
public class SCAObjectInputStream extends ObjectInputStream {
    private final ClassLoader classLoader;
    private WorkContext context;

    public SCAObjectInputStream(InputStream in, WorkContext context) throws IOException, SecurityException {
        super(in);
        this.context = context;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        enableResolveObject(true);
    }

    protected Class resolveClass(ObjectStreamClass streamClass) throws IOException, ClassNotFoundException {
        return classLoader.loadClass(streamClass.getName());
    }

    protected Object resolveObject(Object obj) throws IOException {
        if (obj instanceof SCAExternalizable) {
            SCAExternalizable ext = (SCAExternalizable) obj;
            ext.setWorkContext(context);
            ext.reactivate();
        }
        return obj;
    }


}
