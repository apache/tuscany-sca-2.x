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

package org.apache.tuscany.sca.binding.ajax;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.spi.BindingActivator;
import org.apache.tuscany.sca.spi.ReferenceInvokerFactory;
import org.apache.tuscany.sca.spi.ServiceListener;

public class AjaxBindingActivator implements BindingActivator<AjaxBinding>{

    private ServletHost servletHost;
    
    public static QName AJAX_BINDING_QNAME = new QName(Constants.SCA10_NS, "binding.ajax");

    public AjaxBindingActivator(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    public Class<AjaxBinding> getBindingClass() {
        return AjaxBinding.class;
    }

    public QName getSCDLQName() {
        return AJAX_BINDING_QNAME;
    }

    public ReferenceInvokerFactory createInvokerFactory(RuntimeComponent rc, RuntimeComponentReference rcr, AjaxBinding binding) {
        return new AjaxInvokerFactory(rc, rcr, binding, servletHost);
    }

    public ServiceListener createServiceListener(RuntimeComponent rc, RuntimeComponentService rcs, AjaxBinding binding) {
        return new AjaxServiceListener(rc, rcs, binding, servletHost);
    }

}
