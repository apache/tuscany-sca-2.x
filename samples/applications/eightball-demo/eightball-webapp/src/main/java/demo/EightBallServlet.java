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
package demo;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oasisopen.sca.annotation.Reference;

/**
 */
public class EightBallServlet extends HttpServlet {

    @Reference
    protected EightBall eightball;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String question = request.getParameter("question");
    	String answer = eightball.askQuestion(question);
        Writer out = response.getWriter();
        out.write("<html><head><title>The Magic Eight Ball</title></head><body>");
        out.write("<h2>The Magic Eight Ball</h2>");
        out.write("<p>You ask:");
        out.write("<br><strong>" + question + "</strong>");
        out.write("<p>Eight Ball says:");
        out.write("<br><strong>" + answer + "</strong>");
        out.write("</body></html>");
        out.flush();
        out.close();
    }
}
