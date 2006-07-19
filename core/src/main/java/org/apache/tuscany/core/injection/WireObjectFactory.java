/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * Uses a wire to return an object instance
 *
 * @version $Rev$ $Date$
 */
public class WireObjectFactory implements ObjectFactory {

    private OutboundWire<?> wire;
    private WireService wireService;

    public WireObjectFactory(OutboundWire<?> factory, WireService wireService) {
        this.wire = factory;
        this.wireService = wireService;
    }

    public Object getInstance() throws ObjectCreationException {
        return wireService.createProxy(wire);
    }

}
