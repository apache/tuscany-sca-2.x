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

package org.apache.tuscany.sca.policy.util;

import org.apache.tuscany.sca.policy.IntentAttachPointType;

/**
 * @version $Rev$ $Date$
 */
public class PolicyValidationUtils {
    public static boolean isPolicySetApplicable(String scdlFragment,
                                                String xpath,
                                                IntentAttachPointType attachPointType) {
        
        //FIXME: For now do a simple check and later implement whatever is mentioned in the next comment
       if ( xpath != null && attachPointType != null && xpath.indexOf(attachPointType.getName().getLocalPart()) != -1) {
           return true;
       } else {
           return false;
       }
        
        
        //create a xml node out of the parent object.. i.e. write the parent object as scdl fragment
        //invoke PropertyUtil.evaluate(null, node, xpath)
        //verify the result Node's QName against the bindingType's name
        
        /*if (parent instanceof ComponentReference) {
        } else if (parent instanceof ComponentReference) {
        } else if (parent instanceof Component) {
        } else if (parent instanceof CompositeService) {
        } else if (parent instanceof CompositeReference) {

        }
        return true;*/
    }
}
