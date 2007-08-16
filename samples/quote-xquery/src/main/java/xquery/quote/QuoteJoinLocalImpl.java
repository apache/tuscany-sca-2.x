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
package xquery.quote;

import org.example.avail.AvailQuote;
import org.example.price.PriceQuote;
import org.example.quote.Quote;
import org.osoa.sca.annotations.Reference;

public class QuoteJoinLocalImpl implements QuoteJoinLocal {
	private QuoteJoin quoteJoin;
	private QuoteJoin quoteJoinWs;
	private PropertiesQuoteJoin propertiesQuoteJoin;
	private ExternalReferencesQuoteJoin externalServicesQuoteJoin;
	
	@Reference
	public void setQuoteJoin(QuoteJoin quoteJoin) {
		this.quoteJoin = quoteJoin;
	}
	
	@Reference
	public void setQuoteJoinWs(QuoteJoin quoteJoinWs) {
		this.quoteJoinWs = quoteJoinWs;
	}
	
	@Reference
	public void setPropertiesQuoteJoin(PropertiesQuoteJoin propertiesQuoteJoin) {
		this.propertiesQuoteJoin = propertiesQuoteJoin;
	}
	
	@Reference
	public void setExternalServicesQuoteJoin(ExternalReferencesQuoteJoin externalServicesQuoteJoin) {
		this.externalServicesQuoteJoin = externalServicesQuoteJoin;
	}

	public Quote joinPriceAndAvailQuotes(PriceQuote priceQuote, AvailQuote availQuote, float taxRate) {
		return quoteJoin.joinPriceAndAvailQuotes(priceQuote, availQuote, taxRate);
	}
	
	public Quote joinPriceAndAvailQuotesWs(PriceQuote priceQuote, AvailQuote availQuote, float taxRate) {
		return quoteJoinWs.joinPriceAndAvailQuotes(priceQuote, availQuote, taxRate);
	}

	public Quote joinPriceAndAvailQuotes(float taxRate) {
		return externalServicesQuoteJoin.joinPriceAndAvailQuotes(taxRate);
	}

	public Quote joinPriceAndAvailQuotes() {
		return propertiesQuoteJoin.joinPriceAndAvailQuotes(); 
	}
}
