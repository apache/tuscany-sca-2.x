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
package org.apache.tuscany.services.spi.contribution;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.tuscany.services.contribution.model.Contribution;

/**
 * Interface for services that can process contributions.
 * 
 * @version $Rev$ $Date$
 */
public interface ContributionProcessor {
    /**
     * Process a contribution or an artifact in the contribution from the input
     * stream. The processor might add artifacts or model objects to the
     * contribution object.
     * 
     * @param contribution The contribution model that will be used to hold the
     *            results from the processing
     * @param source The URI for the contribution/artifact
     * @param inputStream The input stream for the contribution. The stream will
     *            not be closed but the read position after the call is
     *            undefined
     * @throws DeploymentException if there was a problem with the contribution
     * @throws IOException if there was a problem reading the stream
     */
    void processContent(Contribution contribution, URI source, InputStream inputStream) throws ContributionException,
        IOException;

    /**
     * Process a contribution from another model object. It will be used for the
     * case that one artifact has other inline artifacts, for example, the WSDL
     * with inline schemas. The schema contribution processor should be able to
     * load the schema model from the WSDL definition.
     * 
     * @param contribution The contribution model that will be used to hold the
     *            results from the processing
     * @param source The URI for the contribution/artifact. 
     * @param modelObject A model object for further processing by the processor
     * @throws DeploymentException
     * @throws IOException
     */
    void processModel(Contribution contribution, URI source, Object modelObject) throws ContributionException,
        IOException;
}
