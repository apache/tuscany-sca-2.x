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
package org.apache.tuscany.bean.context;

/**
 * Dummy bean used as a placeholder for composite references for now.
 *
 *  @version $Rev$ $Date$
 */
public class DummyCompositeReferenceBean {
	
	private String name;
	private String interfaze;
	private Object promote;

	public String getInterface() {
		return interfaze;
	}

	public void setInterface(String interfaze) {
		this.interfaze = interfaze;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Object getPromote() {
		return promote;
	}
	
	public void setPromote(Object promote) {
		this.promote = promote;
	}
	
}
