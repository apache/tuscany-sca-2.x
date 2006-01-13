/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.impl;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.sdo.EDataObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Interface;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.sdo.OverrideOptions;

/**
 * An implementation of the model object '<em><b>External Service</b></em>'.
 */
public class ExternalServiceImpl extends org.apache.tuscany.model.assembly.sdo.impl.ExternalServiceImpl implements ExternalService {
    private ConfiguredService configuredService;

    /**
     * Constructor
     */
    protected ExternalServiceImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ExternalServiceImpl#getName()
     */
    public String getName() {
        return super.getName();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ExternalServiceImpl#setName(java.lang.String)
     */
    public void setName(String newName) {
        super.setName(newName);
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ExternalServiceImpl#getOverridable()
     */
    public OverrideOptions getOverridable() {
        return super.getOverridable();
    }

    public void setOverridable(OverrideOptions newOverridable) {
        super.setOverridable(newOverridable);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ExternalService#getBindings()
     */
    public List<Binding> getBindings() {
        return super.getBindings();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Port#getInterfaceContract()
     */
    public Interface getInterfaceContract() {
        return (Interface) super.getInterface();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Port#setInterfaceContract(org.apache.tuscany.model.assembly.Interface)
     */
    public void setInterfaceContract(Interface value) {
        super.setInterface((org.osoa.sca.model.Interface) value);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Part#getAggregate()
     */
    public Aggregate getAggregate() {
        return (Aggregate) super.getContainer();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ExternalService#getConfiguredService()
     */
    public ConfiguredService getConfiguredService() {
        return configuredService;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        // Initialize the interface
        Interface iface = getInterfaceContract();
        if (iface != null)
            iface.initialize(modelContext);

        // Initialize the bindings
        for (Iterator<Binding> i = getBindings().iterator(); i.hasNext();) {
            Binding binding = i.next();
            binding.initialize(modelContext);
        }

        // Create a configured service for this external service
        AssemblyFactory factory = new AssemblyFactoryImpl();
        configuredService = factory.createConfiguredService();
        configuredService.setPart(this);
        Service service = factory.createService();
        service.setName("service");
        if (iface != null)
            service.setInterfaceContract((Interface) EcoreUtil.copy((EDataObject) iface));
        service.initialize(modelContext);
        configuredService.setPort(service);
        configuredService.initialize(modelContext);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

} //ExternalServiceImpl
