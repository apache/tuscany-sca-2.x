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

package com.tuscanyscatours.blog;

import java.util.Date;

/**
 * Bean for blog posts.
 */
public class BlogPost {

    private final String author;
    private final String title;
    private final String content;
    private final Date updated;
    private final String link;
    private final String related;

    public BlogPost(String author, String title, String content, Date updated, String link, String related) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.updated = updated;
        this.link = link;
        this.related = related;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getUpdated() {
        return updated;
    }

    public String getLink() {
        return link;
    }

    public String getRelated() {
        return related;
    }
}
