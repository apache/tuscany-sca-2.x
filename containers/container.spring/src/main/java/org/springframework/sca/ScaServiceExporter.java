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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * Exposes a bean instance to SCA to using the given service name.
 *
 * @author Adrian Colyer
 * @since 2.0
 */
public class ScaServiceExporter implements InitializingBean, BeanFactoryAware, ScaAdapterAware {

    /**
     * the name of the service we want to advertise
     */
    private String serviceName;

    /**
     * the type the service should be published with
     */
    private Class serviceType;

    /**
     * the bean to be published
     */
    private Object target;

    /**
     * for resolving the bean name
     */
    private BeanFactory beanFactory;
    private ScaAdapter scaAdapter;

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceType(Class serviceType) {
        this.serviceType = serviceType;
    }

    public Class getServiceType() {
        return this.serviceType;
    }

    public void setTarget(Object targetBean) {
        this.target = targetBean;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void afterPropertiesSet() throws Exception {
        if (this.serviceType == null) {
            throw new IllegalArgumentException("Required property serviceType was not set");
        }
        if (this.target == null) {
            throw new IllegalArgumentException("Required property target was not set");
        }
        if (this.beanFactory == null) {
            throw new IllegalArgumentException("Required property beanFactory was not set");
        }
        if (this.serviceName == null) {
            throw new IllegalArgumentException("Required property serviceName was not set");
        }
        publishScaService();
    }

    private void publishScaService() {
        scaAdapter.publishAsService(target, serviceType, serviceName, null);
    }

    public void setScaAdapter(ScaAdapter adapter) {
        this.scaAdapter = adapter;
    }
}
