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
package org.apache.tuscany.binding.axis2.builder;

import commonj.sdo.helper.TypeHelper;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.config.ExternalWebServiceContextFactory;
import org.apache.tuscany.binding.axis2.handler.ExternalWebServiceClient;
import org.apache.tuscany.binding.axis2.handler.WebServicePortMetaData;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.impl.ExternalServiceContextFactory;
import org.apache.tuscany.core.extension.ExternalServiceBuilderSupport;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ExternalService;
import org.osoa.sca.annotations.Scope;

import javax.xml.namespace.QName;


/**
 * Creates a <code>ContextFactory</code> for an external service configured with the {@link WebServiceBinding}
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class ExternalWebServiceBuilder extends ExternalServiceBuilderSupport {

    protected boolean handlesBindingType(Binding binding) {
        return binding instanceof WebServiceBinding;
    }

    protected ExternalServiceContextFactory createExternalServiceContextFactory(ExternalService externalService) {

        ExternalWebServiceClient externalWebServiceClient = createExternalWebServiceClient(externalService);

        return new ExternalWebServiceContextFactory(externalService.getName(),
                new SingletonObjectFactory<ExternalWebServiceClient>(externalWebServiceClient));
    }

    /**
     * Create an ExternalWebServiceClient for the WebServiceBinding
     */
    private ExternalWebServiceClient createExternalWebServiceClient(ExternalService externalService) {
        // TODO: Review should there be a single global Axis ConfigurationContext
        TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator(null, null);
        ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();

        WebServiceBinding wsBinding = (WebServiceBinding) externalService.getBindings().get(0);

        WebServicePortMetaData wsPortMetaData = new WebServicePortMetaData(wsBinding.getWSDLDefinition(),
                wsBinding.getWSDLPort(),
                wsBinding.getURI(),
                false);
        QName serviceQName = wsPortMetaData.getServiceName();
        String portName = wsPortMetaData.getPortName().getLocalPart();

        AxisService axisService;
        try {
            axisService = AxisService.createClientSideAxisService(wsBinding.getWSDLDefinition(),
                    serviceQName,
                    portName,
                    new Options());
        } catch (AxisFault e) {
            BuilderConfigException bce = new BuilderConfigException("AxisFault creating external service", e);
            bce.addContextName(externalService.getName());
            throw bce;
        }

        TypeHelper typeHelper = wsBinding.getTypeHelper();
        return new ExternalWebServiceClient(configurationContext, axisService, wsPortMetaData, typeHelper);
    }

}
