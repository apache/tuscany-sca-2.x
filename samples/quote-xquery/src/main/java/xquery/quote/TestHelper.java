package xquery.quote;


import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import junit.framework.TestCase;

import org.example.avail.AvailFactory;
import org.example.avail.AvailQuote;
import org.example.avail.AvailRequest;
import org.example.price.PriceFactory;
import org.example.price.PriceQuote;
import org.example.price.PriceRequest;
import org.example.price.PriceRequests;
import org.example.price.ShipAddress;
import org.example.quote.Quote;
import org.example.quote.QuoteResponse;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.impl.HelperProvider;

public class TestHelper {

	public static AvailQuote buildAvailQuoteData() {
		AvailQuote availQuote = AvailFactory.INSTANCE.createAvailQuote();
		AvailRequest availRequest = AvailFactory.INSTANCE.createAvailRequest();
		availRequest.setWidgetId(BigInteger.valueOf(12));
		availRequest.setRequestedQuantity(10);
		availRequest.setQuantityAvail(true);
		availRequest.setShipDate("2003-03-22");
		availQuote.getAvailRequest().add(availRequest);
		
		availRequest = AvailFactory.INSTANCE.createAvailRequest();
		availRequest.setWidgetId(BigInteger.valueOf(134));
		availRequest.setRequestedQuantity(345);
		availRequest.setQuantityAvail(false);
		availRequest.setShipDate("BackOrder");
		availQuote.getAvailRequest().add(availRequest);
		
		availRequest = AvailFactory.INSTANCE.createAvailRequest();
		availRequest.setWidgetId(BigInteger.valueOf(211));
		availRequest.setRequestedQuantity(100);
		availRequest.setQuantityAvail(true);
		availRequest.setShipDate("2003-04-21");
		availQuote.getAvailRequest().add(availRequest);
		
		return availQuote;
	}
	
	public static PriceQuote buildPriceQuoteData() {
		PriceQuote priceQuote = PriceFactory.INSTANCE.createPriceQuote();
		priceQuote.setCustomerName("Acme Inc");
		
		ShipAddress shipAddress = PriceFactory.INSTANCE.createShipAddress();
		shipAddress.setStreet("12 Springs Rd");
		shipAddress.setCity("Morris Plains");
		shipAddress.setState("nj");
		shipAddress.setZip("07960");
		priceQuote.setShipAddress(shipAddress);
		
		PriceRequests priceRequests = PriceFactory.INSTANCE.createPriceRequests();
		PriceRequest priceRequest = PriceFactory.INSTANCE.createPriceRequest();
		priceRequest.setWidgetId(BigInteger.valueOf(12));
		priceRequest.setPrice(1.00f);
		priceRequests.getPriceRequest().add(priceRequest);
		
		priceRequest = PriceFactory.INSTANCE.createPriceRequest();
		priceRequest.setWidgetId(BigInteger.valueOf(134));
		priceRequest.setPrice(34.10f);
		priceRequests.getPriceRequest().add(priceRequest);
		
		priceRequest = PriceFactory.INSTANCE.createPriceRequest();
		priceRequest.setWidgetId(BigInteger.valueOf(211));
		priceRequest.setPrice(10.00f);
		priceRequests.getPriceRequest().add(priceRequest);
		
		priceQuote.setPriceRequests(priceRequests);
		
		return priceQuote;
	}
	
	public static void assertQuote(AvailQuote availQuote, PriceQuote priceQuote, Quote quote, float taxRate) {
		QuoteCalculatorImpl quoteCalculatorImpl = new QuoteCalculatorImpl();
		
		TestCase.assertEquals(priceQuote.getCustomerName(), quote.getName());
		ShipAddress shipAddress = priceQuote.getShipAddress();
		TestCase.assertEquals(shipAddress.getStreet()+","+shipAddress.getCity()+","+shipAddress.getState().toUpperCase()+","+shipAddress.getZip(), quote.getAddress());
		List availRequests = availQuote.getAvailRequest();
		List priceRequests = priceQuote.getPriceRequests().getPriceRequest();
		List quoteResponses = quote.getQuoteResponse();
		TestCase.assertEquals(availRequests.size(), priceRequests.size());
		TestCase.assertEquals(availRequests.size(), quoteResponses.size());
		
		for(int i=0; i<availRequests.size(); i++) {
			AvailRequest availRequest = (AvailRequest)availRequests.get(i);
			PriceRequest priceRequest = (PriceRequest)priceRequests.get(i);
			QuoteResponse quoteResponse = (QuoteResponse)quoteResponses.get(i);
			TestCase.assertEquals(availRequest.getWidgetId(), quoteResponse.getWidgetId());
			TestCase.assertEquals(priceRequest.getPrice(), quoteResponse.getUnitPrice());
			TestCase.assertEquals(availRequest.getRequestedQuantity(), quoteResponse.getRequestedQuantity());
			TestCase.assertEquals(availRequest.isQuantityAvail(), quoteResponse.isFillOrder());
			if(availRequest.getShipDate()==null) {
				TestCase.assertNull(quoteResponse.getShipDate());
			} else {
				TestCase.assertEquals(availRequest.getShipDate(), quoteResponse.getShipDate());
			}
			TestCase.assertEquals(taxRate, quoteResponse.getTaxRate());
			TestCase.assertEquals(quoteCalculatorImpl.calculateTotalPrice(taxRate, availRequest.getRequestedQuantity(), priceRequest.getPrice(), availRequest.isQuantityAvail()), quoteResponse.getTotalCost());
		}
	}
	
	public static void serializeToSystemOut(DataObject object, String name) {
		XMLHelper helper = HelperProvider.INSTANCE.xmlHelper();
		
		try {
			helper.save(object, null, name, System.out);
			System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
