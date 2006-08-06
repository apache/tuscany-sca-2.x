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
package org.apache.tuscany.binding.celtix;

import java.lang.reflect.Method;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import org.objectweb.celtix.Bus;

/**
 * The implementation of a {@link org.apache.tuscany.spi.component.Reference} configured with the Celtix binding
 *
 * @version $Rev$ $Date$
 */
public class CeltixReference<T> extends ReferenceExtension<T> {

    private Bus bus;
    private Port port;
    private Definition wsdlDef;
    private Service wsdlService;

    public CeltixReference(String name,
                           Definition wsdlDef,
                           Port port,
                           Service wsdlService,
                           Bus bus,
                           CompositeComponent<?> parent,
                           WireService wireService) {
        super(name, parent, wireService);
        this.port = port;
        this.wsdlDef = wsdlDef;
        this.wsdlService = wsdlService;
        this.bus = bus;
    }

    public TargetInvoker createTargetInvoker(Method operation) {
        //FIXME - can we pass in the method name as the operation name?
        return new CeltixInvoker(operation.getName(), bus, port, wsdlService, wsdlDef);
    }
}
