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

package org.apache.tuscany.sca.http.osgi;

import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A whiteboard for HttpService and Servlet
 */
public class HttpServiceWrapper {

    static final String SERVLET_URI = "servlet.uri";
    private ServiceTracker httpTracker;
    private BundleContext bc;

    private class HttpServiceTracker extends ServiceTracker {
        private HttpServiceTracker() {
            super(bc, HttpService.class.getName(), null);
        }

        @Override
        public Object addingService(ServiceReference reference) {
            HttpService httpService = (HttpService)super.addingService(reference);
            // Register existing servlets
            String filter = "(objectclass=" + Servlet.class.getName() + ")";
            ServiceReference[] servlets = null;
            try {
                servlets = bc.getServiceReferences(null, filter);
            } catch (InvalidSyntaxException e) {
                throw new IllegalStateException(e);
            }
            for (int i = 0; servlets != null && i < servlets.length; i++) {
                Servlet servlet = (Servlet)bc.getService(servlets[i]);
                String alias = (String)servlets[i].getProperty(SERVLET_URI);

                try {
                    httpService.registerServlet(alias, servlet, null, null);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return httpService;
        }
    }

    public HttpServiceWrapper(BundleContext bc) {
        super();
        this.bc = bc;
    }

    void open() {
        httpTracker = new HttpServiceTracker();
        httpTracker.open();

        ServiceListener sl = new ServiceListener() {
            public void serviceChanged(ServiceEvent ev) {
                ServiceReference sr = ev.getServiceReference();
                switch (ev.getType()) {
                    case ServiceEvent.REGISTERED: {
                        registerServlet(sr);
                    }
                        break;
                    case ServiceEvent.UNREGISTERING: {
                        unregisterServlet(sr);
                    }
                        break;
                }
            }
        };

        String filter = "(objectclass=" + Servlet.class.getName() + ")";
        try {
            bc.addServiceListener(sl, filter);
            ServiceReference[] servlets = bc.getServiceReferences(null, filter);
            for (int i = 0; servlets != null && i < servlets.length; i++) {
                sl.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, servlets[i]));
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
    }

    void close() {
        if (httpTracker != null) {
            httpTracker.close();
        }
    }

    void registerServlet(ServiceReference sr) {
        Servlet servlet = (Servlet)bc.getService(sr);
        String alias = (String)sr.getProperty(SERVLET_URI);

        Object[] httpServices = httpTracker.getServices();

        for (int i = 0; httpServices != null && i < httpServices.length; i++) {
            HttpService http = (HttpService)httpServices[i];
            try {
                http.registerServlet(alias, servlet, null, null);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    void unregisterServlet(ServiceReference sr) {
        String alias = (String)sr.getProperty(SERVLET_URI);
        Object[] httpServices = httpTracker.getServices();

        for (int i = 0; httpServices != null && i < httpServices.length; i++) {
            HttpService http = (HttpService)httpServices[i];
            try {
                http.unregister(alias);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            bc.ungetService(sr);
        }
    }

}
