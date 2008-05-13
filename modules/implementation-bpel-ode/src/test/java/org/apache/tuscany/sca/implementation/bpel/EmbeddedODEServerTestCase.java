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

import java.io.File;
import java.net.URL;
import java.util.concurrent.Future;

import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.ode.bpel.iapi.Message;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.MessageExchange.Status;
import org.apache.ode.utils.DOMUtils;
import org.apache.ode.utils.GUID;
import org.apache.tuscany.sca.implementation.bpel.ode.EmbeddedODEServer;
import org.apache.tuscany.sca.implementation.bpel.ode.GeronimoTxFactory;
import org.apache.tuscany.sca.implementation.bpel.ode.ODEDeployment;
import org.w3c.dom.Element;

/**
 * Test to Deploy and Invoke a HelloWorld BPEL process using EmbeddedODEServer
 * 
 * @version $Rev$ $Date$
 */
public class EmbeddedODEServerTestCase extends TestCase {

    private EmbeddedODEServer odeServer;

    private TransactionManager txMgr;

    @Override
    protected void setUp() throws Exception {
        GeronimoTxFactory txFactory = new GeronimoTxFactory();
        txMgr = txFactory.getTransactionManager();

        this.odeServer = new EmbeddedODEServer(txMgr);
        odeServer.init();
    }

    @Override
    protected void tearDown() throws Exception {
        odeServer.stop();
    }

    public void testProcessInvocation() throws Exception {
        if (!odeServer.isInitialized()) {
            fail("Server did not start !");
        }

        URL deployURL = getClass().getClassLoader().getResource("helloworld/deploy.xml");
        File deploymentDir = new File(deployURL.toURI().getPath()).getParentFile();
        System.out.println("Deploying : " + deploymentDir.toString());
        System.out.println(deploymentDir);
        if (odeServer.isInitialized()) {
            try {
                txMgr.begin();
                odeServer.deploy(new ODEDeployment(deploymentDir));
                txMgr.commit();
            } catch (Exception e) {
                e.printStackTrace();
                txMgr.rollback();
            }
            
            // transaction one
            MyRoleMessageExchange mex = null;
            Future onhold = null;
            try {
                // invoke the process
                txMgr.begin();
                mex = odeServer.getBpelServer().getEngine().createMessageExchange(new GUID().toString(),
                        new QName("http://tuscany.apache.org/implementation/bpel/example/helloworld.wsdl", "HelloService"), "hello");

                Message request = mex.createMessage(new QName("", ""));
                request.setMessage(DOMUtils.stringToDOM("<message><TestPart><hello xmlns=\"http://tuscany.apache.org/implementation/bpel/example/helloworld.wsdl\">Hello</hello></TestPart></message>"));
                onhold = mex.invoke(request);
                txMgr.commit();
            } catch (Exception e) {
                e.printStackTrace();
                txMgr.rollback();
            } 
            // - end of transaction one

            // Waiting until the reply is ready in case the engine needs to continue in a different thread
            if (onhold != null)
                onhold.get();

            // transaction two
            try {
                txMgr.begin();
                // Reloading the mex in the current transaction, otherwise we can't be sure we have
                // the "freshest" one.
                mex = (MyRoleMessageExchange) odeServer.getBpelServer().getEngine().getMessageExchange(mex.getMessageExchangeId());

                Status status = mex.getStatus();
                System.out.println("Status: " + status.name());
                Element response = mex.getResponse().getMessage();
                System.out.println("Response: " + DOMUtils.domToString(response));
                txMgr.commit();
                // end of transaction two
            } catch (Exception e) {
                e.printStackTrace();
                txMgr.rollback();
            }
        }
    }

}
