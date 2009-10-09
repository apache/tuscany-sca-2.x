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
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.databinding.javabeans.JavaExceptionDataBinding;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * The default implementation of a data binding extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultDataBindingExtensionPoint implements DataBindingExtensionPoint {
    private ExtensionPointRegistry registry;
    private final Map<String, DataBinding> bindings = new HashMap<String, DataBinding>();
    private final List<DataBinding> databindings = new ArrayList<DataBinding>();
    private static final Logger logger = Logger.getLogger(DefaultDataBindingExtensionPoint.class.getName());
    private boolean loadedDataBindings;

    public DefaultDataBindingExtensionPoint() {
    }

    public DefaultDataBindingExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
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
        if (logger.isLoggable(Level.FINE)) {
            String className = dataBinding.getClass().getName();
            boolean lazy = false;
            if (dataBinding instanceof LazyDataBinding) {
                className = ((LazyDataBinding)dataBinding).dataBindingDeclaration.getClassName();
                lazy = true;
            }
            logger.fine("Adding databinding: " + className + ";name=" + dataBinding.getName() + ",lazy=" + lazy);
        }
        databindings.add(dataBinding);
        bindings.put(dataBinding.getName().toLowerCase(), dataBinding);

    }

    public DataBinding removeDataBinding(String id) {
        if (id == null) {
            return null;
        }
        DataBinding dataBinding = bindings.remove(id.toLowerCase());
        if (dataBinding != null) {
            databindings.remove(dataBinding);
        }
        return dataBinding;
    }

    /**
     * Dynamically load data bindings declared under META-INF/services
     */
    private synchronized void loadDataBindings() {
        if (loadedDataBindings)
            return;

        // Get the databinding service declarations
        Collection<ServiceDeclaration> dataBindingDeclarations;
        try {
            dataBindingDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(DataBinding.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Load data bindings
        for (ServiceDeclaration dataBindingDeclaration : dataBindingDeclarations) {
            Map<String, String> attributes = dataBindingDeclaration.getAttributes();
            String name = attributes.get("name");

            // Create a data binding wrapper and register it
            DataBinding dataBinding = new LazyDataBinding(name, dataBindingDeclaration);
            addDataBinding(dataBinding);
        }

        loadedDataBindings = true;
    }

    /**
     * A data binding facade allowing data bindings to be lazily loaded and
     * initialized.
     */
    private class LazyDataBinding implements DataBinding {

        private String name;
        private ServiceDeclaration dataBindingDeclaration;
        private DataBinding dataBinding;

        private LazyDataBinding(String type, ServiceDeclaration dataBindingDeclaration) {
            this.name = type;
            this.dataBindingDeclaration = dataBindingDeclaration;
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
                    Class<DataBinding> dataBindingClass = (Class<DataBinding>)dataBindingDeclaration.loadClass();
                    try {
                        Constructor<DataBinding> constructor = dataBindingClass.getConstructor();
                        dataBinding = constructor.newInstance();
                    } catch (NoSuchMethodException e) {
                        Constructor<DataBinding> constructor =
                            dataBindingClass.getConstructor(ExtensionPointRegistry.class);
                        dataBinding = constructor.newInstance(DefaultDataBindingExtensionPoint.this.registry);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return dataBinding;
        }

        public Object copy(Object object, DataType dataType, Operation operation) {
            return getDataBinding().copy(object, dataType, operation);
        }

        public String getName() {
            return name;
        }

        public XMLTypeHelper getXMLTypeHelper() {
            return getDataBinding().getXMLTypeHelper();
        }

        public WrapperHandler getWrapperHandler() {
            return getDataBinding().getWrapperHandler();
        }

        public boolean introspect(DataType dataType, Operation operation) {
            return getDataBinding().introspect(dataType, operation);
        }

        public DataType introspect(Object value, Operation operation) {
            return getDataBinding().introspect(value, operation);
        }
    }

    //FIXME The following methods should not be on the extension point
    // they should be on a separate class
    public boolean introspectType(DataType dataType, Operation operation) {
        loadDataBindings();
        for (DataBinding binding : databindings) {
            // don't introspect for JavaBeansDatabinding as all javatypes will
            // anyways match to its basetype
            // which is java.lang.Object. Default to this only if no databinding
            // results
            if (!binding.getName().equals(JavaBeansDataBinding.NAME)) {
                if (binding.introspect(dataType, operation)) {
                    return true;
                }
            }
        }
        // FIXME: Should we honor the databinding from operation/interface
        // level?
        Class<?> physical = dataType.getPhysical();
        if (physical == Object.class) {
            dataType.setDataBinding(JavaBeansDataBinding.NAME);
            return false;
        }
        if (dataType.getPhysical().isArray()) {
            introspectArray(dataType, operation);
            return true;
        } else if (Throwable.class.isAssignableFrom(physical)) {
            dataType.setDataBinding(JavaExceptionDataBinding.NAME);
            return true;
        } else {
            dataType.setDataBinding(JavaBeansDataBinding.NAME);
            return false;
        }
    }

    private boolean introspectArray(DataType dataType, Operation operation) {
        Class<?> physical = dataType.getPhysical();
        if (!physical.isArray() || physical == byte[].class) {
            return false;
        }
        Class<?> componentType = physical.getComponentType();
        Type genericComponentType = componentType;
        if(dataType.getGenericType() instanceof GenericArrayType) {
            genericComponentType = ((GenericArrayType) dataType.getGenericType()).getGenericComponentType();
        }
        DataType logical = new DataTypeImpl(dataType.getDataBinding(), componentType, genericComponentType, dataType.getLogical());
        introspectType(logical, operation);
        dataType.setDataBinding("java:array");
        dataType.setLogical(logical);
        return true;
    }

    public DataType introspectType(Object value, Operation operation) {
        loadDataBindings();
        DataType dataType = null;
        for (DataBinding binding : databindings) {
            // don't introspect for JavaBeansDatabinding as all javatypes will
            // anyways match to its basetype
            // which is java.lang.Object. Default to this only if no databinding
            // results
            if (!binding.getName().equals(JavaBeansDataBinding.NAME)) {
                dataType = binding.introspect(value, operation);
            }
            if (dataType != null) {
                return dataType;
            }
        }
        return new DataTypeImpl<XMLType>(JavaBeansDataBinding.NAME, value.getClass(), XMLType.UNKNOWN);
    }
}
