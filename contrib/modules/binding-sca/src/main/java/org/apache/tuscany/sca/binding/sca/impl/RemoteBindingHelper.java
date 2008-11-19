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

package org.apache.tuscany.sca.binding.sca.impl;

/**
 * TODO: TUSCANY-2578, implement a pluggable mechanism so sca binding impls can 
 *               add their own code to the decision on whether or not to use
 *               the remote binding provider.
 */
public class RemoteBindingHelper {
    
    private static boolean alwaysRemote;
    static {
        try {
            Class.forName("org.apache.tuscany.sca.binding.sca.jms.JMSSCABindingProviderFactory");
            Class.forName("javax.jms.IllegalStateException");
            alwaysRemote = true;
        } catch (ClassNotFoundException e) {
            alwaysRemote = false;
        }
    }
    
    public static boolean isTargetRemote() {
        return alwaysRemote;
    }

}
