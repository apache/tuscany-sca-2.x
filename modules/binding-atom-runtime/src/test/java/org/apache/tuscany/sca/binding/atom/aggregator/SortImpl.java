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
package org.apache.tuscany.sca.binding.atom.aggregator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.osoa.sca.annotations.Property;

/**
 * Implementation of a Feed Sort service component.
 * 
 * @version $Rev: 685663 $ $Date: 2008-08-13 13:11:20 -0700 (Wed, 13 Aug 2008) $
 */
public class SortImpl implements Sort {

    @Property
    public boolean newFirst = true;

    @SuppressWarnings("unchecked")
    public List<Entry> sort(List<Entry> entries) {
        Entry[] entriesArray = new Entry[entries.size()];
        entriesArray = (Entry<String,Item>[])entries.toArray(entriesArray);
        Arrays.sort(entriesArray, new Comparator() {
            public int compare(final Object xObj, final Object yObj) {
            	Entry x = (Entry) xObj;
            	Entry y = (Entry) yObj;
            	Item xItem = (Item) x.getData();
            	Item yItem = (Item) y.getData();
                Date xDate = xItem.getDate();
                Date yDate = yItem.getDate();
                if (xDate == null)
                    return -1;
                if (newFirst)
                    return yDate.compareTo(xDate);
                else
                    return xDate.compareTo(yDate);
            }
        });
        return Arrays.asList(entriesArray);
    }
}
