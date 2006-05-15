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
package org.apache.tuscany.binding.jsonrpc.mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.tuscany.binding.jsonrpc.assembly.JSONRPCBinding;
import org.apache.tuscany.binding.jsonrpc.mocks.servlet.MockServletConfig;
import org.apache.tuscany.binding.jsonrpc.mocks.tuscany.MockBinding;
import org.apache.tuscany.binding.jsonrpc.mocks.tuscany.MockCompositeContextImpl;
import org.apache.tuscany.binding.jsonrpc.mocks.tuscany.MockEntryPointContext;
import org.apache.tuscany.binding.jsonrpc.mocks.tuscany.MockScopeContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.webapp.TuscanyServletListener;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.impl.EntryPointImpl;

public class MockConfigUtils {

    public static ServletConfig createMockServletConfig(String entryPointName, Object instance) {
        ServletConfig servletConfig = new MockServletConfig();
        ServletContext context = servletConfig.getServletContext();
        context.setAttribute(TuscanyServletListener.MODULE_COMPONENT_NAME, createModuleWithJSONRPCEntryPoint(entryPointName, instance));
        return servletConfig;
    }

    public static CompositeContext createModuleWithJSONRPCEntryPoint(String entryPointName, Object instance) {
        MockCompositeContextImpl cci = new MockCompositeContextImpl();
        Module module = (Module) cci.getComposite();
        List<EntryPoint> entryPoints = module.getEntryPoints();
        EntryPoint ep = createMockEntryPoint(entryPointName);
        addJSONRPCBinding(ep);
        entryPoints.add(ep);
        cci.start();

        Map<String, Context> ics = new HashMap<String, Context>();
        ics.put(entryPointName, new MockEntryPointContext(instance));

        ScopeContext sc = new MockScopeContext(ics);
        sc.start();

        Map<String, ScopeContext> scopeIndex = cci.getScopeIndex();
        scopeIndex.put(entryPointName, sc);

        return cci;
    }

    public static EntryPoint createMockEntryPoint(String name) {
        EntryPoint entryPoint = new EntryPointImpl() {
        };
        entryPoint.setName(name);
        return entryPoint;
    }

    public static void addJSONRPCBinding(EntryPoint entryPoint) {
        List<Binding> bindings = entryPoint.getBindings();
        bindings.add(new JSONRPCBinding());
    }

    public static void addNonJSONRPCBinding(EntryPoint entryPoint) {
        List<Binding> bindings = entryPoint.getBindings();
        bindings.add(new MockBinding());
    }

}
