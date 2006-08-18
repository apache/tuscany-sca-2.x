/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.sca.metadata;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Metadata for an SCA component.
 * @author Rod Johnson
 */
public interface ServiceMetadata {
	
	/**
	 * Return the service name
	 * @return the service name of the component
	 */
	String getServiceName();
	
	/**
	 * Return the service interfaces implemented by the component
	 * @return interfaces implemented by the component
	 */
	Class<?>[] getServiceInterfaces();
	
	/**
	 * Return a list of OneWay methods
	 * @return never returns null
	 */
	List<Method> getOneWayMethods();
	
	/**
	 * Return a list of SCA injections
	 * @return a list of SCA injections. Never returns null.
	 */
	List<Injection> getInjections();

}
