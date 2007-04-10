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
package org.apache.tuscany.assembly.util;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.Operation;

/**
 * This class encapsulates utility methods to deal with Interface definitions
 *
 */
public class InterfaceUtil {
    public static boolean checkInterfaceCompatibility(Interface source,
                                                      Interface target) {
        boolean isCompatible = true;
        if (source != target) {
            //TODO : Fix comparisons of interaction scopes.
            /*if (source.getInteractionScope() != target.getInteractionScope()) {
            throw new IncompatibleOverridingServiceContractException(
                                                           "Interaction scopes settings do not match",
                                                           source, target);
              }*/

            Operation targetOperation = null;
            for (Operation sourceOperation : source.getOperations()) {
                for (Operation anOperation : target.getOperations()) {
                    if (targetOperation.getName().equals(sourceOperation.getName())) {
                        targetOperation = anOperation;
                        break;
                    }
                }
                if (targetOperation == null) {
                    isCompatible = false;
                } else if (!sourceOperation.equals(targetOperation)) {
                    isCompatible = false;
                }
            }
        }
        return isCompatible;
    }
}
