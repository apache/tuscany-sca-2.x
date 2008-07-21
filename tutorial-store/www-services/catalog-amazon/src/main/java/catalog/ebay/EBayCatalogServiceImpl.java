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

package catalog.ebay;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.databinding.impl.DOMHelper;
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
public class EBayCatalogServiceImpl implements CatalogService {
    private static final String EBAY_TNS = "urn:ebay:apis:eBLBaseComponents";

    @Reference(name = "eBayFindItems")
    protected EBayCatalogService findItems;

    @Reference(name = "eBayGetUserProfile")
    protected EBayCatalogService getUserProfile;

    private Node eBayFindItems(String keywords, int maxEntries) throws Exception {
        Document doc = DOMHelper.newDocument();
        Element request = doc.createElementNS(EBAY_TNS, "FindItemsRequest");
        doc.appendChild(request);
        Element keywordsElement = doc.createElementNS(EBAY_TNS, "QueryKeywords");
        keywordsElement.appendChild(doc.createTextNode(keywords));
        Element maxEntriesElement = doc.createElementNS(EBAY_TNS, "MaxEntries");
        maxEntriesElement.appendChild(doc.createTextNode(String.valueOf(maxEntries)));
        Element messageIDElement = doc.createElementNS(EBAY_TNS, "MessageID");
        messageIDElement.appendChild(doc.createTextNode("001"));
        request.appendChild(maxEntriesElement);
        request.appendChild(keywordsElement);
        request.appendChild(messageIDElement);
        // System.out.println(new Node2String().transform(doc, null));
        Node result = findItems.FindItems(doc);
        return result;
    }

    private Node eBayGetUserProfile(String userID) throws Exception {
        Document doc = DOMHelper.newDocument();
        Element req = doc.createElementNS(EBAY_TNS, "GetUserProfileRequest");
        doc.appendChild(req);
        Element user = doc.createElementNS(EBAY_TNS, "UserID");
        user.appendChild(doc.createTextNode(userID));
        Element msgID = doc.createElementNS(EBAY_TNS, "MessageID");
        msgID.appendChild(doc.createTextNode("001"));
        req.appendChild(user);
        // System.out.println(new Node2String().transform(doc, null));
        Node result = getUserProfile.GetUserProfile(doc);
        return result;
    }

    public String[] get() {
        try {
            Node items = eBayFindItems("Fruit", 10);
            Map<String, String> map = new HashMap<String, String>();
            map.put("e", EBAY_TNS);
            NodeList nodes = XPathHelper.selectNodes("//e:Item/e:Title", items, map);
            String[] titles = new String[nodes.getLength()];
            System.out.println("eBay catalog:");
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
