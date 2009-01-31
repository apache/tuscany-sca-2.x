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

package org.apache.tuscany.sca.binding.gdata.provider;

import java.util.Date;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.DataType;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextContent;

/**
 * Utility methods used in this package.
 * 
 * @version $Rev$ $Date$
 */
class GdataBindingUtil {

    /**
     * Create a data item from an GData entry.
     * 
     * @param feedEntry
     * @param itemClassType
     * @param itemXMLType
     * @param mediator
     * @return
     */
    static Entry<Object, Object> entry(com.google.gdata.data.Entry feedEntry,
                                       DataType<?> itemClassType,
                                       DataType<?> itemXMLType,
                                       Mediator mediator) {
        if (feedEntry != null) {
            if (itemClassType.getPhysical() == Item.class) {
                String key = feedEntry.getId().toString();

                Item item = new Item();
                item.setTitle(feedEntry.getTitle().toString());
                TextContent content = (TextContent)feedEntry.getContent();
                item.setContents(content.getContent().getPlainText());

                for (com.google.gdata.data.Link link : feedEntry.getLinks()) {
                    if (link.getRel() == null || "self".equals(link.getRel())) {
                        if (item.getLink() == null) {
                            item.setLink(link.getHref().toString());
                        }
                    } else if ("related".equals(link.getRel())) {
                        item.setRelated(link.getHref().toString());
                    } else if ("alternate".equals(link.getRel())) {
                        item.setAlternate(link.getHref().toString());
                    }
                }

                Date date = new Date(feedEntry.getUpdated().getValue());
                item.setDate(date);
                return new Entry<Object, Object>(key, item);

            } else {
                String key = null;
                if (feedEntry.getId() != null) {
                    key = feedEntry.getId();
                }

                // Create the item from XML
                if (feedEntry.getContent() == null) {
                    return null;
                }

                TextContent content = (TextContent)feedEntry.getContent();
                String value = content.getContent().getPlainText();
                Object data = mediator.mediate(value, itemXMLType, itemClassType, null);
                return new Entry<Object, Object>(key, data);
            }
        } else {
            return null;
        }
    }

    /**
     * Create an Gdata entry for a key and item from a collection.
     * 
     * @param entry
     * @param itemClassType
     * @param itemXMLType
     * @param mediator
     * @param factory
     * @return
     */
    static com.google.gdata.data.Entry feedEntry(Entry<Object, Object> entry,
                                                 DataType<?> itemClassType,
                                                 DataType<?> itemXMLType,
                                                 Mediator mediator) {

        Object key = entry.getKey();
        Object data = entry.getData();
        if (data instanceof Item) {
            Item item = (Item)data;

            com.google.gdata.data.Entry feedEntry = new com.google.gdata.data.Entry();
            if (key != null) {
                feedEntry.setId(key.toString());
            }
            feedEntry.setTitle(new PlainTextConstruct(item.getTitle()));
            feedEntry.setContent(new PlainTextConstruct(item.getContents()));

            String href = item.getLink();
            if (href == null && key != null) {
                href = key.toString();
            }

            if (href != null) {
                feedEntry.addHtmlLink(href, "", "");
            }
            String related = item.getRelated();
            if (related != null) {
                feedEntry.addHtmlLink(related, "", "related");
            }
            String alternate = item.getAlternate();
            if (alternate != null) {
                feedEntry.addHtmlLink(alternate, "", "alternate");
            }

            Date date = item.getDate();
            if (date != null) {
                DateTime datetime = new DateTime(date);
                feedEntry.setUpdated(datetime);
            }
            return feedEntry;

        } else if (data != null) {

            com.google.gdata.data.Entry feedEntry = new com.google.gdata.data.Entry();
            feedEntry.setId(key.toString());
            feedEntry.setTitle(new PlainTextConstruct("item"));

            // Convert the item to XML
            String value = mediator.mediate(data, itemClassType, itemXMLType, null).toString();

            // Might be wrong because the example uses XML datatype, I am using
            // plainText here
            feedEntry.setContent(new PlainTextConstruct(value));

            feedEntry.addHtmlLink(key.toString(), "", "");
            return feedEntry;

        } else {
            return null;
        }
    }

}
