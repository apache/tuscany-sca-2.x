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

package itest;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 */
public class HelloworldTestCase {

    @Test
    public void testA() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        HtmlPage page = (HtmlPage)new WebClient().getPage("http://localhost:8085/helloworld-servlet");

        HtmlForm form = (HtmlForm) page.getForms().get(0);

        HtmlInput textField = form.getInputByName("name");
        textField.setValueAttribute("petra");

        HtmlButton button = (HtmlButton) form.getButtonsByName("submit").get(0);

        Object foo = button.click();
        
        // TODO: something up with getting the response but this works:
        UnexpectedPage p = (UnexpectedPage) foo;
        String s = new String(p.getWebResponse().getResponseBody());

        assertTrue(s.endsWith("</strong>Hello petra</body></html>"));
    }

}
