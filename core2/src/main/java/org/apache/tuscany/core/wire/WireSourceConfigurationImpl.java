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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.WireSourceConfiguration;
import org.apache.tuscany.spi.wire.SourceInvocationConfiguration;

/**
 * Contains configuration for the source side of a wire
 *
 * @version $Rev: 394379 $ $Date: 2006-04-15 15:01:36 -0700 (Sat, 15 Apr 2006) $
 */
public class WireSourceConfigurationImpl implements WireSourceConfiguration {

    private String referenceName;
    protected Map<Method, SourceInvocationConfiguration> configurations;
    protected QualifiedName targetName;


    /**
     * Creates the source side of a wire
     *
     * @param referenceName     the name of the reference the wire is associated with
     * @param targetName        the qualified name of the target service specified by the wire
     * @param invocationConfigs a collection of service operation-to-invocation chain mappings
     */
    public WireSourceConfigurationImpl(String referenceName, QualifiedName targetName,
                                       Map<Method, SourceInvocationConfiguration> invocationConfigs) {
        this.targetName = targetName;
        this.referenceName = referenceName;
        this.configurations = invocationConfigs;
    }

    /**
     * Creates the source side of a wire where the reference is "anonymous", i.e. on an entry point
     *
     * @param targetName        the qualified name of the target service specified by the wire
     * @param invocationConfigs a collection of service operation-to-invocation chain mappings
     */
    public WireSourceConfigurationImpl(QualifiedName targetName, Map<Method, SourceInvocationConfiguration> invocationConfigs) {
        this(null, targetName, invocationConfigs);
    }

    public QualifiedName getTargetName() {
        return targetName;
    }

    public Map<Method, SourceInvocationConfiguration> getInvocationConfigurations() {
        return configurations;
    }


    public String getReferenceName() {
        return referenceName;
    }

}
