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

import java.util.Random;
import java.lang.Double;
import java.util.Date;

public class EightBallImpl implements EightBall {

  static String answers[] = {
      "Zeichen zeigen auf \"Ja\".",
      "Ja.",
      "Antwort unklar, versuchen Sie es erneut.",
      "Ohne Zweifel.",
      "Meine Quellen sagen nein.",
      "Wie ich es sehe, ja.",
      "Sie koennen sich darauf verlassen.",
      "Konzentrieren Sie sich und fragen Sie erneut.",
      "Aussichten unguenstig.",
      "Auf alle Faelle, ja.",
      "Es ist besser, es Ihnen jetzt nicht zu sagen.",
      "Sehr zweifelhaft.",
      "Ja - auf jeden Fall.",
      "Es ist sicher.",
      "Kann jetzt nicht vorhergesagt werden.",
      "Höchstwahrscheinlich.",
      "Fragen Sie später noch einmal.",
      "Meine Antwort ist nein.",
      "Aussichten gut.",
      "Verlassen Sie sich nicht darauf."};

    public String askQuestion(String question) {
        String answer;
    	if ("1+2".equals(question)) {
    		answer = "3"; 
    	} else {
            Random r = new Random(new Date().getTime());
            Double d = new Double((r.nextDouble() * 20) - 1);
            answer = new String(answers[d.intValue()]);
    	}
		System.out.println("EightBall answer: " + answer);
		return answer;
    }

}
