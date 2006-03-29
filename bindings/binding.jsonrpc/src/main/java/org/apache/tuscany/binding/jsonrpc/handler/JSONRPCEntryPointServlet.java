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

package org.apache.tuscany.binding.jsonrpc.handler;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tuscany.binding.jsonrpc.assembly.JSONRPCBinding;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;

import com.metaparadigm.jsonrpc.JSONRPCBridge;
import com.metaparadigm.jsonrpc.JSONRPCServlet;

/**
 * @version $Rev: 383148 $ $Date: 2006-03-04 08:07:17 -0800 (Sat, 04 Mar 2006) $
 */
public class JSONRPCEntryPointServlet extends JSONRPCServlet {

    private static final long serialVersionUID = 1L;

    private static final String ENTRYPOINT_CONFIG = JSONRPCEntryPointServlet.class.getName() + ".EntryPoints";

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ClassCastException {

        HttpSession session = request.getSession();
        JSONRPCBridge json_bridge = (JSONRPCBridge) session.getAttribute("JSONRPCBridge");
        if (json_bridge == null) {
            json_bridge = createJSONRPCBridge(session.getServletContext());
            session.setAttribute("JSONRPCBridge", json_bridge);
        }
        super.service(request, response);
    }

    private JSONRPCBridge createJSONRPCBridge(ServletContext servletContext) {

        JSONRPCBridge json_bridge = new JSONRPCBridge();

        Map<String, Object> entryPoints = (Map<String, Object>) servletContext.getAttribute(ENTRYPOINT_CONFIG);
        for (Iterator i = entryPoints.keySet().iterator(); i.hasNext();) {
            String entryPointName = (String) i.next();
            Object target = entryPoints.get(entryPointName);
            json_bridge.registerObject(entryPointName, target);
        }

        return json_bridge;
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader mycl = getClass().getClassLoader();
        try {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(mycl);
            }

            initTuscany(config);

        } finally {
            if (tccl != mycl) {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }
    }

    private void initTuscany(ServletConfig config) {

        ServletContext servletContext = config.getServletContext();
        AggregateContext moduleContext = (AggregateContext) servletContext.getAttribute("org.apache.tuscany.core.webapp.ModuleComponentContext");
        Module module = (Module) moduleContext.getAggregate();

        Map<String, Object> entryPoints = new HashMap<String, Object>();
        for (EntryPoint entryPoint : module.getEntryPoints()) {
            if (hasJSONRPCBinding(entryPoint)) {
                String entryPointName = entryPoint.getName();
                Object proxy = createProxy(moduleContext, entryPoint, entryPointName);
                entryPoints.put(entryPointName, proxy);
            }
        }

        servletContext.setAttribute(ENTRYPOINT_CONFIG, entryPoints);
    }

    /**
     * Tests if the EntryPoint has a JSONRPCBinding
     */
    private boolean hasJSONRPCBinding(EntryPoint entryPoint) {
        for (Binding binding : entryPoint.getBindings()) {
            if (binding instanceof JSONRPCBinding) {
                return true;
            }
        }
        return false;
    }

    private Object createProxy(AggregateContext moduleContext, EntryPoint entryPoint, String entryPointName) {
        final EntryPointContext entryPointContext = (EntryPointContext) moduleContext.getContext(entryPointName);
        InvocationHandler ih = new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                Object target = entryPointContext.getImplementationInstance();
                if (target instanceof InvocationHandler) {
                    return ((InvocationHandler) target).invoke(this, method, args);
                } else {
                    return method.invoke(target, args);
                }
            }
        };
        Class iFace = entryPoint.getConfiguredReference().getPort().getServiceContract().getInterface();
        ClassLoader cl = iFace.getClassLoader();
        Object proxy = Proxy.newProxyInstance(cl, new Class[] { iFace }, ih);
        return proxy;
    }

}
