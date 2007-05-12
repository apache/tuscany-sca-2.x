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
package org.apache.tuscany.sca.databinding;

/**
 * Data pipe allows a data source pushes data into its sink and pipe the data into its result
 * 
 * @param <S> The data binding type of the sink
 * @param <R> The data binding type of the result
 */
public interface DataPipe<S, R> extends Transformer {

    /**
     * Returns a sink (for example, java.io.OutputStream, java.io.Writer or org.xml.sax.ContentHandler) to receive data
     * pushed by the source
     * 
     * @return The sink to consume data
     */
    S getSink();

    /**
     * Returns the data populated by the sink
     * 
     * @return
     */
    R getResult();

}
