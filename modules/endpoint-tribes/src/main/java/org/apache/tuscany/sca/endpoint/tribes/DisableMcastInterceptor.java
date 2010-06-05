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

package org.apache.tuscany.sca.endpoint.tribes;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;

/**
 * A static interceptor to disables multicast.
 * Can be removed when/if the function gets added to Tribes.
 * See Tomcat email http://markmail.org/message/doqu7pfl2hvvdfcl
 */
public class DisableMcastInterceptor extends ChannelInterceptorBase {

    public DisableMcastInterceptor() {
        super();
    }

    public void start(int svc) throws ChannelException {
        svc = (svc & (~Channel.MBR_TX_SEQ));
        super.start(svc);
    }
}
