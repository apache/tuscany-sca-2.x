/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.tomcat.integration;

import org.apache.catalina.Valve;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.startup.ContextConfig;
import org.apache.tuscany.tomcat.TuscanyHost;
import org.apache.tuscany.tomcat.TuscanyValve;
import org.apache.tuscany.tomcat.ContainerLoader;

import java.io.File;

/**
 * @version $Rev$ $Date$
 */
public class TomcatIntegrationTestCase extends AbstractTomcatTest {
    protected File app1;
    private Loader loader;

    public void testComponentIntegration() throws Exception {
        // create the webapp Context
        StandardContext ctx = new StandardContext();
        ctx.addLifecycleListener(new ContextConfig());
        ctx.setName("testContext");
        ctx.setDocBase(app1.getAbsolutePath());
        ctx.setLoader(loader);

        // define our test servlet
        StandardWrapper wrapper = new StandardWrapper();
        wrapper.setServletClass(TestServlet.class.getName());
        ctx.addChild(wrapper);

        host.addChild(ctx);
        boolean found = false;
        for (Valve valve: ctx.getPipeline().getValves()) {
            if (valve instanceof TuscanyValve) {
                found = true;
                break;
            }
        }
        assertTrue("TuscanyValve not in pipeline", found);

        request.setContext(ctx);
        request.setWrapper(wrapper);
        host.invoke(request, response);

        host.removeChild(ctx);
    }

    public void testWebServiceIntegration() throws Exception {
        // create the webapp Context
        StandardContext ctx = new StandardContext();
        ctx.addLifecycleListener(new ContextConfig());
        ctx.setName("testContext");
        ctx.setDocBase(app1.getAbsolutePath());
        ctx.setLoader(loader);

        host.addChild(ctx);

        Wrapper wrapper = (Wrapper) ctx.findChild("TuscanyAxis2EntryPointServlet");
        assertNotNull("No webservice wrapper present", wrapper);
        request.setContext(ctx);
        request.setRequestURI("/services/HelloWorldService");
        request.setWrapper(wrapper);
        request.setContentType("text/xml");
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:q0=\"http://helloworldaxis.samples.tuscany.apache.org\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "<soapenv:Body>\n" +
                "<q0:getGreetings>\n" +
                "<q0:in0>World</q0:in0>\n" +
                "</q0:getGreetings>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>\n";
        request.setStream(new MockInputStream(xml.getBytes("UTF-8")));
        host.invoke(request, response);
        xml = "<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header /><soapenv:Body><helloworldaxis:getGreetingsResponse xmlns:helloworldaxis=\"http://helloworldaxis.samples.tuscany.apache.org\">\n" +
                "  <helloworldaxis:getGreetingsReturn>Hello World</helloworldaxis:getGreetingsReturn>\n" +
                "</helloworldaxis:getGreetingsResponse></soapenv:Body></soapenv:Envelope>";
        assertEquals(xml, response.getOutputStream().toString());

        // FIXME TUSCANY-117 shouldn't the status be 200 rather than 0
//        assertEquals(200, response.getStatus());
        host.removeChild(ctx);
    }

    protected void setUp() throws Exception {
        super.setUp();
        app1 = new File(getClass().getResource("/app1").toURI());
        File baseDir = new File(app1, "../../tomcat").getCanonicalFile();
        setupTomcat(baseDir, new TuscanyHost());
        engine.start();

        TestClassLoader cl = new TestClassLoader(classes, new File(app1, "WEB-INF/classes").toURL(), getClass().getClassLoader());
        cl.start();
        loader = new ContainerLoader(cl);
    }

    protected void tearDown() throws Exception {
        engine.stop();
        super.tearDown();
    }

}
