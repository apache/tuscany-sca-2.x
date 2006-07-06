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
package org.apache.tuscany.databinding.trax;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;

import org.apache.tuscany.databinding.PushStyleTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;

/**
 * Transform TrAX Source to Result
 * 
 */
public class Source2ResultTransformer implements PushStyleTransformer<Source, Result> {
    private static final TransformerFactory factory = TransformerFactory.newInstance();

    public void transform(Source source, Result result, TransformationContext context) {
        try {
            // FIXME:
            Source xslt = context == null ? null : (Source) context.get(Source.class.getName());
            javax.xml.transform.Transformer transformer = factory.newTransformer(xslt);
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<Source> getSourceType() {
        return Source.class;
    }

    public Class<Result> getSinkType() {
        return Result.class;
    }

    public int getWeight() {
        return 40;
    }

}
