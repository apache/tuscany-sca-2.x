package org.apache.tuscany.sca.implementation.spring.runtime.context;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class SCAGenericApplicationContext extends GenericApplicationContext {

	 ClassLoader classloader = null;
	 
	 public SCAGenericApplicationContext(DefaultListableBeanFactory beanFactory, 
             							 ApplicationContext parent,
             							 ClassLoader classloader) {
		 super(beanFactory, parent);
		 this.classloader = classloader;
	 }
	 
	 public SCAGenericApplicationContext(ApplicationContext parent,
				 						 ClassLoader classloader) {
		 super(parent);
		 this.classloader = classloader;
	 }

	 @Override
	 protected void postProcessBeanFactory (ConfigurableListableBeanFactory beanFactory) {
		 beanFactory.setBeanClassLoader(classloader);
	 }
}
