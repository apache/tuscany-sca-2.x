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
package org.apache.tuscany.sca.itest.servicereference;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tuscany.sca.itest.servicereference.utils.ServiceReferenceUtils;
import org.junit.Assert;
import org.osoa.sca.CallableReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Service;

/**
 * Simple Stateless Service implementation
 * 
 * @version $Date$ $Revision$
 */
@Service(StatelessService.class)
public class StatelessServiceImpl implements StatelessService {

    /**
     * Injected reference to the call back
     */
    @Callback
    protected CallableReference<StatelessServiceCallback> theCallbackRef;

    /**
     * Constructor
     */
    public StatelessServiceImpl() {
    }

    /**
     * Gets the current time
     * 
     * @return The current time
     */
    public String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        return sdf.format(new Date());
    }

    /**
     * Method that triggers the callback
     * 
     * @param msg A message to pass with the callback
     * @throws Exception Test failed
     */
    public void triggerCallback(String msg) throws Exception {
        Assert.assertNotNull(theCallbackRef);

        // Serialize the CallableReference
        byte[] serializedCR = ServiceReferenceUtils.serialize(theCallbackRef);
        Assert.assertNotNull(serializedCR);

        // Deserlaize the CallableReference
        CallableReference<?> cr = ServiceReferenceUtils.deserializeCallableReference(serializedCR);
        Assert.assertNotNull(cr);
        CallableReference<StatelessServiceCallback> regotCallbackRef 
            = (CallableReference<StatelessServiceCallback>) cr;

        // Use the deseralized CallbackReference
        regotCallbackRef.getService().callback(msg);
    }
}
