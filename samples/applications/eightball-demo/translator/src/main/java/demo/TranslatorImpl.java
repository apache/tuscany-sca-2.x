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

import java.util.Arrays;
import java.util.List;

public class TranslatorImpl implements Translator {

	  static List<String> english = Arrays.asList(new String[]{
	      "Signs point to yes.",
          "Yes.",
          "Reply hazy, try again.",
          "Without a doubt.",
          "My sources say no.",
          "As I see it, yes.",
          "You may rely on it.",
          "Concentrate and ask again.",
          "Outlook not so good.",
          "It is decidedly so.",
          "Better not tell you now.",
          "Very doubtful.",
          "Yes - definitely.",
          "It is certain.",
          "Cannot predict now.",
          "Most likely.",
          "Ask again later.",
          "My reply is no.",
          "Outlook good.",
          "Don't count on it.",
          "You are a donkey"});

	  static List<String> german = Arrays.asList(new String[]{
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
	          "Verlassen Sie sich nicht darauf.",
	          "Du bist ein Esel"});

	  public String toEnglish(String phrase) {
		int x = german.indexOf(phrase);
		String translatedPhrase;
		if (x == -1) {
			translatedPhrase = phrase; 
		} else {
			translatedPhrase = english.get(x); 
		}
		System.out.println("Translated " + phrase + " : " + translatedPhrase);
		return translatedPhrase; 
	}

	public String toGerman(String phrase) {
		int x = english.indexOf(phrase);
		String translatedPhrase;
		if (x == -1) {
			translatedPhrase = phrase; 
		} else {
			translatedPhrase = german.get(x); 
		}
		System.out.println("Translated " + phrase + " : " + translatedPhrase);
		return translatedPhrase; 
	}
}
