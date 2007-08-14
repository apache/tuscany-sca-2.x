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
package org.apache.tuscany.sca.binding.sca;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.SCABinding;


/**
 * Represents an SCA binding used in the distributed runtime.
 * At the moment just provides us with a sensibly named type 
 * against which the distributed sca binding providers can be registered
 * 
 * @version $Rev: 556322 $ $Date: 2007-07-14 19:53:15 +0100 (Sat, 14 Jul 2007) $
 */
public interface DistributedSCABinding extends Binding {
    
    public SCABinding getSCABinding();
    public void setSCABinging(SCABinding scaBinding);

}
