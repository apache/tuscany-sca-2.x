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

package org.apache.tuscany.sca.itest.databindings.jaxb.impl;

import org.apache.tuscany.sca.itest.databindings.jaxb.Bean1;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean2;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean3;
import org.apache.tuscany.sca.itest.databindings.jaxb.GenericsLocalService;
import org.apache.tuscany.sca.itest.databindings.jaxb.GenericsService;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of GenericsService.
 * This implementation provides both a local and a remotable service.
 * 
 * @version $Rev$ $Date$
 */
@Service(interfaces={GenericsService.class, GenericsLocalService.class})
public class GenericsServiceImpl implements GenericsService, GenericsLocalService {

    public Bean1<String> getTypeExplicit(Bean1<String> arg) {
        return GenericsTransformer.getTypeExplicit(arg);
    }

    public <T> Bean1<T> getTypeUnbound(T[] anArray) {
        return GenericsTransformer.getTypeUnbound(anArray);
    }

    public <T extends Bean2> Bean1<T> getTypeExtends(T[] anArray) {
        return GenericsTransformer.getTypeExtends(anArray);
    }

    public <T extends Bean1<String>> Bean1<T> getRecursiveTypeBound(T[] anArray) {
        return GenericsTransformer.getRecursiveTypeBound(anArray);
    }

    public Bean1<?> getWildcardUnbound(Bean1<?> arg) {
        return GenericsTransformer.getWildcardUnbound(arg);
    }

    public Bean1<? super Bean3> getWildcardSuper(Bean1<? super Bean3> arg) {
        return GenericsTransformer.getWildcardSuper(arg);
    }

    public Bean1<? extends Bean2> getWildcardExtends(Bean1<? extends Bean2> arg) {
        return GenericsTransformer.getWildcardExtends(arg);
    }

    public Bean2 getPolymorphic(Bean2 arg) {
        return GenericsTransformer.getPolymorphic(arg);
    }
}
