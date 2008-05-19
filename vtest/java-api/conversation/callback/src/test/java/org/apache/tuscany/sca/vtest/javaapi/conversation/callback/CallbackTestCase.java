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

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osoa.sca.ServiceRuntimeException;

/**
 * 
 */
public class CallbackTestCase {

    protected static AService aService = null;

    @Before
    public void init() throws Exception {
        try {
            System.out.println("Setting up");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @After
    public void destroy() throws Exception {

        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
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
        ServiceFinder.init("callback.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
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
        ServiceFinder.init("callback-local.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
        aService.testCallback();
    }

    /**
     * Lines 534, 535
     * <p>
     * Callbacks may be used for both remotable and local services. Either both
     * interfaces of a bidirectional service must be remotable, or both must be
     * local. It is illegal to mix the two.
     * <p>
     * In this test configuration BServiceCallback is remotable and CService is
     * not
     */
    @Test(expected = ServiceRuntimeException.class)
    @Ignore("TUSCANY-2291")
    public void statefulMixedCallback() throws Exception {
        System.out.println("Setting up for mixed local/remote callback tests");
        ServiceFinder.init("callback-mixed.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
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
     * <p>
     * This test is identical in structure to the stateful test except that
     * BServiceCallback is not conversational and we test that the callback is
     * NOT routed to the same instance.
     */
    @Test
    public void statelessCallback() throws Exception {
        System.out.println("Setting up for stateless callback tests");
        ServiceFinder.init("callback-stateless.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
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
     * <p>
     * Lines 747-755
     * <p>
     * The identity that is used to identify a callback request is, by default,
     * generated by the system. However, it is possible to provide an
     * application specified identity that should be used to identify the
     * callback by calling the ServiceReference.setCallbackID() method. This can
     * be used even either stateful or stateless callbacks. The identity will be
     * sent to the service provider, and the binding must guarantee that the
     * service provider will send the ID back when any callback method is
     * invoked. The callback identity has the same restrictions as the
     * conversation ID. It should either be a string or an object that can be
     * serialized into XML. Bindings determine the particular mechanisms to use
     * for transmission of the identity and these may lead to further
     * restrictions when using a given binding.
     * <p>
     * TODO - Need to add explicit test back to stateful for 747-755
     */
    @Test
    public void statelessCallback2() throws Exception {
        System.out.println("Setting up for stateless callback id tests");
        ServiceFinder.init("callback-stateless-callbackid.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
        aService.testCallback();

    }

    /**
     * Lines 650-654
     * <p>
     * The difference for stateless services is that the callback field would
     * not be available if the component is servicing a request for anything
     * other than the original client. So, the technique used in the previous
     * section, where there was a response from the backend Service which was
     * forwarded as a callback from MyService would not work because the
     * callback field would be null when the message from the backend system was
     * received.
     * <p>
     */
    @Test
    // @Ignore("TUSCANY-2306")
    public void statelessCallback3() throws Exception {
        System.out.println("Setting up for stateless callback ref null tests");
        ServiceFinder.init("callback-stateless-callbackfieldnull.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
        aService.testCallback();

    }

    /**
     * Lines 658-669
     * <p>
     * Since it is possible for a single implementation class to implement
     * multiple services, it is also possible for callbacks to be defined for
     * each of the services that it implements. The service implementation can
     * include an injected field for each of its callbacks. The runtime injects
     * the callback onto the appropriate field based on the type of the
     * callback. The following shows the declaration of two fields, each of
     * which corresponds to a particular service offered by the implementation.
     * <p>
     * Lines 670,671
     * <p>
     * If a single callback has a type that is compatible with multiple declared
     * callback fields, then all of them will be set.
     */
    @Test
    @Ignore("TUSCANY-2311")
    public void statefulMultiBidirectional() throws Exception {
        System.out.println("Setting up for multi-bidirectional interfaces tests");
        ServiceFinder.init("callback-multi.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
        aService.testCallback();
        aService.testCallback2(); // Includes test for 670,671

    }

    /**
     * Lines 675-706
     * <p>
     * In addition to injecting a reference to a callback service, it is also
     * possible to obtain a reference to a Callback instance by annotating a
     * field or method with the "@Callback" annotation. A reference implementing
     * the callback service interface may be obtained using
     * CallableReference.getService(). The following fragments come from a
     * service implementation that uses the callback API:
     * <p>
     * Alternatively a callback may be retrieved programmatically using the
     * RequestContext API. The snippet below show how to retrieve a callback in
     * a method programmatically:
     * <p>
     * Lines 695, 696
     * <p>
     * Alternatively a callback may be retrieved programmatically using the
     * RequestContext API. The snippet below show how to retrieve a callback in
     * a method programmatically:
     */
    @Test
    public void accessingCallbacks() throws Exception {
        System.out.println("Setting up for callback accessing tests");
        ServiceFinder.init("callback-accessing.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
        aService.testCallback();
        aService.testCallback2(); // Lines 695-696

    }

    /**
     * Lines 708-724
     * <p>
     * On the client side, the service that implements the callback can access
     * the callback ID (i.e. reference parameters) that was returned with the
     * callback operation also by accessing the request context, as follows:
     * <p>
     * On the client side, the object returned by the getServiceReference()
     * method represents the service reference that was used to send the
     * original request. The object returned by getCallbackID() represents the
     * identity associated with the callback, which may be a single String or
     * may be an object (as described below in “Customizing the Callback
     * Identity”).
     */
    @Test
    public void callbackId() throws Exception {
        System.out.println("Setting up for callback id tests");
        ServiceFinder.init("callback-id.composite");
        aService = ServiceFinder.getService(AService.class, "AComponent");
        aService.testCallback();

    }

    /**
     * Lines 728-732
     * <p>
     * By default, the client component of a service is assumed to be the
     * callback service for the bidirectional service. However, it is possible
     * to change the callback by using the ServiceReference.setCallback()
     * method. The object passed as the callback should implement the interface
     * defined for the callback, including any additional SCA semantics on that
     * interface such as its scope and whether or not it is remotable.
     * <p>
     * TODO - Need to complete testing of 1.6.7.5 after resolution of t-2312
     */
    @Test
    @Ignore("TUSCANY-2312")
    public void customCallback() throws Exception {
        System.out.println("Setting up for custom callback tests; create domain instance");
        ServiceFinder.init("callback-custom.composite");
        System.out.println("Setting up for custom callback tests; get AService handle");
        aService = ServiceFinder.getService(AService.class, "AComponent");
        aService.testCallback();

    }
}
