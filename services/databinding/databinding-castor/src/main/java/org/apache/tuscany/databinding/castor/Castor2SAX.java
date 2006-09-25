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
package org.apache.tuscany.databinding.castor;

import java.io.Writer;

import org.apache.tuscany.spi.databinding.PushTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.exolab.castor.xml.Marshaller;

public class Castor2SAX<T> extends TransformerExtension<T, Writer> implements PushTransformer<T, Writer> {
    private Class<T> type;

    /**
     * @param type
     */
    public Castor2SAX(Class<T> type) {
        super();
        this.type = type;
    }

    public Class getSourceType() {
        return type;
    }

    public Class getTargetType() {
        return Writer.class;
    }

    public int getWeight() {
        return 40;
    }

    /**
     * @see org.apache.tuscany.spi.databinding.PullTransformer#transform(java.lang.Object, org.apache.tuscany.spi.databinding.TransformationContext)
     */
    public void transform(T source, Writer writer, TransformationContext context) {
        try {
            Marshaller.marshal(source, writer);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
