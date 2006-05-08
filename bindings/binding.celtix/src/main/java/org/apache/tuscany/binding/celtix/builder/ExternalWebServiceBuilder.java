/*  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.binding.celtix.builder;

import org.apache.tuscany.binding.celtix.assembly.WebServiceBinding;
import org.apache.tuscany.binding.celtix.config.ExternalWebServiceContextFactory;
import org.apache.tuscany.binding.celtix.handler.ExternalWebServiceClient;
import org.apache.tuscany.core.extension.ExternalServiceBuilderSupport;
import org.apache.tuscany.core.extension.ExternalServiceContextFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.model.assembly.ExternalService;
import org.objectweb.celtix.Bus;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;


/**
 * Creates a <code>ContextFactoryBuilder</code> for an external service configured with the {@link
 * WebServiceBinding}
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ExternalWebServiceBuilder extends ExternalServiceBuilderSupport<WebServiceBinding> {

    private Bus bus;

    @Override
    @Init(eager = true)
    public void init() throws Exception {
        super.init();
        bus = Bus.init();
    }

    protected ExternalServiceContextFactory createExternalServiceContextFactory(
            ExternalService externalService) {
        ExternalWebServiceClient externalWebServiceClient = new ExternalWebServiceClient(bus,
                externalService);
        return new ExternalWebServiceContextFactory(externalService.getName(),
                new SingletonObjectFactory<ExternalWebServiceClient>(externalWebServiceClient));
    }
}
