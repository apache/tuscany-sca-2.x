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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Factory bean that returns a reference to an SCA service obtained by asking the SCA runtime for the service with the
 * given name for the given component.
 *
 * @author Adrian Colyer
 * @since 2.0
 */
public class ScaServiceProxyFactoryBean
    implements InitializingBean, FactoryBean, ApplicationContextAware, ScaAdapterAware {

    /**
     * the public interface type of the service (may be a class...)
     */
    private Class serviceType;

    /**
     * the name of the reference to look up
     */
    private String referenceName;

    /**
     * the default service name to resolve the reference too
     */
    private String defaultServiceName;

    private Object resolvedServiceReference;
    private ApplicationContext applicationContext;
    private ScaAdapter scaAdapter;

    public void setServiceType(Class serviceType) {
        this.serviceType = serviceType;
    }

    public Class getServiceType() {
        return this.serviceType;
    }

    public void setReferenceName(String name) {
        this.referenceName = name;
    }

    public String getReferenceName() {
        return this.referenceName;
    }

    public void setDefaultServiceName(String defaultService) {
        this.defaultServiceName = defaultService;
    }

    public String getDefaultServiceName() {
        return this.defaultServiceName;
    }

    /* (non-Javadoc)
    * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
    */
    public void afterPropertiesSet() throws Exception {
        if (this.serviceType == null) {
            throw new IllegalArgumentException("Required property serviceType was not set");
        }
        if (this.referenceName == null) {
            throw new IllegalArgumentException("Required property referenceName was not set");
        }
    }

    /* (non-Javadoc)
    * @see org.springframework.beans.factory.FactoryBean#getObject()
    */
    public Object getObject() throws Exception {
        if (this.resolvedServiceReference != null) {
            return this.resolvedServiceReference;
        }

        // TODO: AMC is there any merit in proxying this with a lazy target source?
        //       should the returned service ref be proxied? Only seems to add value
        //       if SCA gives us any lifecycle events we can subscribe to and take
        //       meaningful action on...
        //       See OsgiServiceProxyFactoryBean for an example of how to do the
        //       proxying if needed.
        Object scaServiceRef;
        if (this.applicationContext.getParent() == null) {
            return null;
        }

        if (!this.applicationContext.getParent().containsBean(this.referenceName)) {
            scaServiceRef = this.applicationContext.getParent().getBean(this.defaultServiceName);
        } else {
            scaServiceRef = this.applicationContext.getParent().getBean(this.referenceName);
        }
        if (!this.serviceType.isAssignableFrom(scaServiceRef.getClass())) {
            throw new IllegalStateException("...");
        }
        this.resolvedServiceReference = scaServiceRef;
        return this.resolvedServiceReference;
    }

    /* (non-Javadoc)
    * @see org.springframework.beans.factory.FactoryBean#getObjectType()
    */
    public Class getObjectType() {
        return this.serviceType;
    }

    /* (non-Javadoc)
    * @see org.springframework.beans.factory.FactoryBean#isSingleton()
    */
    public boolean isSingleton() {
        return true;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setScaAdapter(ScaAdapter adapter) {
        this.scaAdapter = adapter;
    }
}
