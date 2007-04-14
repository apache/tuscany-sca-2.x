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

package org.apache.tuscany.contribution.processor;

import org.apache.tuscany.contribution.Contribution;

/**
 * A processor invoked after the contribution is loaded 
 * 
 * @version $Rev: 528640 $ $Date: 2007-04-13 13:12:12 -0700 (Fri, 13 Apr 2007) $
 */
public interface ContributionPostProcessor {
    void visit(Contribution contribution);
}
