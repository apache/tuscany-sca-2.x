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

package org.apache.tuscany.sca.binding.dwr;

import java.util.Collection;

import org.apache.tuscany.sca.core.invocation.MessageImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;

public class DWRInvoker implements Invoker {

    private String referenceFunction;
    
    public DWRInvoker(String referenceName, Operation operation) {
        this.referenceFunction = referenceName + "." + operation.getName();
    }

    public Message invoke(Message requestMsg) {

        invoke((Object[])requestMsg.getBody());

        // DWR references can not return anything 
        return new MessageImpl();
    }

    public void invoke(Object[] args) {

        // TODO: this only works if its the same thread as request
        WebContext wctx = WebContextFactory.get();
        String currentPage = wctx.getCurrentPage();

        // Get a DWR Util proxy for all the browsers on the current page:
        Collection sessions = wctx.getScriptSessionsByPage(currentPage);
        Util utilAll = new Util(sessions);

        ScriptBuffer referenceInvoke = getInvokeFragment(args, wctx);

        // add the reference call to the Util proxy which will cause DWR to 
        // asynchronously send it to be run on each active browser client
        utilAll.addScript(referenceInvoke);
    }

    /**
     * Creates a fragment of JavaScript code to invoke the reference function
     * Eg: "<referenceName>.<operationName>(arg1, arg2,...);"
     */
    protected ScriptBuffer getInvokeFragment(Object[] args, WebContext wctx) {

        ScriptBuffer sb = new ScriptBuffer();
        sb.appendScript(referenceFunction);
        sb.appendScript("(");
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                sb.appendData(args[i]);
                if (i < (args.length - 1)) {
                    sb.appendScript(", ");
                }
            }
        }
        sb.appendScript(");");

        return sb;
    }

}
