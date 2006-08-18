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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * DeploymentMetadata implementation backed by Spring BeanFactory
 * metadata
 * @author Rod Johnson
 */
public class BeanFactoryDeploymentMetadata implements BeanFactoryAware, DeploymentMetadata {
	
	private BeanFactory beanFactory;
	
	private Map<String, ServiceMetadata> serviceNameToMetadataMap = new HashMap<String, ServiceMetadata>();
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	
	/* (non-Javadoc)
	 * @see org.springframework.sca.metadata.DeploymentMetadata#getServiceMetadata(java.lang.String)
	 */
	public synchronized ServiceMetadata getServiceMetadata(String serviceName) throws NoSuchServiceException {
		ServiceMetadata sm = serviceNameToMetadataMap.get(serviceName);
		if (sm == null) {
			Class clazz = beanFactory.getType(serviceName);
			sm = new AnnotationServiceMetadata(serviceName, clazz);
			serviceNameToMetadataMap.put(serviceName, sm);
		}
		return sm;
	}
	
}
