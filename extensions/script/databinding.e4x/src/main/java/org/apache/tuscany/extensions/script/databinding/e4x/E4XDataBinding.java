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

package org.apache.tuscany.extensions.script.databinding.e4x;

import org.apache.tuscany.spi.databinding.DataBinding;
import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.databinding.extension.DataBindingExtension;
import org.mozilla.javascript.xmlimpl.XML;
import org.osoa.sca.annotations.Service;

/**
 * DataBinding for E4X
 * 
 * This requires Rhino using the WSO2 Axiom based E4X impl
 */
@Service(DataBinding.class)
public class E4XDataBinding extends DataBindingExtension {
    
    public static final String NAME = XML.class.getName();

    public E4XDataBinding() {
        super(NAME, XML.class);
    }

    /**
     * @see org.apache.tuscany.spi.databinding.extension.DataBindingExtension#getWrapperHandler()
     */
    @Override
    public WrapperHandler getWrapperHandler() {
        return new E4XWrapperHandler();
    }

    /**
     * Treat E4X as pass-by-ref
     */
    public Object copy(Object source) {
        return source;
    }
}
