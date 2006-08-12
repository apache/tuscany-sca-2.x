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
package org.apache.servicemix.sca;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.client.DefaultServiceMixClient;
import org.apache.servicemix.client.ServiceMixClient;
import org.apache.servicemix.components.util.MockServiceComponent;
import org.apache.servicemix.jbi.container.ActivationSpec;
import org.apache.servicemix.jbi.container.JBIContainer;
import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.servicemix.jbi.jaxp.StringSource;
import org.apache.servicemix.jbi.resolver.ServiceNameEndpointResolver;
import org.apache.servicemix.sca.bigbank.stockquote.StockQuoteResponse;
import org.w3c.dom.Node;

public class ScaComponentTest extends TestCase {

    private static Log log =  LogFactory.getLog(ScaComponentTest.class);
    
    protected JBIContainer container;
    
    protected void setUp() throws Exception {
        container = new JBIContainer();
        container.setUseMBeanServer(false);
        container.setCreateMBeanServer(false);
        container.setMonitorInstallationDirectory(false);
        container.setNamingContext(new InitialContext());
        container.setEmbedded(true);
        container.init();
    }
    
    protected void tearDown() throws Exception {
        if (container != null) {
            container.shutDown();
        }
    }
    
    public void testDeploy() throws Exception {
        ScaComponent component = new ScaComponent();
        container.activateComponent(component, "JSR181Component");

        MockServiceComponent mock = new MockServiceComponent();
        mock.setService(new QName("http://www.quickstockquote.com", "StockQuoteService"));
        mock.setEndpoint("StockQuoteServiceJBI");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StockQuoteResponse r = new StockQuoteResponse();
        r.setResult(8.23f);
        JAXBContext.newInstance(StockQuoteResponse.class).createMarshaller().marshal(r, baos);
        mock.setResponseXml(baos.toString());
        ActivationSpec as = new ActivationSpec();
        as.setComponent(mock);
        container.activateComponent(as);
        
        // Start container
        container.start();
        
        // Deploy SU
        component.getServiceUnitManager().deploy("su", getServiceUnitPath("org/apache/servicemix/sca/bigbank"));
        component.getServiceUnitManager().init("su", getServiceUnitPath("org/apache/servicemix/sca/bigbank"));
        component.getServiceUnitManager().start("su");
        
        ServiceMixClient client = new DefaultServiceMixClient(container);
        Source req = new StringSource("<AccountReportRequest><CustomerID>id</CustomerID></AccountReportRequest>");
        Object rep = client.request(new ServiceNameEndpointResolver(
        										new QName("http://sca.servicemix.apache.org/Bigbank/Account", "AccountService")),
        			   						 null, null, req);
        if (rep instanceof Node) {
            rep = new DOMSource((Node) rep);
        }
        log.info(new SourceTransformer().toString((Source) rep));
    }
     
    protected String getServiceUnitPath(String name) {
        URL url = getClass().getClassLoader().getResource(name + "/sca.module");
        File path = new File(url.getFile());
        path = path.getParentFile();
        return path.getAbsolutePath();
    }
    
}
