package org.apache.tuscany.sca.binding.local;

import org.apache.tuscany.sca.binding.sca.provider.SCABindingAsyncResponseInvoker;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.InvokerAsyncResponse;
import org.apache.tuscany.sca.provider.EndpointAsyncProvider;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

public class LocalSCAServiceBindingProvider implements EndpointAsyncProvider {
    private RuntimeEndpoint endpoint;

    public LocalSCAServiceBindingProvider(RuntimeEndpoint endpoint, SCABindingMapper scaBindingMapper) {
        this.endpoint = endpoint;
    }

    @Override
    public InterfaceContract getBindingInterfaceContract() {
        return endpoint.getComponentTypeServiceInterfaceContract();
    }

    @Override
    public InvokerAsyncResponse createAsyncResponseInvoker() {
        return new SCABindingAsyncResponseInvoker(null, null);
    }

    @Override
    public boolean supportsOneWayInvocation() {
        // Default for Local invocation
        return false;
    }

    @Override
    public boolean supportsNativeAsync() {
        return true;
    }

    @Override
    public void stop() {
        // Nothing required for local invocation
    }

    @Override
    public void start() {
        // Nothing required for local invocation
    }

    @Override
    public void configure() {
        // Nothing required for local invocation
    }
}
