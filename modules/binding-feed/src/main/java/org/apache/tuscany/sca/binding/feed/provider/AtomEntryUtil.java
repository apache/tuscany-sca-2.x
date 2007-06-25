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

package org.apache.tuscany.sca.binding.feed.provider;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedInput;
import com.sun.syndication.io.WireFeedOutput;

/**
 * Utility methods to read/write Atom entries.
 *
 * @version $Rev$ $Date$
 */
class AtomEntryUtil {

    /**
     * Read an Atom entry
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws JDOMException
     * @throws FeedException
     * @throws IllegalArgumentException
     */
    static Entry readEntry(String feedType, Reader reader) throws JDOMException, IOException, IllegalArgumentException,
        FeedException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        root.detach();
        Feed feed = new Feed();
        feed.setFeedType(feedType);
        WireFeedOutput wireFeedOutput = new WireFeedOutput();
        document = wireFeedOutput.outputJDom(feed);
        document.getRootElement().addContent(root);
        WireFeedInput input = new WireFeedInput();
        feed = (Feed)input.build(document);
        Entry entry = (Entry)feed.getEntries().get(0);
        return entry;
    }

    /**
     * Write an Atom entry.
     * 
     * @param entry
     * @param response
     * @throws FeedException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws ServletException
     */
    static void writeEntry(Entry entry, String feedType, Writer writer) throws IllegalArgumentException, FeedException,
        IOException {
        Feed feed = new Feed();
        feed.setFeedType(feedType);
        List<Entry> entries = new ArrayList<Entry>();
        entries.add(entry);
        feed.setEntries(entries);

        WireFeedOutput wireFeedOutput = new WireFeedOutput();
        Document document = wireFeedOutput.outputJDom(feed);
        Element root = document.getRootElement();
        Element element = (Element)root.getChildren().get(0);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        outputter.output(element, writer);
    }

}
