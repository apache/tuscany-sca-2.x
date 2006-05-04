/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.context.event;

import org.apache.tuscany.spi.context.InstanceContext;

/**
 * An event propagated upon the creation of an instance belonging to a {@link org.apache.tuscany.spi.context.Context}
 *
 * @version $$Rev$$ $$Date$$
 */
public class InstanceCreated extends AbstractEvent {

    private InstanceContext context;

    public InstanceCreated(Object source, InstanceContext context) {
        super(source);
        assert(context != null): "Instance context was null";
        this.context = context;
    }

    public InstanceContext getContext() {
        return context;
    }


}
