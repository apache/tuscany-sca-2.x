package org.apache.tuscany.sca.binding.local;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

public class LocalSCABindingProviderFactory implements BindingProviderFactory<LocalSCABinding> {
    private ExtensionPointRegistry extensionPoints;
    private SCABindingMapper scaBindingMapper;

    public LocalSCABindingProviderFactory(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        this.scaBindingMapper = utilities.getUtility(SCABindingMapper.class);
    }

    @Override
    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeEndpointReference endpointReference) {
        return new LocalSCAReferenceBindingProvider(extensionPoints, endpointReference, scaBindingMapper);
    }

    @Override
    public ServiceBindingProvider createServiceBindingProvider(RuntimeEndpoint endpoint) {
        return new LocalSCAServiceBindingProvider(endpoint, scaBindingMapper);
    }

    @Override
    public Class<LocalSCABinding> getModelType() {
        return LocalSCABinding.class;
    }

}
