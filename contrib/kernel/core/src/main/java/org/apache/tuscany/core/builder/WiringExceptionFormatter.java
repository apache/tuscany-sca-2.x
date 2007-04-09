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

import java.io.PrintWriter;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.builder.WiringException;

import org.apache.tuscany.host.monitor.ExceptionFormatter;
import org.apache.tuscany.host.monitor.FormatterRegistry;

/**
 * Formats {@link WiringException}s
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public class WiringExceptionFormatter implements ExceptionFormatter {
    private FormatterRegistry factory;

    public WiringExceptionFormatter(@Reference FormatterRegistry factory) {
        this.factory = factory;
        factory.register(this);
    }

    public boolean canFormat(Class<?> type) {
        return WiringException.class.isAssignableFrom(type);
    }

    @Destroy
    public void destroy() {
        factory.unregister(this);
    }

    public PrintWriter write(PrintWriter writer, Throwable exception) {
        assert exception instanceof WiringException;
        WiringException e = (WiringException) exception;
        e.appendBaseMessage(writer);
        if (e.getSourceUri() != null) {
            writer.write("\nSource : " + e.getSourceUri());
        }
        if (e.getTargetUri() != null) {
            writer.write("\nTarget : " + e.getTargetUri());
        }
        return writer;
    }
}
