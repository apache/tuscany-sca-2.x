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
package test;

import org.oasisopen.sca.annotation.*;

/**
 * Enhanced Java component implementation for business interface Service1,
 * where the implementation also has a single reference using the Service1
 * interface with multiplicity 0..1 which gets called when 
 * operation1 is invoked, if present
 * @author MikeEdwards
 *
 */
@Service(Service1.class)
public class service1Impl6 implements Service1 {
	
	@Property
	public String serviceName = "service1";
	// Required = false -> multiplicity 0..1
	@Reference(required=false)
	public Service1 reference1 = null;

	public String operation1(String input) {
		String result = serviceName + " operation1 invoked";
		// Call the reference if present
		if( reference1 != null ) result = result.concat(reference1.operation1( input ));
		return result;
	}

}
