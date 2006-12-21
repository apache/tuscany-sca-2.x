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
package org.apache.tuscany.binding.jms;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tuscany.binding.jms.databinding.ObjectMsgDataBinding;

public class SimpleJMSResourceFactory implements JMSResourceFactory {
		
	private JMSBinding jmsBinding;
	private Connection con;
	private Context context;
	private boolean isConnectionStarted;
        private JMSDataBinding jmsDataBinding;

	public  SimpleJMSResourceFactory(JMSBinding jmsBinding){
		this.jmsBinding = jmsBinding;
                this.jmsDataBinding = new ObjectMsgDataBinding();
	}
	
	/* 
	 * This is a simple implementation where a connection is created per binding
	 * Ideally the resource factory should be able to leverage the host environment
	 * to provide connection pooling if it can.
	 * 
	 * For ex If Tuscany is running inside an AppServer
	 * Then we could leverage the JMS resources it provides
	 *
	 * @see org.apache.tuscany.binding.jms.JMSResourceFactory#getConnection()
	 */
	public Connection getConnection() throws NamingException, JMSException{
		if (con == null){
			createConnection();			
		}
		return con;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tuscany.binding.jms.JMSResourceFactory#createSession()
	 */
	public Session createSession() throws JMSException, NamingException{
		return getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tuscany.binding.jms.JMSResourceFactory#startConnection()
	 */
	public void startConnection() throws JMSException, NamingException{
		if(!isConnectionStarted){
			getConnection().start();
			isConnectionStarted = true;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tuscany.binding.jms.JMSResourceFactory#closeConnection()
	 */
	public void closeConnection() throws JMSException, NamingException{
		if(con != null){	
			con.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tuscany.binding.jms.JMSResourceFactory#createTextMessage(javax.jms.Session)
	 */
	public Message createMessage(Session session, Object payload) throws JMSException {
            Message message = jmsDataBinding.toJMSMessage(session, payload);
            message.setJMSDeliveryMode(jmsBinding.getDeliveryMode());
            message.setJMSPriority(jmsBinding.getPriority());
            return message;
        }

	private void createConnection() throws NamingException, JMSException {
		if(context == null){
			createInitialContext();
		}
		ConnectionFactory conFac = (ConnectionFactory)context.lookup(jmsBinding.getConnectionFactoryName());
		con = conFac.createConnection();		
	}
	
	private void createInitialContext() throws NamingException{
		Properties props = new Properties();
    	props.setProperty(Context.INITIAL_CONTEXT_FACTORY,jmsBinding.getInitialContextFactoryName().trim());
    	props.setProperty(Context.PROVIDER_URL,jmsBinding.getJNDIProviderURL().trim());
    	
    	context = new InitialContext(props);
	}

	public Destination lookupDestination(String jndiName) throws NamingException {
                if(context == null){
                        createInitialContext();
                }
		return (Destination)context.lookup(jndiName);
	}

    public void setDataBinding(JMSDataBinding jmsDataBinding) {
        this.jmsDataBinding = jmsDataBinding;
    }

    public Object getMessagePayload(Message jmsMessage) throws JMSException {
        return jmsDataBinding.fromJMSMessage(jmsMessage);
    }

}
