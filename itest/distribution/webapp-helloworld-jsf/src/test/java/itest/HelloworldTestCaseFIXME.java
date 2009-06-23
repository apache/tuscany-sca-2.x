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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

/**
 * TODO: This doesn't work yet, I can't find the hello response in the
 * the response page from the button click. Not sure if thats just
 * looking in the wrong place or the button click is not working
 * Also the Cargo maven plugin has a problem finding the el-api when using Jetty 
 */
public class HelloworldTestCaseFIXME {

    @Test
    public void testA() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        HtmlPage page = (HtmlPage)new WebClient().getPage("http://localhost:8085/helloworld-jsf");

        HtmlForm form = page.getFormByName("mainForm");

        HtmlInput textField = form.getInputByName("mainForm:name");
        textField.setValueAttribute("root");

        Iterator i = form.getChildIterator();
        i.next();
        HtmlInput button = (HtmlInput)i.next();

        HtmlPage page2 = (HtmlPage)button.click();

        Iterator<?> ss = page2.getAllHtmlChildElements();
        ss.next();
        ss.next();
        ss.next();
        ss.next();
        HtmlForm form2 = (HtmlForm)ss.next();

        Object oow = form2.getFirstChild();
        System.out.println(oow);
//        assertTrue(p.asText().startsWith("Hello Petra"));
    }

}
