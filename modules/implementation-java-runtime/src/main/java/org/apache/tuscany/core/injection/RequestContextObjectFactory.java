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
package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.implementation.composite.RequestContextImpl;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.WorkContext;
import org.osoa.sca.RequestContext;

/**
 * Creates instances of {@link org.apache.tuscany.core.implementation.composite.ManagedRequestContext} for injection on
 * component implementation instances
 *
 * @version $Rev$ $Date$
 */
public class RequestContextObjectFactory implements ObjectFactory<RequestContext> {
    private WorkContext workContext;

    public RequestContextObjectFactory(WorkContext workContext) {
        assert workContext != null;
        this.workContext = workContext;
    }

    public RequestContext getInstance() throws ObjectCreationException {
        return new RequestContextImpl(workContext);
    }
}
