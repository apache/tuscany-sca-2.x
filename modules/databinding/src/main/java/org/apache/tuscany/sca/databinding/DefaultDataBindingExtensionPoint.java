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
package org.apache.tuscany.sca.databinding;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.impl.TempServiceDeclarationUtil;

/**
 * The default implementation of a data binding extension point.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultDataBindingExtensionPoint implements DataBindingExtensionPoint {
    private final Map<String, DataBinding> bindings = new HashMap<String, DataBinding>();
    private boolean loadedDataBindings;
    
    public DefaultDataBindingExtensionPoint() {
    }

    public DataBinding getDataBinding(String id) {
        if (id == null) {
            return null;
        }
        DataBinding dataBinding = bindings.get(id.toLowerCase());
        if (dataBinding == null) {
            loadDataBindings();
            dataBinding = bindings.get(id.toLowerCase());
        }
        return dataBinding;
    }

    public void addDataBinding(DataBinding dataBinding) {
        bindings.put(dataBinding.getName().toLowerCase(), dataBinding);
        String[] aliases = dataBinding.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                bindings.put(alias.toLowerCase(), dataBinding);
            }
        }
    }

    public DataBinding removeDataBinding(String id) {
        if (id == null) {
            return null;
        }
        DataBinding dataBinding = bindings.remove(id.toLowerCase());
        if (dataBinding != null) {
            String[] aliases = dataBinding.getAliases();
            if (aliases != null) {
                for (String alias : aliases) {
                    bindings.remove(alias.toLowerCase());
                }
            }
        }
        return dataBinding;
    }

    /**
     * Dynamically load data bindings declared under META-INF/services
     */
    private void loadDataBindings() {
        if (loadedDataBindings)
            return;

        // Get the databinding service declarations
        ClassLoader classLoader = DataBinding.class.getClassLoader();
        Set<String> dataBindingDeclarations; 
        try {
            dataBindingDeclarations = TempServiceDeclarationUtil.getServiceClassNames(classLoader, DataBinding.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load data bindings
        for (String dataBindingDeclaration: dataBindingDeclarations) {
            Map<String, String> attributes = TempServiceDeclarationUtil.parseServiceDeclaration(dataBindingDeclaration);
            String className = attributes.get("class");
            String type = attributes.get("type");
            String name = attributes.get("name");
                
            // Create a data binding wrapper and register it
            DataBinding dataBinding = new LazyDataBinding(type, name, classLoader, className);
            addDataBinding(dataBinding);
        }
        
        loadedDataBindings = true;
    }

    
    /**
     * A data binding facade allowing data bindings to be lazily loaded and
     * initialized.
     */
    private static class LazyDataBinding implements DataBinding {

        private String name;
        private String[] aliases; 
        private WeakReference<ClassLoader> classLoader;
        private String className;
        private DataBinding dataBinding;

        private LazyDataBinding(String type, String name, ClassLoader classLoader, String className) {
            this.name = type;
            if (name != null) {
                this.aliases = new String[] {name};
            }
            this.classLoader = new WeakReference<ClassLoader>(classLoader);
            this.className = className;
        }

        /**
         * Load and instantiate the data binding class.
         * 
         * @return The data binding.
         */
        @SuppressWarnings("unchecked")
        private DataBinding getDataBinding() {
            if (dataBinding == null) {
                try {
                    Class<DataBinding> dataBindingClass =
                        (Class<DataBinding>)Class.forName(className, true, classLoader.get());
                    Constructor<DataBinding> constructor = dataBindingClass.getConstructor();
                    dataBinding = constructor.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return dataBinding;
        }

        public Object copy(Object object) {
            return getDataBinding().copy(object);
        }

        public String[] getAliases() {
            return aliases;
        }

        public ExceptionHandler getExceptionHandler() {
            return getDataBinding().getExceptionHandler();
        }

        public String getName() {
            return name;
        }

        public SimpleTypeMapper getSimpleTypeMapper() {
            return getDataBinding().getSimpleTypeMapper();
        }

        public WrapperHandler getWrapperHandler() {
            return getDataBinding().getWrapperHandler();
        }

        public boolean introspect(DataType dataType, Annotation[] annotations) {
            return getDataBinding().introspect(dataType, annotations);
        }

        public DataType introspect(Object value) {
            return getDataBinding().introspect(value);
        }
    }

    
    //FIXME The following methods should not be on the extension point
    // they should be on a separate class
    public boolean introspectType(DataType dataType, Annotation[] annotations) {
        return introspectType(dataType, annotations, false);
    }

    //
    // Leverage the DataBinding ExceptionHandler to calculate the DataType of an exception DataType
    //
    public boolean introspectType(DataType dataType, Annotation[] annotations, boolean isException) {
        loadDataBindings();
        for (DataBinding binding : bindings.values()) {
            // don't introspect for JavaBeansDatabinding as all javatypes will
            // anyways match to its basetype
            // which is java.lang.Object. Default to this only if no databinding
            // results
            if (!binding.getName().equals(JavaBeansDataBinding.NAME)) {
                if (binding.introspect(dataType, annotations)) {
                    return true;
                }
                if (isException) {
                    // Next look to see if the DB's exceptionHandler handles this exception
                    ExceptionHandler excHandler = binding.getExceptionHandler();
                    if (excHandler != null && excHandler.getFaultType(dataType) != null) {
                        // Assymetric to have the introspect() methods set the DataBindings themselves
                        // whereas we're setting it ourselves here.   
                        dataType.setDataBinding(binding.getName());
                        return true;
                    }
                }
            }
        }
        // FIXME: Should we honor the databinding from operation/interface
        // level?
        Class physical = dataType.getPhysical();
        if (physical == Object.class || Throwable.class.isAssignableFrom((Class)physical)) {
            return false;
        }
        dataType.setDataBinding(JavaBeansDataBinding.NAME);
        return false;
    }

    //
    // Didn't bother to provide special exc-handling support for this method
    //
    public DataType introspectType(Object value) {
        loadDataBindings();
        DataType dataType = null;
        for (DataBinding binding : bindings.values()) {
            // don't introspect for JavaBeansDatabinding as all javatypes will
            // anyways match to its basetype
            // which is java.lang.Object. Default to this only if no databinding
            // results
            if (!binding.getName().equals(JavaBeansDataBinding.NAME)) {
                dataType = binding.introspect(value);
            }
            if (dataType != null) {
                return dataType;
            }
        }
        return new DataTypeImpl<Class>(JavaBeansDataBinding.NAME, value.getClass(), value.getClass());
    }
}
