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

package org.apache.tuscany.sca.contribution;

import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Extensible;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * The representation of an SCA contribution.
 *
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.asclient
 */
public interface Contribution extends Artifact, Extensible {

    /**
     * Default location of contribution metadata in an SCA contribution.
     */
    String SCA_CONTRIBUTION_META = "META-INF/sca-contribution.xml";

    /**
     * Default location of a generated contribution metadata in an SCA contribution.
     */
    String SCA_CONTRIBUTION_GENERATED_META = "META-INF/sca-contribution-generated.xml";

    /**
     * Returns a list of exports based on the contribution metadata.
     *
     * @return The list of exports in this contribution
     */
    List<Export> getExports();

    /**
     * Returns a list of imports based on the contribution metadata.
     *
     * @return The list of imports in this contribution
     */
    List<Import> getImports();

    /**
     * Returns the list of deployable composites in the contribution.
     *
     * @return The list of deployable composites
     */
    List<Composite> getDeployables();

    /**
     * Returns the list of artifacts in the contribution.
     *
     * @return The list of artifacts in the contribution
     */
    List<Artifact> getArtifacts();

    /**
     * Returns the model resolver for the models representing the artifacts
     * visible in the scope of this contribution.
     *
     * @return The model resolver
     */
    ModelResolver getModelResolver();

    /**
     * Sets the model resolver for the models representing the artifacts
     * visible in the scope of this contribution.
     *
     * @param modelResolver The model resolver
     */
    void setModelResolver(ModelResolver modelResolver);

    /**
     * Returns the list of contributions that this contribution depends on.
     *
     * @return
     */
    List<Contribution> getDependencies();

    /**
     * Returns the ClassLoader used to load classes and resources from
     * this contribution
     *
     * FIXME Remove this, the base contribution model should not depend
     * on Java ClassLoaders.
     *
     * @return The contribution ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * Sets the ClassLoader used to load classes and resources from
     * this contribution
     *
     * FIXME Remove this, the base contribution model should not depend
     * on Java ClassLoaders.
     *
     * @param classLoader the contribution class loader
     */
    void setClassLoader(ClassLoader classLoader);

    /**
     * Get a list of mime types that apply to this contribution archive
     * @return
     */
    Set<String> getTypes();

}