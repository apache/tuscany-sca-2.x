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
package org.apache.tuscany.databinding.xml;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.spi.databinding.PushTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.osoa.sca.annotations.Service;

/**
 * Transform TrAX Source to Result
 */
@Service(org.apache.tuscany.spi.databinding.Transformer.class)
public class Source2ResultTransformer extends TransformerExtension<Source, Result> implements
    PushTransformer<Source, Result> {
    private static final TransformerFactory FACTORY = TransformerFactory.newInstance();

    public void transform(Source source, Result result, TransformationContext context) {
        try {
            javax.xml.transform.Transformer transformer = FACTORY.newTransformer();
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class getSourceType() {
        return Source.class;
    }

    public Class getTargetType() {
        return Result.class;
    }

    public int getWeight() {
        return 40;
    }

}
