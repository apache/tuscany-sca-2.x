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
package org.apache.tuscany.sca.core.spring.assembly.impl;

import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.WireableBinding;
import org.springframework.beans.factory.config.RuntimeBeanReference;

/**
 * An implementation of RuntimeBeanReference wrappering an SCA assembly
 * Reference
 * 
 * @version $Rev$ $Date$
 */
public class BeanReferenceImpl extends RuntimeBeanReference {
    private Reference reference;

    protected BeanReferenceImpl(Reference reference) {
        super("temp");
        this.reference = reference;
    }

    public String getBeanName() {
        SCABinding binding = reference.getBinding(SCABinding.class);
        if (binding instanceof WireableBinding) {
            return ((WireableBinding) binding).getTargetComponent().getURI();
        } else {
            return null;
        }
    }

    public boolean equals(Object other) {
        if (this != other) {
            if (other instanceof RuntimeBeanReference) {
                RuntimeBeanReference br = (RuntimeBeanReference)other;
                return (getBeanName().equals(br.getBeanName()) && this.isToParent() == br.isToParent());
            } else
                return false;
        } else
            return true;
    }

    public int hashCode() {
        return getBeanName().hashCode() * 29 + (this.isToParent() ? 1 : 0);
    }

}
