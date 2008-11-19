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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

public class GeneratedClassLoader extends SecureClassLoader {
    private class GeneratedClass {
        private String className;
        private byte[] byteCode;
        private Class<?> cls;

        public GeneratedClass(String className, byte[] byteCode) {
            super();
            this.className = className;
            this.byteCode = byteCode;
        }

        public synchronized Class<?> getGeneratedClass() {
            if (cls == null) {
                cls = defineClass(className, byteCode, 0, byteCode.length);
            }
            return cls;
        }
    }

    private Map<String, GeneratedClass> generatedClasses = new HashMap<String, GeneratedClass>();

    public GeneratedClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        GeneratedClass cls = generatedClasses.get(className);
        if (cls != null) {
            return cls.getGeneratedClass();
        }
        return super.findClass(className);
    }

    public synchronized Class<?> getGeneratedClass(String className, byte[] byteCode) {
        GeneratedClass cls = generatedClasses.get(className);
        if (cls == null) {
            cls = new GeneratedClass(className, byteCode);
            generatedClasses.put(className, cls);
        }
        return cls.getGeneratedClass();
    }
}
