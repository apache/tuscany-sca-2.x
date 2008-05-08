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

package org.apache.tuscany.sca.vtest.javaapi.conversation.callback;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osoa.sca.ServiceRuntimeException;

/**
 * 
 */
public class CallbackTestCase {

    protected static SCADomain domain;
    protected static String compositeName = "callback-remote.composite";
    protected static AService aService = null;
    protected static BService bService = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {

        System.out.println("Cleaning up");
        if (domain != null)
            domain.close();

    }

    /**
     * Lines 529-610
     * <p>
     * A callback service is a service that is used for asynchronous
     * communication from a service provider back to its client in contrast to
     * the communication through return values from synchronous operations.
     * Callbacks are used by bidirectional services, which are services that
     * have two interfaces: • an interface for the provided service • a callback
     * interface that must be provided by the client
     * <p>
     * Callbacks may be used for both remotable and local services. Either both
     * interfaces of a bidirectional service must be remotable, or both must be
     * local. It is illegal to mix the two. There are two basic forms of
     * callbacks: stateless callbacks and stateful callbacks. A callback
     * interface is declared by using the "@Callback" annotation on a remotable
     * service interface, which takes the Java Class object of the interface as
     * a parameter. The annotation may also be applied to a method or to a field
     * of an implementation, which is used in order to have a callback injected,
     * as explained in the next section.
     * <p>
     * 1.6.7.1. Stateful Callbacks A stateful callback represents a specific
     * implementation instance of the component that is the client of the
     * service. The interface of a stateful callback should be marked as
     * conversational. The following example interfaces define an interaction
     * over stateful callback.
     * <p>
     * An implementation of the service in this example could use the
     * "@Callback" annotation to request that a stateful callback be injected.
     * The following is a fragment of an implementation of the example service.
     * In this example, the request is passed on to some other component, so
     * that the example service acts essentially as an intermediary. Because the
     * service is conversation scoped, the callback will still be available when
     * the backend service sends back its asynchronous response.
     * <p>
     * This fragment must come from an implementation that offers two services,
     * one that it offers to it clients (MyService) and one that is used for
     * receiving callbacks from the back end (MyServiceCallback). The client of
     * this service would also implement the methods defined in
     * MyServiceCallback.
     * <p>
     * Stateful callbacks support some of the same use cases as are supported by
     * the ability to pass service references as parameters. The primary
     * difference is that stateful callbacks do not require any additional
     * parameters be passed with service operations. This can be a great
     * convenience. If the service has many operations and any of those
     * operations could be the first operation of the conversation, it would be
     * unwieldy to have to take a callback parameter as part of every operation,
     * just in case it is the first operation of the conversation. It is also
     * more natural than requiring the application developers to invoke an
     * explicit operation whose only purpose is to pass the callback object that
     * should be used.
     * <p>
     * This tests the *remote* bidirectional interfaces option
     */
    @Test
    public void statefulCallback() throws Exception {
        System.out.println("Setting up for callback tests");
        domain = SCADomain.newInstance("callback.composite");
        aService = domain.getService(AService.class, "AComponent");
        aService.testCallback();
    }

    /**
     * Lines 529-610
     * <p>
     * A callback service is a service that is used for asynchronous
     * communication from a service provider back to its client in contrast to
     * the communication through return values from synchronous operations.
     * Callbacks are used by bidirectional services, which are services that
     * have two interfaces: • an interface for the provided service • a callback
     * interface that must be provided by the client
     * <p>
     * Callbacks may be used for both remotable and local services. Either both
     * interfaces of a bidirectional service must be remotable, or both must be
     * local. It is illegal to mix the two. There are two basic forms of
     * callbacks: stateless callbacks and stateful callbacks. A callback
     * interface is declared by using the "@Callback" annotation on a remotable
     * service interface, which takes the Java Class object of the interface as
     * a parameter. The annotation may also be applied to a method or to a field
     * of an implementation, which is used in order to have a callback injected,
     * as explained in the next section.
     * <p>
     * 1.6.7.1. Stateful Callbacks A stateful callback represents a specific
     * implementation instance of the component that is the client of the
     * service. The interface of a stateful callback should be marked as
     * conversational. The following example interfaces define an interaction
     * over stateful callback.
     * <p>
     * An implementation of the service in this example could use the
     * "@Callback" annotation to request that a stateful callback be injected.
     * The following is a fragment of an implementation of the example service.
     * In this example, the request is passed on to some other component, so
     * that the example service acts essentially as an intermediary. Because the
     * service is conversation scoped, the callback will still be available when
     * the backend service sends back its asynchronous response.
     * <p>
     * This fragment must come from an implementation that offers two services,
     * one that it offers to it clients (MyService) and one that is used for
     * receiving callbacks from the back end (MyServiceCallback). The client of
     * this service would also implement the methods defined in
     * MyServiceCallback.
     * <p>
     * Stateful callbacks support some of the same use cases as are supported by
     * the ability to pass service references as parameters. The primary
     * difference is that stateful callbacks do not require any additional
     * parameters be passed with service operations. This can be a great
     * convenience. If the service has many operations and any of those
     * operations could be the first operation of the conversation, it would be
     * unwieldy to have to take a callback parameter as part of every operation,
     * just in case it is the first operation of the conversation. It is also
     * more natural than requiring the application developers to invoke an
     * explicit operation whose only purpose is to pass the callback object that
     * should be used.
     * <p>
     * This tests the *local* bidirectional interfaces option
     */
    @Test
    public void localstatefulCallback() throws Exception {
        System.out.println("Setting up for local callback tests");
        domain = SCADomain.newInstance("callback-local.composite");
        aService = domain.getService(AService.class, "AComponent");
        aService.testCallback();
    }

    /**
     * Lines 534, 535
     * <p>
     * Callbacks may be used for both remotable and local services. Either both
     * interfaces of a bidirectional service must be remotable, or both must be
     * local. It is illegal to mix the two.
     */
    @Test(expected = ServiceRuntimeException.class)
    @Ignore("TUSCANY-2291")
    public void statefulMixedCallback() throws Exception {
        System.out.println("Setting up for mixed local/remote callback tests");
        domain = SCADomain.newInstance("callback-mixed.composite");
        aService = domain.getService(AService.class, "AComponent");
        aService.testCallback();
    }

    /**
     * Lines 613-615
     * <p>
     * A stateless callback interface is a callback whose interface is not
     * marked as conversational. Unlike stateless services, the client of that
     * uses stateless callbacks will not have callback methods routed to an
     * instance of the client that contains any state that is relevant to the
     * conversation.
     */
    @Test
    public void statelessCallback() throws Exception {
        System.out.println("Setting up for stateless callback tests");
        domain = SCADomain.newInstance("callback-stateless.composite");
        aService = domain.getService(AService.class, "AComponent");
        aService.testCallback();
    }

    /**
     * Lines 616-621
     * <p>
     * The only information that the client has to work with (other than the
     * parameters of the callback method) is a callback ID object that is passed
     * with requests to the service and is guaranteed to be returned with any
     * callback.
     * <p>
     * The following is a repeat of the client code fragment above, but with the
     * assumption that in this case the MyServiceCallback is stateless. The
     * client in this case needs to set the callback ID before invoking the
     * service and then needs to get the callback ID when the response is
     * received.
     */
    @Test
    public void statelessCallback2() throws Exception {
        System.out.println("Setting up for stateless callback tests");
        domain = SCADomain.newInstance("callback-stateless-callbackid.composite");
        aService = domain.getService(AService.class, "AComponent");
        aService.testCallback();

    }

}
