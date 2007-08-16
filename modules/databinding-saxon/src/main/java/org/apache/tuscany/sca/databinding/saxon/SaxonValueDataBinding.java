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
 package org.apache.tuscany.sca.databinding.saxon;

import java.lang.annotation.Annotation;

import net.sf.saxon.value.Value;

import org.apache.tuscany.sca.databinding.impl.BaseDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Represents data binding for parameters of type Value
 * @version $Rev$ $Date$
 * The Value type is the type accepted by the Saxon XQuery processor for
 * all simple types and strings
 */
public class SaxonValueDataBinding extends BaseDataBinding {
	public static final String NAME = Value.class.getName();
    public static final String[] ALIASES = new String[] {"saxon_value"};
    
    public SaxonValueDataBinding() {
    	super(NAME, Value.class);
    }
    
    @Override
    public boolean introspect(DataType type, Annotation[] annotations) {
        if (super.introspect(type, annotations)) {
            type.setLogical(XMLType.UNKNOWN);
            return true;
        } else {
            return false;
        }
    }
}
