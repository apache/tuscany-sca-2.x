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

package test;

import java.util.List;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * @version $Rev$ $Date$
 */
@Service(Aggregator.class)
public class AggregatorImpl implements Aggregator {

    @Reference(name = "uniSource")
    protected Source source;

    @Reference(name = "multiSource")
    protected List<Source> sources;

    public String getAggregatedData() {
        System.out.println("uniSource: " + source.getData());
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (Source s : sources) {
            if (i != 0) {
                sb.append(", ");
            } else {
                sb.append("multiSource: ");
            }
            sb.append(s.getData());
            i++;
        }
        return sb.toString();
    }

}
