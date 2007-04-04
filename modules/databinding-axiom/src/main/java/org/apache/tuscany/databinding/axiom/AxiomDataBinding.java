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

package org.apache.tuscany.databinding.axiom;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.spi.databinding.ExceptionHandler;
import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.databinding.extension.DataBindingExtension;

/**
 * DataBinding for AXIOM
 */
public class AxiomDataBinding extends DataBindingExtension {
    
    public static final String NAME = OMElement.class.getName();
    public static final String[] ALIASES = new String[] {"axiom"};

    public AxiomDataBinding() {
        super(NAME, ALIASES, OMElement.class);
    }

    /**
     * @see org.apache.tuscany.spi.databinding.extension.DataBindingExtension#getWrapperHandler()
     */
    @Override
    public WrapperHandler getWrapperHandler() {
        return new OMElementWrapperHandler();
    }

    public Object copy(Object source) {
        if ( OMElement.class.isAssignableFrom(source.getClass()) ) {
            try {
                OMElement sourceElement = (OMElement)source;
                return sourceElement.cloneOMElement();
            } catch ( Exception e ) {
                throw new IllegalArgumentException(e);
            }
        }
        return super.copy(source);
    }
    
    @Override
    public ExceptionHandler getExceptionHandler() {
        return new AxiomExceptionHandler();
    }    

}
