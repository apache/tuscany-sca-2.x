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
package org.apache.tuscany.sca.implementation.spring.processor;

import java.lang.annotation.Annotation;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.util.Assert;

public class InitDestroyAnnotationProcessor extends InitDestroyAnnotationBeanPostProcessor {    
    
    private static final long serialVersionUID = 0;
    
    private Class<? extends Annotation> initAnnotationType = Init.class;

    private Class<? extends Annotation> destroyAnnotationType = Destroy.class;
    
    /**
     * Gets init annotation type.
     */
    protected Class<? extends Annotation> getInitAnnotationType() {
        return this.initAnnotationType;
    }

    /**
     * Sets init annotation type.
     */
    /*public void setInitAnnotationType(Class<? extends Annotation> initAnnotationType) {
        Assert.notNull(initAnnotationType, "Init annotation type must not be null.");
        this.initAnnotationType = initAnnotationType;
    }*/
    
    /**
     * Gets destroy annotation type.
     */
    protected Class<? extends Annotation> getDestroyAnnotationType() {
        return this.destroyAnnotationType;
    }

    /**
     * Sets destroy annotation type.
     */
    /*public void setDestroyAnnotationType(Class<? extends Annotation> destroyAnnotationType) {
        Assert.notNull(destroyAnnotationType, "Destroy annotation type must not be null.");
        this.destroyAnnotationType = destroyAnnotationType;
    }*/

    public InitDestroyAnnotationProcessor () {
        // Set the @Init annotation type
        setInitAnnotationType(initAnnotationType);
        
        // Set the @Destroy annotation type
        setDestroyAnnotationType(destroyAnnotationType);
    }
}
