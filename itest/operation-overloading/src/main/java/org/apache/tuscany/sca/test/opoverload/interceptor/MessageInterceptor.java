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

package org.apache.tuscany.sca.test.opoverload.interceptor;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.Wire;

/**
 * An interceptor to transform data accross databindings on the wire
 * 
 * @version $Rev$ $Date$
 */
public class MessageInterceptor implements Interceptor {
    private Interceptor next;

    private CompositeComponent compositeComponent;

    private Operation<?> sourceOperation;

    private Operation<?> targetOperation;

    protected FileWriter fw= null;
  

    public MessageInterceptor(Wire sourceWire, Operation<?> sourceOperation, Operation<?> targetOperation) {
        super();
        // this.sourceWire = sourceWire;
        this.sourceOperation = sourceOperation;
        // this.targetWire = targetWire;
        this.targetOperation = targetOperation;
        this.compositeComponent = sourceWire.getContainer().getParent();
        
        
        try {
             fw = new FileWriter("MessageInterceptor.log", true);
        } catch (IOException e) {
            fw= null;
            e.printStackTrace();
        }
    }

    /**
     * @see org.apache.tuscany.spi.wire.Interceptor#getNext()
     */
    public Interceptor getNext() {
        return next;
    }

    /**
     * @see org.apache.tuscany.spi.wire.Interceptor#invoke(org.apache.tuscany.spi.wire.Message)
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.spi.wire.Interceptor#invoke(org.apache.tuscany.spi.wire.Message)
     */
    public Message invoke(Message msg) {
        out(msg.toString());
        return msg;
      }

 

    private void out(String string) {
        if( null != fw){
            try {
                fw.write(string);
                fw.flush();
            } catch (IOException e) {
                
            }
        }
        
    }

    /**
     * @see org.apache.tuscany.spi.wire.Interceptor#isOptimizable()
     */
    public boolean isOptimizable() {
        return false;
    }

    /**
     * @see org.apache.tuscany.spi.wire.Interceptor#setNext(org.apache.tuscany.spi.wire.Interceptor)
     */
    public void setNext(Interceptor next) {
        this.next = next;
    }


}
