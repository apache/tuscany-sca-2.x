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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;

/**
 * This is a special transformer to transform the input from one IDL to the
 * other one
 */
public abstract class AbstractJmsTransformer<T> extends TransformerExtension<T, T> implements PullTransformer<T, T> {

    protected static final String IDL_INPUT = "idl:input";
    protected static final String JMS_INPUT = "jms:input";
    protected static final String IDL_OUTPUT = "idl:output";
    protected static final String JMS_OUTPUT = "jms:output";

    protected DataBindingRegistry dataBindingRegistry;

    protected Mediator mediator;

    public AbstractJmsTransformer() {
        super();
    }

    /**
     * @see org.apache.tuscany.spi.databinding.Transformer#getWeight()
     */
    public int getWeight() {
        return 10;
    }

    protected Object read(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis) {

                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    try {
                        return Class.forName(desc.getName(), false, this.getClass().getClassLoader());
                    } catch (ClassNotFoundException e) {
                        return super.resolveClass(desc);
                    }
                }

            };
            Object object = ois.readObject();
            ois.close();
            bis.close();

            return object;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    protected byte[] write(Object source) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(source);
            oos.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new TransformationException(e);
        }

    }

}
