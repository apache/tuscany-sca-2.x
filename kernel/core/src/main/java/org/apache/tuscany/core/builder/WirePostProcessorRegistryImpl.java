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
package org.apache.tuscany.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.builder.WirePostProcessor;
import org.apache.tuscany.spi.builder.WirePostProcessorRegistry;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * The default implementation of a <code>WirePostProcessor</code>
 *
 * @version $Rev$ $Date$
 */
public class WirePostProcessorRegistryImpl implements WirePostProcessorRegistry {

    private final List<WirePostProcessor> processors = new ArrayList<WirePostProcessor>();

    public void process(OutboundWire<?> source, InboundWire<?> target) {
        for (WirePostProcessor processor : processors) {
            processor.process(source, target);
        }
    }

    public void process(InboundWire<?> source, OutboundWire<?> target) {
        for (WirePostProcessor processor : processors) {
            processor.process(source, target);
        }
    }

    public void register(WirePostProcessor processor) {
        processors.add(processor);
    }

    public void unregister(WirePostProcessor processor) {
        processors.remove(processor);
    }
}
