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
package org.apache.tuscany.binding.jms.databinding;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.tuscany.binding.jms.JMSDataBinding;
import org.apache.tuscany.binding.jms.JMSBindingRuntimeException;

public class ObjectMsgDataBinding implements JMSDataBinding {

    /* (non-Javadoc)
     * @see org.apache.tuscany.binding.jms.databinding.DataBinding#fromJMSMessage(javax.jms.Message)
     */
    public Object fromJMSMessage(Message msg) throws JMSException {

        Object o = ((ObjectMessage)msg).getObject();

        return o;

    }

    /* (non-Javadoc)
     * @see org.apache.tuscany.binding.jms.databinding.DataBinding#toJMSMessage(javax.jms.Session, java.lang.Object)
     */
    public Message toJMSMessage(Session session, Object o) {
        try {

            ObjectMessage message = session.createObjectMessage(); // default
            message.setObject((Serializable)o);
            return message;

        } catch (JMSException e) {
            throw new JMSBindingRuntimeException(e);
        }
    }

}
