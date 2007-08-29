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
package feed;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.osoa.sca.annotations.Property;

import com.sun.syndication.feed.atom.Entry;

/**
 * Implementation of a Feed Sort service component.
 * 
 * @version $Rev$ $Date$
 */
public class SortImpl implements Sort {

    @Property
    public boolean newFirst = true;

    @SuppressWarnings("unchecked")
    public List<Entry> sort(List<Entry> entries) {
        Entry[] entriesArray = new Entry[entries.size()];
        entriesArray = (Entry[])entries.toArray(entriesArray);
        Arrays.sort(entriesArray, new Comparator() {
            public int compare(final Object xObj, final Object yObj) {
                Date xDate = ((Entry)xObj).getPublished();
                Date yDate = ((Entry)yObj).getPublished();
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
