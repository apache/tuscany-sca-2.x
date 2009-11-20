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

package org.apache.tuscany.sca.binding.atom.provider;

import java.util.Date;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Content.Type;
import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.interfacedef.DataType;

/**
 * Utility methods used in this package.
 *
 * @version $Rev$ $Date$
 */
class AtomBindingUtil {

    /**
     * Create a data item from an Atom entry.
     * @param feedEntry
     * @param itemClassType
     * @param itemXMLType
     * @param mediator
     * @return
     */
    static Entry<Object, Object> entry(org.apache.abdera.model.Entry feedEntry,
                                       DataType<?> itemClassType, DataType<?> itemXMLType, Mediator mediator) {
        if (feedEntry != null) {
            if (itemClassType.getPhysical() == Item.class || feedEntry.getContentType() == Type.HTML ) {
                String key = feedEntry.getId().toString();

                Item item = new Item();
                item.setTitle(feedEntry.getTitle());
                item.setContents(feedEntry.getContent());

                for (Link link : feedEntry.getLinks()) {
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

                item.setDate(feedEntry.getUpdated());

                return new Entry<Object, Object>(key, item);

            } else {
                String key = null; 
                if ( feedEntry.getId() != null) {
                    key = feedEntry.getId().toString();
                }

                // Create the item from XML
                if (feedEntry.getContentElement().getElements().size() == 0) {
                    return null;
                }

                String value = feedEntry.getContent();
                Object data = mediator.mediate(value, itemXMLType, itemClassType, null);

                return new Entry<Object, Object>(key, data);
            }
        } else {
            return null;
        }
    }

    /**
     * Create an Atom entry for a key and item from a collection.
     * @param entry
     * @param itemClassType
     * @param itemXMLType
     * @param mediator
     * @param factory
     * @return
     */
    static org.apache.abdera.model.Entry feedEntry(Entry<Object, Object> entry,
                                  DataType<?> itemClassType, DataType<?> itemXMLType, Mediator mediator,
                                  Factory factory) {
        Object key = entry.getKey();
        Object data = entry.getData();
        if (data instanceof Item) {
            Item item = (Item)data;

            org.apache.abdera.model.Entry feedEntry = factory.newEntry();
            if (key != null) {
                feedEntry.setId(key.toString());
            }
            feedEntry.setTitle(item.getTitle());
            feedEntry.setContentAsHtml(item.getContents());

            String href = item.getLink();
            if (href == null && key != null) {
                href = key.toString();
            }

            if (href != null) {
                feedEntry.addLink(href);
            }
            String related = item.getRelated();
            if (related != null) {
                feedEntry.addLink(related, "related");
            }
            String alternate = item.getAlternate();
            if (alternate != null) {
                feedEntry.addLink(alternate, "alternate");
            }

            Date date = item.getDate();
            if (date != null) {
                feedEntry.setUpdated(date);
            }
            return feedEntry;

        } else if (data != null) {
            org.apache.abdera.model.Entry feedEntry = factory.newEntry();
            feedEntry.setId(key.toString());
            feedEntry.setTitle("item");


            // Convert the item to XML
            String value = mediator.mediate(data, itemClassType, itemXMLType, null).toString();

            Content content = factory.newContent();
            content.setContentType(Content.Type.XML);
            content.setValue(value);

            feedEntry.setContentElement(content);

            feedEntry.addLink(key.toString());

            return feedEntry;
        } else {
            return null;
        }
    }
    
}
