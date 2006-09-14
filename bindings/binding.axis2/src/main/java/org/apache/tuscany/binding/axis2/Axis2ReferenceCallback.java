/**
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
package org.apache.tuscany.binding.axis2;

import java.lang.reflect.InvocationTargetException;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.async.AsyncResult;
import org.apache.axis2.client.async.Callback;
import org.apache.axis2.context.MessageContext;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;

public class Axis2ReferenceCallback extends Callback {
    
    private Axis2ReferenceCallbackTargetInvoker targetInvoker;
    private SDODataBinding dataBinding;
    private boolean pureOMelement;
    
    public Axis2ReferenceCallback(Axis2ReferenceCallbackTargetInvoker targetInvoker,
            SDODataBinding dataBinding,
            boolean pureOMelement) {
        this.targetInvoker = targetInvoker;
        this.dataBinding = dataBinding;
        this.pureOMelement = pureOMelement;
    }

    public void onComplete(AsyncResult result) {
        MessageContext responseMC = result.getResponseMessageContext();
        OMElement responseOM = responseMC.getEnvelope().getBody().getFirstElement();

        Object[] os = null;
        if (responseOM != null) {
            os = dataBinding.fromOMElement(responseOM);
        }

        Object response;
        if (pureOMelement) {
            response = responseOM;
        } else {
            if (os == null || os.length < 1) {
                response = null;
            } else {
                response = os[0];
            }
        }

        try {
            targetInvoker.invokeTarget(response);
        } catch(InvocationTargetException e) {
            // FIXME what is the appropriate exception here?
            throw new RuntimeException(e);
        }
    }

    public void setComplete(boolean complete) {
        super.setComplete(complete);
    }

    public void onError(Exception e) {
    }
}
