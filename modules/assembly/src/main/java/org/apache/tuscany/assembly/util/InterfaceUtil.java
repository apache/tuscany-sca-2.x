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

import org.apache.tuscany.interfacedef.Interface;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;

/**
 * This class encapsulates utility methods to deal with Interface definitions
 *
 */
public class InterfaceUtil {
    public static boolean checkInterfaceCompatibility(InterfaceContract sourceContract,
                                                      InterfaceContract targetContract) {
        boolean isCompatible = true;
        Interface source = sourceContract.getInterface();
        Interface target = targetContract.getInterface();
        if (source != target) {
            for (Operation sourceOperation : source.getOperations()) {
                Operation targetOperation = null;
                for (Operation anOperation : target.getOperations()) {
                    if (anOperation.getName().equals(sourceOperation.getName())) {
                        targetOperation = anOperation;
                        break;
                    }
                }
                if (targetOperation == null) {
                    isCompatible = false;
                    
                } else if (!sourceOperation.equals(targetOperation)) {
//                  FIXME Work around the fact that OperationImpl.equals() returns false 
//                  in some cases when the two operations have compatible but
//                  not identical input types. Uncomment the following line after 
//                  OperationImpl gets fixed.
//                    isCompatible = false;
                }
            }
        }
        return isCompatible;
    }
}
