/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on 11-Apr-2006 by Adrian Colyer
 */
package org.springframework.sca;

/**
 * @author Adrian Colyer
 * @since 2.0
 */
public class DefaultScaAdapter implements ScaAdapter {

    public Object getServiceReference(String referenceName,
                                      Class referenceType,
                                      String moduleName,
                                      String defaultServiceName) {
        // TODO
        return new Object();
    }

    public Object getPropertyReference(String propertyName, Class propertyType, String moduleName) {
        // TODO
        return new Object();
    }

    public void publishAsService(Object serviceImplementation,
                                 Class serviceInterface,
                                 String serviceName,
                                 String moduleName) {
        // TODO
    }

}
