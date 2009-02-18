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

package org.apache.tuscany.sca.stripes;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.util.Log;


/**
 * <p>An {@link Interceptor} that uses the implementation.web ComponentContext to inject reference 
 * proxies into newly created ActionBeans immediately following ActionBeanResolution.  For more 
 * information on how the injection is performed see {@link TuscanyHelper#injectBeans(Object,
 *  net.sourceforge.stripes.action.ActionBeanContext)}.</p>
 *
 * <p>To configure the TuscanyInterceptor for use you will need to add the following to your
 * web.xml (assuming no other interceptors are yet configured):</p>
 *
 * <pre>
 * &lt;init-param&gt;
 *     &lt;param-name&gt;Interceptor.Classes&lt;/param-name&gt;
 *     &lt;param-value&gt;
 *         org.apache.tuscany.sca.stripes.TuscanyInterceptor,
 *         net.sourceforge.stripes.controller.BeforeAfterMethodInterceptor
 *     &lt;/param-value&gt;
 * &lt;/init-param&gt;
 * </pre>
 *
 * <p>If one or more interceptors are already configured in your web.xml simply separate the
 * fully qualified names of the interceptors with commas (additional whitespace is ok).</p>
 *
 * Created for Tuscany from the Stripes SpringInterceptor written by Tim Fennell
 */
@Intercepts(LifecycleStage.ActionBeanResolution)
public class TuscanyInterceptor implements Interceptor {
    private static final Log log = Log.getInstance(TuscanyInterceptor.class);

    /**
     * Allows ActionBean resolution to proceed and then once the ActionBean has been
     * located invokes the {@link TuscanyHelper} to perform SCA reference injection.
     *
     * @param context the current execution context
     * @return the Resolution produced by calling context.proceed()
     * @throws Exception if the Tuscany injection process produced unrecoverable errors
     */
    public Resolution intercept(ExecutionContext context) throws Exception {
        Resolution resolution = context.proceed();
        log.debug("Running Tuscany dependency injection for instance of ",
                  context.getActionBean().getClass().getSimpleName());
        TuscanyHelper.injectBeans(context.getActionBean(), context.getActionBeanContext());
        return resolution;
    }
}
