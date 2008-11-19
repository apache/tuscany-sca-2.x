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
package org.apache.tuscany.sca.data.collection;

import java.util.Date;


/**
 * Represents a data item.
 * 
 * @version $Rev$ $Date$
 */
public class Item {

    private String title;
    private String contents;
    private String link;
    private String related;
    private String alternate;
    private Date date;
    
    /**
     * Constructs a new item.
     */
    public Item() {
    }

    /**
     * Constructs a new item.
     * @param title the item title
     * @param contents the item contents
     * @param link the item link to a web resource
     * @param related the item link to a related web resource
     * @param date the item date
     */
    public Item(String title, String contents, String link, String related, Date date) {
        this.title = title;
        this.contents = contents;
        this.link = link;
        this.related = related;
        this.date = date;
    }

    /**
     * Returns the item title.
     * @return the item title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the item title.
     * @param title the item title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the item contents
     * @return the item contents
     */
    public String getContents() {
        return contents;
    }

    /**
     * Sets the item contents
     * @param contents the item contents
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

    /**
     * Returns the item link to a web resource
     * @return the item link to a web resource
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the item link to a web resource
     * @param link the item link to a web resource
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Returns the item link to a related web resource
     * @return the item link to a related web resource
     */
    public String getRelated() {
        return related;
    }

    /**
     * Sets the item link to a related web resource.
     *
     * @param related the item link to a related web resource
     */
    public void setRelated(String related) {
        this.related = related;
    }

    /**
     * Returns the item link to an alternate web resource
     * @return the item link to an alternate web resource
     */
    public String getAlternate() {
        return alternate;
    }

    /**
     * Sets the item link to an alternate web resource.
     *
     * @param alternate the item link to an alternate web resource
     */
    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    /**
     * Returns the item date
     * @return the item date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the item date
     * @param date the item date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    
}
