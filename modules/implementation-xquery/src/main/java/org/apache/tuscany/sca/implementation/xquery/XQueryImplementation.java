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
package org.apache.tuscany.sca.implementation.xquery;

import java.lang.reflect.Method;
import java.util.Map;

import net.sf.saxon.query.XQueryExpression;

import org.apache.tuscany.sca.assembly.Implementation;

/**
 * Class representing the XQuery implementation type
 * @version $Rev$ $Date$
 */
public interface XQueryImplementation extends Implementation {

    /**
     * Location of the xquery implementation file
     * @return
     */
    public String getLocation();

    public void setLocation(String location);

    /**
     * The XQuery expression that is loaded from the xquery implementation file
     * @return
     */
    public String getXqExpression();

    public void setXqExpression(String expression);

    /**
     * The XQuery expression should be extended for with additional
     * script, which provides the external variables needed to invoke
     * a function. In this way for each function that is defined in the 
     * original XQuery expression additional expression is defined, which
     * can invoke this function, using external variables as input.
     * These expression extensions are stored in this map. It provides for
     * each method of a service interface that is implemented by this component
     * type corresponding expression extension
     * @return
     */
    public Map<Method, String> getXqExpressionExtensionsMap();

    /**
     * This map is a kind of cache for function invokations. If a given
     * xquery function of this implementation has been invoked already
     * its compiled expression can be reused. In this manner the preformance
     * can be increased
     * @return
     */
    public Map<String, XQueryExpression> getCompiledExpressionsCache();
}
