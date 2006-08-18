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
package org.apache.tuscany.test.binding;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.wire.WireService;

/**
 * @version $Rev: 431036 $ $Date: 2006-08-12 06:58:50 -0700 (Sat, 12 Aug 2006) $
 */
public class TestBindingService<T> extends ServiceExtension<T> {
    public TestBindingService(String name,
                              Class<T> interfaze,
                              CompositeComponent parent,
                              WireService wireService) throws CoreRuntimeException {
        super(name, interfaze, parent, wireService);
        // do nothing, but this could register with the host environment
    }
}
