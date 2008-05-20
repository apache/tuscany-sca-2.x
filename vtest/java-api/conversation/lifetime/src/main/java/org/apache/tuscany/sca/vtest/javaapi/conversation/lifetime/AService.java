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
package org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime;

import org.osoa.sca.annotations.Remotable;

/**
 * Simple Remotable Service
 */
@Remotable
public interface AService {

    public void testConversationStarted();

    public void testConversationStarted2();

    public void testConversationContinue();

    public void testConversationContinue2();

    public void testConversationContinue3();

    public void testConversationEnd();

    public void testConversationEnd2();

    public void testConversationEnd3();

    public void testConversationEnd4();

    public void testConversationEnd5();

    public void testConversationEnd6();

    public void testConversationEnd7();

    public void testConversationEnd8();
    
    public void testConversationEnd9();    
}
