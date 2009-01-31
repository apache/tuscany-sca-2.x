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
package org.apache.tuscany.sca.implementation.bpel;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * The model representing the BPEL implementation in an SCA assembly model.
 * 
 * @version $Rev$ $Date$
 */
public interface BPELImplementation extends Implementation {

    /**
     * Get the BPEL process Name
     * 
     * @return
     */
    QName getProcess();
    
    /**
     * Set the BPEL process Name
     * 
     * @param processName process QName
     */
    void setProcess(QName processName);
    
    /**
     * Get the BPEL process definition
     * 
     * @return
     */
    BPELProcessDefinition getProcessDefinition();
    
    /**
     * Set the BPEL process definition
     * 
     * @param processDefinition
     */
    void setProcessDefinition(BPELProcessDefinition processDefinition);
    
    /** 
     * Returns the componentType for this implementation.
     * 
     *  @return
     */
    public ComponentType getComponentType(); 

    /**
     * Sets the componentType for this implementation
     * 
     * @param componentType the component type to set
     */
    public void setComponentType(ComponentType componentType); 

    /**
     * Returns the model resolver that can be used to resolve WSDLs and XSDs
     * referenced by the BPEL process.
     * 
     * @return
     */
    ModelResolver getModelResolver();

    /**
     * Sets the model resolver that can be used to resolve WSDLs and XSDs referenced
     * by the BPEL process.
     * 
     * @param modelResolver
     */
    void setModelResolver(ModelResolver modelResolver);
}
