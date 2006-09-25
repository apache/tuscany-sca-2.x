/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.databinding.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.model.DataType;

public class JAXBContextHelper {
    // TODO: Do we need to set them for source and target?
    public static final String JAXB_CLASSES = "jaxb.classes";

    public static final String JAXB_CONTEXT_PATH = "jaxb.contextPath";

    private JAXBContextHelper() {
    }

    public static JAXBContext createJAXBContext(TransformationContext tContext, boolean source) throws JAXBException {
        if (tContext == null)
            throw new TransformationException("JAXB context is not set for the transformation.");

        // FIXME: We should check the context path or classes
        // FIXME: What should we do if JAXB is an intermediate node?
        DataType<?> bindingContext = source ? tContext.getSourceDataType() : tContext.getTargetDataType();
        String contextPath = (String) bindingContext.getMetadata(JAXB_CONTEXT_PATH);
        JAXBContext context = null;
        if (contextPath != null)
            context = JAXBContext.newInstance(contextPath);
        else {
            Class[] classes = (Class[]) bindingContext.getMetadata(JAXB_CLASSES);
            context = JAXBContext.newInstance(classes);
        }
        if (context == null)
            throw new TransformationException("JAXB context is not set for the transformation.");
        return context;
    }

}
