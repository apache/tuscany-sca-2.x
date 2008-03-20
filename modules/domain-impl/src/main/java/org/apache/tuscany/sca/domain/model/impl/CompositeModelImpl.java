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

package org.apache.tuscany.sca.domain.model.impl;


import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.domain.model.CompositeModel;


/**
 * A wrapper for the assembly model composite 
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public class CompositeModelImpl implements CompositeModel {
    
    private QName compositeQName;
    private Composite composite;
    
    /**
     * Retrieve the composite QName
     * 
     * @return composite QName
     */
    public QName getCompositeQName(){
        return compositeQName;
    }
    
    /**
     * Set the composite QName
     * 
     * @param compositeQName
     */    
    public void setCompositeQName(QName compositeQName) {
        this.compositeQName = compositeQName;
    }
    
    /**
     * Retrieve the assembly composite object
     * 
     * @return composite
     */
    public Composite getComposite(){
        return composite;
    }
    
    /** 
     * Set the assembly composite object
     * 
     * @param composite
     */
    public void setComposite(Composite composite){    
        this.composite = composite;
    }
}

