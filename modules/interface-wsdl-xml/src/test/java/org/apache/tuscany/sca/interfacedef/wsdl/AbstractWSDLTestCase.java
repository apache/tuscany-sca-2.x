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

package org.apache.tuscany.sca.interfacedef.wsdl;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.impl.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLDocumentProcessor;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLModelResolver;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.XSDModelResolver;

/**
 * Test case for WSDLOperation
 */
public abstract class AbstractWSDLTestCase extends TestCase {
    protected WSDLDocumentProcessor processor;
    protected ModelResolver resolver;
    protected WSDLFactory wsdlFactory;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContributionFactory contributionFactory = new DefaultContributionFactory();
        Contribution contribution = contributionFactory.createContribution();
        ModelResolverExtensionPoint modelResolvers = new DefaultModelResolverExtensionPoint();
        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        wsdlFactory = new DefaultWSDLFactory();
        factories.addFactory(wsdlFactory); 
        javax.wsdl.factory.WSDLFactory wsdl4jFactory = javax.wsdl.factory.WSDLFactory.newInstance();
        factories.addFactory(wsdlFactory);
        factories.addFactory(wsdl4jFactory);
        resolver = new ExtensibleModelResolver(contribution, modelResolvers, factories);
        contribution.setModelResolver(resolver);
        modelResolvers.addResolver(WSDLDefinition.class, WSDLModelResolver.class);
        modelResolvers.addResolver(XSDefinition.class, XSDModelResolver.class);
        
        processor = new WSDLDocumentProcessor(factories);
    }

}
