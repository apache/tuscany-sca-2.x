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
package org.apache.tuscany.sca.itest.conversational;

import org.osoa.sca.annotations.Remotable;

/**
 * The client for the conversational itest. Is implemented by clients
 * offering both stateless and stateful callbacks
 *
 * @version $Rev: 537240 $ $Date: 2007-05-11 18:35:03 +0100 (Fri, 11 May 2007) $
 */

@Remotable
public interface ConversationalClient { 
	
	public int runConversation(); 	
	public int runConversationCallback(); 
	public int runConversationFromReference();
	public int runConversationPassingReference();
	public int runConversationError();
	public int runConversationAgeTimeout();
	public int runConversationIdleTimeout();
	public int runConversationPrincipleError();	

}
