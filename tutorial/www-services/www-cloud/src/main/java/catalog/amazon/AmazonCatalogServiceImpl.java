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

package catalog.amazon;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import catalog.CatalogService;
import catalog.util.XPathHelper;

/**
 * @version $Rev$ $Date$
 */
@Service(CatalogService.class)
public class AmazonCatalogServiceImpl implements CatalogService {
    private static final String AMAZON_TNS = "http://webservices.amazon.com/AWSECommerceService/2007-09-21";

    @Reference(name = "amazonFindItems")
    protected AmazonCatalogService itemSearch;
    
    @Property(name="AWSAccessKeyId", required=true)
    protected String accessKeyId;

    private Node amazonItemSearch(String keywords) throws Exception {
        Document doc = DOMHelper.newDocument();
        Element wrapper = doc.createElementNS(AMAZON_TNS, "ItemSearch");
        doc.appendChild(wrapper);
        Element key = doc.createElementNS(AMAZON_TNS, "AWSAccessKeyId");
        key.appendChild(doc.createTextNode(accessKeyId));
        wrapper.appendChild(key);

        Element request = doc.createElementNS(AMAZON_TNS, "Request");
        wrapper.appendChild(request);

        Element keywordsElement = doc.createElementNS(AMAZON_TNS, "Keywords");
        keywordsElement.appendChild(doc.createTextNode(keywords));
        request.appendChild(keywordsElement);

        Element searchIndex = doc.createElementNS(AMAZON_TNS, "SearchIndex");
        searchIndex.appendChild(doc.createTextNode("All"));
        request.appendChild(searchIndex);

        // System.out.println(new Node2String().transform(doc, null));
        Node result = itemSearch.ItemSearch(doc);
        return result;
    }

    public String[] get() {
        try {
            Node list = amazonItemSearch("Fruit");
            Map<String, String> map = new HashMap<String, String>();
            map.put("a", AMAZON_TNS);
            NodeList nodes =
                XPathHelper.selectNodes("//a:Item/a:ItemAttributes[a:ProductGroup='Grocery']/a:Title", list, map);
            String titles[] = new String[nodes.getLength()];
            System.out.println("Amazon catalog:");
            for (int i = 0; i < nodes.getLength(); i++) {
                titles[i] = nodes.item(i).getTextContent();
                System.out.println("[" + i + "]" + titles[i]);
            }

            return titles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
