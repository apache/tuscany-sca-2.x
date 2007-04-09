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
package org.apache.tuscany.core.binding.local;

import java.net.URI;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * The runtime representaion of the local reference binding
 *
 * @version $Rev$ $Date$
 * @Deprecated
 */
public class LocalReferenceBinding extends ReferenceBindingExtension {

    public LocalReferenceBinding(URI name, URI targetUri) throws CoreRuntimeException {
        super(name, targetUri);
    }

    public QName getBindingType() {
        return Wire.LOCAL_BINDING;
    }

    public TargetInvoker createTargetInvoker(String name, Operation operation)
        throws TargetInvokerCreationException {
        if (operation.isCallback()) {
            return new LocalCallbackTargetInvoker(operation, wire);
        } else {
            return new LocalTargetInvoker(operation, wire);
        }
    }

    public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
        throws TargetInvokerCreationException {
        return null;
    }

}
