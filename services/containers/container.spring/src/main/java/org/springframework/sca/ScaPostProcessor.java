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

package org.springframework.sca;

import java.beans.PropertyDescriptor;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.sca.intercept.OneWayAdvisor;
import org.springframework.sca.metadata.DeploymentMetadata;
import org.springframework.sca.metadata.Injection;
import org.springframework.sca.metadata.NoSuchServiceException;
import org.springframework.sca.metadata.ServiceMetadata;

/**
 * Spring bean post processor that looks up service metadata by name for each bean definition and performs SCA
 * injection.
 * <p/>
 * Also performs proxying for OneWay.
 *
 * @author Rod Johnson
 */
public class ScaPostProcessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware {

    private DeploymentMetadata deploymentMetadata;

    private ApplicationContext applicationContext;

    private TaskExecutor taskExecutor;

    //private ScaAdapter scaAdapter;


    /**
     * @param taskExecutor The taskExecutor to set.
     */
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    // TODO would process side files when container starts up

    public void setDeploymentMetadata(DeploymentMetadata deploymentMetadata) {
        this.deploymentMetadata = deploymentMetadata;
    }

    /**
     * @param scaAdapter the ScaAdapter for use to export services if necessary
     */
    public void setScaAdapter(ScaAdapter scaAdapter) {
        //this.scaAdapter = scaAdapter;
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Object postProcessBeforeInstantiation(Class beanClass,
                                                 String beanName) throws BeansException {
        return null;
    }

    public boolean postProcessAfterInstantiation(Object bean, String beanName)
        throws BeansException {
        try {
            ServiceMetadata smd = deploymentMetadata.getServiceMetadata(beanName);
            doScaInjection(bean, smd);
        } catch (NoSuchServiceException ex) {
            //
        }
        return true;
    }

    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
                                                    String beanName) throws BeansException {
        return pvs;
    }

    public PropertyValues postProcessPropertyValues(PropertyValues propertyValues, Object object, String string)
        throws BeansException {
        return propertyValues;
    }

    protected void doScaInjection(Object bean, ServiceMetadata smd) {
        for (Injection injection : smd.getInjections()) {
            injection.apply(applicationContext, bean);
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
        try {
            ServiceMetadata smd = deploymentMetadata.getServiceMetadata(beanName);
            return createScaProxy(bean, smd);
        } catch (NoSuchServiceException ex) {
            return bean;
        }

        // TODO validate required injections here or earlier

        // TODO publish if necessary, using adapter
    }

    protected Object createScaProxy(Object bean, ServiceMetadata smd) {
        ProxyFactory pf = new ProxyFactory(bean);
        for (Class intf : smd.getServiceInterfaces()) {
            pf.addInterface(intf);
        }

//pf.addAdvisor(ExposeInvocationInterceptor.ADVISOR);
//pf.addAdvisor(new ExposeBeanNameAdvisor(smd.getServiceName()));
        // TODO enforce call by value

        if (!smd.getOneWayMethods().isEmpty()) {
            pf.addAdvisor(new OneWayAdvisor(smd, this.taskExecutor));
        }

        return pf.getProxy();
    }


}
