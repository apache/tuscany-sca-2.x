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

import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.ode.bpel.iapi.Message;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.MessageExchange.Status;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange.CorrelationStatus;
import org.apache.ode.utils.DOMUtils;
import org.apache.ode.utils.GUID;
import org.apache.tuscany.sca.implementation.bpel.ode.EmbeddedODEServer;
import org.apache.tuscany.sca.implementation.bpel.ode.GeronimoTxFactory;
import org.apache.tuscany.sca.implementation.bpel.ode.ODEDeployment;

/**
 * Test to Deploy and Invoke a HelloWorld BPEL process using EmbeddedODEServer
 * 
 * @version $Rev$ $Date$
 */
public class EmbeddedODEServerTestCaseFIXME extends TestCase {

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

        URL deployURL = getClass().getClassLoader().getResource("deploy.xml");
        File deploymentDir = new File(deployURL.toURI().getPath()).getParentFile();
        System.out.println("Deploying : " + deploymentDir.toString());
        System.out.println(deploymentDir);
        if (odeServer.isInitialized()) {

             
            try {
                txMgr.begin();
                odeServer.deploy(new ODEDeployment(deploymentDir));
                txMgr.commit();
            } catch (Exception e) {
                txMgr.rollback();
            }
            
            // transaction one
            MyRoleMessageExchange mex = null;
            try {
                // invoke the process
                txMgr.begin();
                mex = odeServer.getBpelServer().getEngine().createMessageExchange(new GUID().toString(),
                        new QName("http://tuscany.apache.org/implementation/bpel/example/helloworld", "HelloWorld"), "hello");

                Message request = mex.createMessage(new QName("", ""));
                request.setMessage(DOMUtils.stringToDOM("<message><TestPart>Hello</TestPart></message>"));
                mex.invoke(request);
                txMgr.commit();
            } catch (Exception e) {
                txMgr.rollback();
            } 

            // - end of transaction one

            // transaction two
            try {
                txMgr.begin();
                Status status = mex.getStatus();
                System.out.println("Status" + status.name());
                CorrelationStatus cstatus = mex.getCorrelationStatus();
                System.out.println("CorrelationStatus" + cstatus.name());
                txMgr.commit();
                // end of transaction two
            } catch (Exception e) {
                txMgr.rollback();
            }
        }
    }

}
