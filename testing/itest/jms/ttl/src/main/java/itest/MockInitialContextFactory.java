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

package itest;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Some classes that mock up various JMS interfaces with the end result
 * of having the Time-To-Live value set on the static timeToLive 
 * 
 * This class is referenced from the AMQ jndi.properties file
 */
public class MockInitialContextFactory extends org.apache.activemq.jndi.ActiveMQInitialContextFactory{
    
    public static Object lock = new Object();
    public static Long timeToLive;

    public Context getInitialContext(Hashtable environment) throws NamingException {
        return new Context() {

            public Object addToEnvironment(String propName, Object propVal) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public void bind(Name name, Object obj) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public void bind(String name, Object obj) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public void close() throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public Name composeName(Name name, Name prefix) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public String composeName(String name, String prefix) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public Context createSubcontext(Name name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public Context createSubcontext(String name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public void destroySubcontext(Name name) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public void destroySubcontext(String name) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public Hashtable<?, ?> getEnvironment() throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public String getNameInNamespace() throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public NameParser getNameParser(Name name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public NameParser getNameParser(String name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public Object lookup(Name name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public Object lookup(String name) throws NamingException {
                if (name.endsWith("ConnectionFactory")) {
                    return new ConnectionFactory() {
                        public Connection createConnection() throws JMSException {
                            return new Connection(){

                                public void close() throws JMSException {
                                    // TODO Auto-generated method stub
                                    
                                }

                                public ConnectionConsumer createConnectionConsumer(Destination arg0,
                                                                                   String arg1,
                                                                                   ServerSessionPool arg2,
                                                                                   int arg3) throws JMSException {
                                    // TODO Auto-generated method stub
                                    return null;
                                }

                                public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
                                                                                          String arg1,
                                                                                          String arg2,
                                                                                          ServerSessionPool arg3,
                                                                                          int arg4) throws JMSException {
                                    // TODO Auto-generated method stub
                                    return null;
                                }

                                public Session createSession(boolean arg0, int arg1) throws JMSException {
                                    // TODO Auto-generated method stub
                                    return new Session() {

                                        public void close() throws JMSException {
                                            // TODO Auto-generated method stub
                                            
                                        }

                                        public void commit() throws JMSException {
                                            // TODO Auto-generated method stub
                                            
                                        }

                                        public QueueBrowser createBrowser(Queue arg0) throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public QueueBrowser createBrowser(Queue arg0, String arg1) throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public BytesMessage createBytesMessage() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return new BytesMessage() {

                                                public long getBodyLength() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public boolean readBoolean() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return false;
                                                }

                                                public byte readByte() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public int readBytes(byte[] arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public int readBytes(byte[] arg0, int arg1) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public char readChar() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public double readDouble() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public float readFloat() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public int readInt() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public long readLong() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public short readShort() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public String readUTF() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public int readUnsignedByte() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public int readUnsignedShort() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public void reset() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeBoolean(boolean arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeByte(byte arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeBytes(byte[] arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeBytes(byte[] arg0, int arg1, int arg2)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeChar(char arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeDouble(double arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeFloat(float arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeInt(int arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeLong(long arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeObject(Object arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeShort(short arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void writeUTF(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void acknowledge() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void clearBody() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void clearProperties() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public boolean getBooleanProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return false;
                                                }

                                                public byte getByteProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public double getDoubleProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public float getFloatProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public int getIntProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public String getJMSCorrelationID() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public int getJMSDeliveryMode() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public Destination getJMSDestination() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public long getJMSExpiration() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public String getJMSMessageID() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public int getJMSPriority() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public boolean getJMSRedelivered() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return false;
                                                }

                                                public Destination getJMSReplyTo() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public long getJMSTimestamp() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public String getJMSType() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public long getLongProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public Object getObjectProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public Enumeration getPropertyNames() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public short getShortProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public String getStringProperty(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public boolean propertyExists(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return false;
                                                }

                                                public void setBooleanProperty(String arg0, boolean arg1)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setByteProperty(String arg0, byte arg1) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setDoubleProperty(String arg0, double arg1)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setFloatProperty(String arg0, float arg1)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setIntProperty(String arg0, int arg1) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSCorrelationID(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSDeliveryMode(int arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSDestination(Destination arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSExpiration(long arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSMessageID(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSPriority(int arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSRedelivered(boolean arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSReplyTo(Destination arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSTimestamp(long arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setJMSType(String arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setLongProperty(String arg0, long arg1) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setObjectProperty(String arg0, Object arg1)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setShortProperty(String arg0, short arg1)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setStringProperty(String arg0, String arg1)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }};
                                        }

                                        public MessageConsumer createConsumer(Destination arg0) throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public MessageConsumer createConsumer(Destination arg0, String arg1)
                                            throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public MessageConsumer createConsumer(Destination arg0,
                                                                              String arg1,
                                                                              boolean arg2) throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1)
                                            throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public TopicSubscriber createDurableSubscriber(Topic arg0,
                                                                                       String arg1,
                                                                                       String arg2,
                                                                                       boolean arg3)
                                            throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public MapMessage createMapMessage() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public Message createMessage() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public ObjectMessage createObjectMessage() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public ObjectMessage createObjectMessage(Serializable arg0) throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public MessageProducer createProducer(Destination arg0) throws JMSException {
                                            return  new MessageProducer() {

                                                public void close() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public int getDeliveryMode() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public Destination getDestination() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return null;
                                                }

                                                public boolean getDisableMessageID() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return false;
                                                }

                                                public boolean getDisableMessageTimestamp() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return false;
                                                }

                                                public int getPriority() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public long getTimeToLive() throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    return 0;
                                                }

                                                public void send(Message arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void send(Destination arg0, Message arg1) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void send(Message arg0, int arg1, int arg2, long arg3)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void send(Destination arg0,
                                                                 Message arg1,
                                                                 int arg2,
                                                                 int arg3,
                                                                 long arg4) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setDeliveryMode(int arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setDisableMessageID(boolean arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setDisableMessageTimestamp(boolean arg0)
                                                    throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setPriority(int arg0) throws JMSException {
                                                    // TODO Auto-generated method stub
                                                    
                                                }

                                                public void setTimeToLive(long arg0) throws JMSException {
                                                    MockInitialContextFactory.timeToLive = Long.valueOf(arg0);
                                                    synchronized(MockInitialContextFactory.lock) {
                                                        MockInitialContextFactory.lock.notifyAll();
                                                    }
                                                }};
                                        }

                                        public Queue createQueue(String arg0) throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public StreamMessage createStreamMessage() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public TemporaryQueue createTemporaryQueue() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public TemporaryTopic createTemporaryTopic() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public TextMessage createTextMessage() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public TextMessage createTextMessage(String arg0) throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public Topic createTopic(String arg0) throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public int getAcknowledgeMode() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return 0;
                                        }

                                        public MessageListener getMessageListener() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return null;
                                        }

                                        public boolean getTransacted() throws JMSException {
                                            // TODO Auto-generated method stub
                                            return false;
                                        }

                                        public void recover() throws JMSException {
                                            // TODO Auto-generated method stub
                                            
                                        }

                                        public void rollback() throws JMSException {
                                            // TODO Auto-generated method stub
                                            
                                        }

                                        public void run() {
                                            // TODO Auto-generated method stub
                                            
                                        }

                                        public void setMessageListener(MessageListener arg0) throws JMSException {
                                            // TODO Auto-generated method stub
                                            
                                        }

                                        public void unsubscribe(String arg0) throws JMSException {
                                            // TODO Auto-generated method stub
                                            
                                        }};
                                }

                                public String getClientID() throws JMSException {
                                    // TODO Auto-generated method stub
                                    return null;
                                }

                                public ExceptionListener getExceptionListener() throws JMSException {
                                    // TODO Auto-generated method stub
                                    return null;
                                }

                                public ConnectionMetaData getMetaData() throws JMSException {
                                    // TODO Auto-generated method stub
                                    return null;
                                }

                                public void setClientID(String arg0) throws JMSException {
                                    // TODO Auto-generated method stub
                                    
                                }

                                public void setExceptionListener(ExceptionListener arg0) throws JMSException {
                                    // TODO Auto-generated method stub
                                    
                                }

                                public void start() throws JMSException {
                                    // TODO Auto-generated method stub
                                    
                                }

                                public void stop() throws JMSException {
                                    // TODO Auto-generated method stub
                                    
                                }};
                        }
                        public Connection createConnection(String arg0, String arg1) throws JMSException {
                            return null;
                        }};
                } else {
                    return new Queue(){
                        public String getQueueName() throws JMSException {
                            return null;
                        }};
                }
            }

            public Object lookupLink(Name name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public Object lookupLink(String name) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public void rebind(Name name, Object obj) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public void rebind(String name, Object obj) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public Object removeFromEnvironment(String propName) throws NamingException {
                // TODO Auto-generated method stub
                return null;
            }

            public void rename(Name oldName, Name newName) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public void rename(String oldName, String newName) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public void unbind(Name name) throws NamingException {
                // TODO Auto-generated method stub
                
            }

            public void unbind(String name) throws NamingException {
                // TODO Auto-generated method stub
                
            }};
    }
    
}
