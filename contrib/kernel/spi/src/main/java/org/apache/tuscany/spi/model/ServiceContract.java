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
package org.apache.tuscany.spi.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class representing service contract information
 *
 * @version $Rev$ $Date$
 */
public abstract class ServiceContract<T> extends ModelObject implements Cloneable {
    protected boolean conversational;
    protected boolean remotable;
    protected Class<?> interfaceClass;
    protected String interfaceName;
    protected String callbackName;
    protected Class<?> callbackClass;
    protected Map<String, Operation<T>> operations;
    protected Map<String, Operation<T>> callbackOperations;
    protected String dataBinding;
    protected Map<String, Object> metaData;

    protected ServiceContract() {
    }

    protected ServiceContract(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    protected ServiceContract(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * Returns the interface name for the contract
     *
     * @return the interface name for the contract
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Sets the interface name for the contract
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * Returns the class used to represent the service contract
     */
    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    /**
     * Sets the class used to represent the service contract
     */
    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    /**
     * Returns true if the service contract is conversational
     *
     * @return true if the service contract is conversational
     */
    public boolean isConversational() {
        return conversational;
    }

    /**
     * Sets if the service contract is conversational
     *
     * @param conversational the conversational attribute
     */
    public void setConversational(boolean conversational) {
        this.conversational = conversational;
    }

    /**
     * @return the remotable
     */
    public boolean isRemotable() {
        return remotable;
    }

    /**
     * @param remotable the remotable to set
     */
    public void setRemotable(boolean remotable) {
        this.remotable = remotable;
    }

    /**
     * Returns the name of the callback or null if the contract is unidirectional
     */
    public String getCallbackName() {
        return callbackName;
    }

    /**
     * Sets the name of the callback service
     */
    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }

    /**
     * Returns the name of the callback service
     */
    public Class<?> getCallbackClass() {
        return callbackClass;
    }

    public void setCallbackClass(Class<?> callbackClass) {
        this.callbackClass = callbackClass;
    }

    public Operation getOperation(String name) {
        return operations == null ? null : operations.get(name);
    }

    public Map<String, Operation<T>> getOperations() {
        if (operations == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(operations);
    }

    public void setOperations(Map<String, Operation<T>> operations) {
        for (Operation<T> operation : operations.values()) {
            operation.setServiceContract(this);
        }
        this.operations = operations;
    }

    public Map<String, Operation<T>> getCallbackOperations() {
        if (callbackOperations == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(callbackOperations);
    }

    public void setCallbackOperations(Map<String, Operation<T>> callbacksOperations) {
        for (Operation<T> operation : callbacksOperations.values()) {
            operation.setServiceContract(this);
            operation.setCallback(true);
        }
        this.callbackOperations = callbacksOperations;
    }

    public String getDataBinding() {
        return dataBinding;
    }

    public void setDataBinding(String dataBinding) {
        this.dataBinding = dataBinding;
    }

    /**
     * Returns a map of metadata key to value mappings for the operation.
     *
     * @return a map of metadata key to value mappings for the operation.
     */
    public Map<String, Object> getMetaData() {
        if (metaData == null) {
            return Collections.emptyMap();
        }
        return metaData;
    }

    /**
     * Adds metadata associated with the operation.
     *
     * @param key the metadata key
     * @param val the metadata value
     */
    public void setMetaData(String key, Object val) {
        if (metaData == null) {
            metaData = new HashMap<String, Object>();
        }
        metaData.put(key, val);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ServiceContract that = (ServiceContract) o;

        if (callbackName != null ? !callbackName.equals(that.callbackName) : that.callbackName != null) {
            return false;
        }
        if (callbackOperations != null ? !callbackOperations.equals(that.callbackOperations)
            : that.callbackOperations != null) {
            return false;
        }
        if (interfaceClass != null ? !interfaceClass.equals(that.interfaceClass) : that.interfaceClass != null) {
            return false;
        }
        if (interfaceName != null ? !interfaceName.equals(that.interfaceName) : that.interfaceName != null) {
            return false;
        }
        return !(operations != null ? !operations.equals(that.operations) : that.operations != null);

    }

    public int hashCode() {
        int result;
        result = interfaceClass != null ? interfaceClass.hashCode() : 0;
        result = 29 * result + (interfaceName != null ? interfaceName.hashCode() : 0);
        result = 29 * result + (callbackName != null ? callbackName.hashCode() : 0);
        result = 29 * result + (operations != null ? operations.hashCode() : 0);
        result = 29 * result + (callbackOperations != null ? callbackOperations.hashCode() : 0);
        return result;
    }

    public String toString() {
        if (interfaceName != null) {
            return new StringBuilder().append("ServiceContract[").append(interfaceName).append("]").toString();
        } else if (interfaceClass != null) {
            return new StringBuilder().append("ServiceContract[").append(interfaceClass.getName()).append("]")
                .toString();
        } else {
            return super.toString();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() throws CloneNotSupportedException {
        ServiceContract<T> copy = (ServiceContract<T>) super.clone();

        if (operations != null) {
            Map<String, Operation<T>> clonedOperations = new HashMap<String, Operation<T>>();
            for (Operation<T> o : operations.values()) {
                clonedOperations.put(o.getName(), (Operation<T>)o.clone());
            }
            copy.setOperations(clonedOperations);
        }

        if (callbackOperations != null) {
            Map<String, Operation<T>> clonedCallbackOperations = new HashMap<String, Operation<T>>();
            for (Operation<T> o : callbackOperations.values()) {
                clonedCallbackOperations.put(o.getName(), (Operation<T>)o.clone());
            }
            copy.setCallbackOperations(clonedCallbackOperations);
        }

        if (this.metaData != null) {
            assert this.metaData instanceof HashMap;
            copy.metaData = (HashMap) ((HashMap) this.metaData).clone();
        }

        return copy;
    }
}
