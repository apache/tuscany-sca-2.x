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

package org.apache.tuscany.sca.core.invocation;

import org.oasisopen.sca.annotation.OneWay;
import org.oasisopen.sca.annotation.Remotable;

/**
 * An interface which describes a general response pattern for the asynchronous invocation of a service
 *
 * @param <V> - the type of the non-fault response
 */
public interface AsyncResponseHandler<V> extends AsyncResponseService<V> {
	
	/**
	 * Async process completed with a wrapped Fault.  Must only be invoked once
	 * <inherited>
	 * @param e - the wrapper containing the Fault to send
	 * @throws IllegalStateException if either the setResponse method or the setFault method have been called previously
	 */
	@OneWay
	public void setWrappedFault(AsyncFaultWrapper w);
	
	/**
	 * Async process completed with a Fault.  Must only be invoked once.
	 * @param e - the Fault to send
	 * @throws IllegalStateException if either the setResponse method or the setFault method have been called previously
	 */
	@OneWay
	public void setFault( Throwable e );
	
	/**
	 * Async process completed with a response message.  Must only be invoked once
	 * <inherited>
	 * @throws IllegalStateException if either the setResponse method or the setFault method have been called previously
	 * @param res - the response message, which is of type V
	 */
	@OneWay
	public void setResponse(V res);

}
