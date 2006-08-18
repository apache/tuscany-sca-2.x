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
 *
 * Created on 10-Apr-2006 by Adrian Colyer
 */
package org.springframework.sca;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Factory bean that returns a reference to an SCA property
 * obtained by asking the SCA runtime for the property with
 * the given name for the given component.
 * 
 * @author Adrian Colyer
 * @since 2.0
 */
public class ScaPropertyProxyFactoryBean implements InitializingBean, FactoryBean {

	/** the type of the property */
	private Class propertyType;
	
	/** the name of the property to look up */
	private String propertyName;
	
	/** 
	 * the SCA component we should present ourselves as when asking for
	 * a service reference
	 */
	private ScaComposite scaComposite;
	
	private Object resolvedPropertyVal;
	
	public void setPropertyType(Class serviceType) {
		this.propertyType = serviceType;
	}

	public Class getPropertyType() {
		return this.propertyType;
	}
	
	public void setPropertyName(String name) {
		this.propertyName = name;
	}
	
	public String getPropertyName() {
		return this.propertyName;
	}
	
	public void setScaComposite(ScaComposite scaComposite) {
		this.scaComposite = scaComposite;
	}
	
	public ScaComposite getScaComposite() {
		return this.scaComposite;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (this.propertyType == null) {
			throw new IllegalArgumentException("Required property serviceType was not set");
		}
		if (this.scaComposite == null) {
			throw new IllegalArgumentException("Required property scaComposite was not set");
		}
		if (this.propertyName == null) {
			throw new IllegalArgumentException("Required property referenceName was not set");
		}		
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public Object getObject() throws Exception {
		if (this.resolvedPropertyVal != null) {
			return this.resolvedPropertyVal;
		}
		
		String moduleName = this.scaComposite.getComponent();
		// TODO: AMC is there any merit in proxying this with a lazy target source?
		Object propertyVal = this.scaComposite.getScaAdapter().getPropertyReference(this.propertyName, this.propertyType, moduleName);
		if (!this.propertyType.isAssignableFrom(propertyVal.getClass())) {
			throw new IllegalStateException("Property value '" + propertyVal.toString() + "'" + 
					" of type '" + propertyVal.getClass().getName() + "' " + 
					" is not of expected type '" + this.propertyType.getName() + "'");
		}
		this.resolvedPropertyVal = propertyVal;
		return this.resolvedPropertyVal;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class getObjectType() {
		return this.propertyType;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}
	
}
