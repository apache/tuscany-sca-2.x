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
package conversation.referenceclient;

import org.osoa.sca.CallableReference;
import org.osoa.sca.annotations.AllowsPassByReference;
import org.osoa.sca.annotations.Service;

import conversation.service.ConversationalService;

/**
 * A client component that accepts a reference to an ongoing conversation
 * and takes part in that conversation
 *
 * @version $Rev$ $Date$
 */

@Service(interfaces={ConversationalReferenceClient.class})
@AllowsPassByReference
public class ConversationalReferenceClientImpl implements ConversationalReferenceClient{
   
    public void incrementCount(CallableReference<ConversationalService> conversationalService){
        ConversationalService callableReference = conversationalService.getService();
        
        callableReference.incrementCount();        
    }


}
