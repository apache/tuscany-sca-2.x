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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Link;

// To-Change
// import com.google.gdata.data.Entry;
// import com.google.gdata.data.Feed;
// import com.google.gdata.data.Link;

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
 */
class GdataBindingUtil {

    /**
     * Create a data item from an Atom entry.
     * 
     * @param feedEntry
     * @param itemClassType
     * @param itemXMLType
     * @param mediator
     * @return
     */
    // To-Change: org.apache.abdera.model.Entry --> com.google.gdata.data.Entry
    static Entry<Object, Object> entry(org.apache.abdera.model.Entry feedEntry,
                                       DataType<?> itemClassType,
                                       DataType<?> itemXMLType,
                                       Mediator mediator) {
        if (feedEntry != null) {
            if (itemClassType.getPhysical() == Item.class) {
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

                // To-change
                // Get feed information from feedEntry and create a new Entry
                // For GDtata, the corresponding methods are different such as
                // getTitle(), getContent(), getUpdated() and getLinks()
                // I am going to use Gdata Entry(com.google.gdata.data.Entry)
                // and modify accordingly

            } else {
                String key = null;
                if (feedEntry.getId() != null) {
                    key = feedEntry.getId().toString();
                }

                // Create the item from XML
                if (feedEntry.getContentElement().getElements().size() == 0) {
                    return null;
                }

                String value = feedEntry.getContent();
                Object data = mediator.mediate(value, itemXMLType, itemClassType, null);

                return new Entry<Object, Object>(key, data);

                // To-change
                // For GDtata, feedEntry.getContent() will return a
                // content(com.google.gdata.data.Content)
                // mediator.mediate needs to be changed accordingly
            }
        } else {
            return null;
        }
    }

    /**
     * Create an Atom entry for a key and item from a collection.
     * 
     * @param entry
     * @param itemClassType
     * @param itemXMLType
     * @param mediator
     * @param factory
     * @return
     */

    // To-Change: org.apache.abdera.model.Entry --> com.google.gdata.data.Entry
    static org.apache.abdera.model.Entry feedEntry(Entry<Object, Object> entry,
                                                   DataType<?> itemClassType,
                                                   DataType<?> itemXMLType,
                                                   Mediator mediator,
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

            // To-change
            // Get feed information from collection.item and create a new Gdata
            // Entry
            // For GDtata, the corresponding methods are quite different such as
            // setTitle(), setContent(), setUpdated(), addLink()
            //
            // For example:
            // entry.setTitle(new PlainTextConstruct("title_"));
            // entry.setContent(new PlainTextConstruct("content_"));
            // entry.setUpdated(DateTime.now());
            // entry.addHtmlLink("http://www.google.com", "languageType",
            // "title");
            //
            // I am going to use Gdata Entry(com.google.gdata.data.Entry) and
            // modify accordingly

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

            // To-change
            // Get feed information from XML data(item to XML first via
            // mediator)
            // and create a new Gdata Entry
            // Modify GData Entry set methods accordingly

        } else {
            return null;
        }
    }

    /**
     * Create a data item from an Gdata entry.
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
