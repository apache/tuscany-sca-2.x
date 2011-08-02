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

package org.apache.tuscany.sca.databinding.util;

import java.util.List;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;

/**
*
* @version $Rev$ $Date$
*/
public class OperationDataBindingHelper {
    
    private static boolean isTransformationRequired(DataType source, DataType target) {
        if (source == null || target == null) { // void return type
            return false;
        }
        if (source == target) {
            return false;
        }

        String sourceDataBinding = source.getDataBinding();
        String targetDataBinding = target.getDataBinding();
        if (sourceDataBinding == targetDataBinding) {
            return false;
        }
        if (sourceDataBinding == null || targetDataBinding == null) {
            // TODO: If any of the databinding is null, then no transformation
            return false;
        }
        return !sourceDataBinding.equals(targetDataBinding);
    }

    public static boolean isTransformationRequired(Operation source, Operation target) {
        if (source == target) {
            return false;
        }

        if (source.isWrapperStyle() != target.isWrapperStyle()) {
            return true;
        }

        // Check output type
        List<DataType> sourceOutputType = source.getOutputType().getLogical();
        List<DataType> targetOutputType = target.getOutputType().getLogical();

        int outputSize = sourceOutputType.size();
        if ( outputSize != targetOutputType.size() ) {
                return true;
        }
        
        for (int i = 0; i < outputSize; i++) {
            if (isTransformationRequired(sourceOutputType.get(i), targetOutputType.get(i))) {
                return true;
            }
        }       

        List<DataType> sourceInputType = source.getInputType().getLogical();
        List<DataType> targetInputType = target.getInputType().getLogical();

        int size = sourceInputType.size();
        if (size != targetInputType.size()) {
            // TUSCANY-1682: The wrapper style may have different arguments
            return true;
        }
        for (int i = 0; i < size; i++) {
            if (isTransformationRequired(sourceInputType.get(i), targetInputType.get(i))) {
                return true;
            }
        }

        return false;
    }
}

