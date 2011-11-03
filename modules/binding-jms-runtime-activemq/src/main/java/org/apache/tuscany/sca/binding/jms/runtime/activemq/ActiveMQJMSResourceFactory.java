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

package org.apache.tuscany.sca.binding.jms.runtime.activemq;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.jndi.ActiveMQInitialContextFactory;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactoryImpl;
import org.apache.tuscany.sca.extensibility.ClassLoaderContext;

public class ActiveMQJMSResourceFactory extends JMSResourceFactoryImpl {

    public ActiveMQJMSResourceFactory(String connectionFactoryName,
                                      String responseConnectionFactoryName,
                                      String initialContextFactoryName,
                                      String jndiURL) {
        super(connectionFactoryName, responseConnectionFactoryName, initialContextFactoryName, jndiURL);
    }

    protected synchronized Context getInitialContext() throws NamingException {
        if (context == null) {
            Properties props = new Properties();

            if (initialContextFactoryName != null) {
                props.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryName);
            }
            if (jndiURL != null) {
                props.setProperty(Context.PROVIDER_URL, jndiURL);
            }

            initJREEnvironment(props);
            
            try {
                        // Load the JNDI InitialContext (will load the InitialContextFactory, if present)
                        context = new InitialContext(props);
                if( context == null ) {
                        throw new NamingException();
                } else if ( context.getEnvironment().get(InitialContext.INITIAL_CONTEXT_FACTORY) == null ) {
                        throw new NamingException();
                } // end if
            } catch (NamingException e ) {
                context = getInitialContextOsgi( props );
            } // end try
                // In the case where the InitialContext fails, check whether performing an OSGi based load succeeds...            

            
        }
        return context;
    } // end method getInitialContext
    
    static final String ACTIVEMQ_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";    
    private Context getInitialContextOsgi( Properties props ) throws NamingException {
        /**
         * For OSGi, need to provide access to the InitialContextFactory for the JMS provider that is going to be used.
         * 
         * The situation is that the InitialContext constructor instantiates an instance of the InitialContextFactory by
         * calling "new" using the TCCL - thus there is a need to prepare the TCCL.
         * 03/12/2010 MJE - for the present, only worry about ActiveMQ - other providers can be added later 
         * 10/12/2010 MJE - the following code attempts to get the classloader for the ActiveMQ initial context factory
         *                  it will fail if the ActiveMQ classes are not available in the runtime, but the code will still
         *                  execute (although under OSGi the new InitialContext() operation will fail to find a suitable
         *                  InitialContextFactory object...)
         */
        
        String contextFactoryName = (String)props.get(Context.INITIAL_CONTEXT_FACTORY);
        
        ClassLoader ActiveMQCl = null;
        try {
                if( contextFactoryName == null || ACTIVEMQ_FACTORY.equals(contextFactoryName) ) {
                        ActiveMQCl = ActiveMQInitialContextFactory.class.getClassLoader();
                        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, ACTIVEMQ_FACTORY);
                        if( props.getProperty(Context.PROVIDER_URL) == null ) {
                                props.setProperty(Context.PROVIDER_URL, "vm://localhost?broker.persistent=false" );
                        } // end if
                } // end if 
        } catch (Exception e) {
                // Nothing to do in this case - the ActiveMQCl classloader will simply be null
        } // end try 

        ClassLoader tccl = ClassLoaderContext.setContextClassLoader(JMSResourceFactoryImpl.class.getClassLoader(),
                        ActiveMQCl,
                        Thread.currentThread().getContextClassLoader() );
        try {
                // Load the JNDI InitialContext (will load the InitialContextFactory, if present)
                return new InitialContext(props);
        } finally {
            // Restore the TCCL if we changed it
            if( tccl != null ) Thread.currentThread().setContextClassLoader(tccl);
        } // end try

    } // end method getInitialContextOsgi
}
