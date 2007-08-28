declare namespace quoteJoin="scaservice:java/xquery.quote.ExternalReferencesQuoteJoin";

declare namespace quoteCalculator="scareference:java/xquery.quote.QuoteCalculator";

declare namespace priceQuoteProvider="scareference:java/xquery.quote.PriceQuoteProviderNodeInfo";
declare namespace availQuoteProvider="scareference:java/xquery.quote.AvailQuoteProviderNodeInfo";

declare namespace quo="http://www.example.org/quote";

declare variable $quoteCalculator external;

declare variable $priceQuoteProvider external;
declare variable $availQuoteProvider external;

declare function quoteJoin:joinPriceAndAvailQuotes($taxRate) {
let $priceQuoteDoc := priceQuoteProvider:providePriceQuote($priceQuoteProvider)
let $availQuoteDoc := availQuoteProvider:provideAvailQuote($availQuoteProvider, 'dummy')
return
<quo:quote>
    <quo:name>{ data($priceQuoteDoc/priceQuote/customerName) }</quo:name>
    <quo:address>{ concat($priceQuoteDoc/priceQuote/shipAddress/@street , ",", $priceQuoteDoc/priceQuote/shipAddress/@city ,",", fn:upper-case($priceQuoteDoc/priceQuote/shipAddress/@state) , ",", $priceQuoteDoc/priceQuote/shipAddress/@zip) }</quo:address>
    {
        for $priceRequest in $priceQuoteDoc/priceQuote/priceRequests/priceRequest,
            $availRequest in $availQuoteDoc/availQuote/availRequest
        where data($priceRequest/widgetId) = data($availRequest/widgetId)
        return
            <quo:quoteResponse>
                <quo:widgetId>{ data($priceRequest/widgetId) }</quo:widgetId>
                <quo:unitPrice>{ data($priceRequest/price) }</quo:unitPrice>
                <quo:requestedQuantity>{ data($availRequest/requestedQuantity) }</quo:requestedQuantity>
                <quo:fillOrder>{ data($availRequest/quantityAvail) }</quo:fillOrder>
                {
                    for $shipDate in $availRequest/shipDate
                    return
                        <quo:shipDate>{ data($shipDate) }</quo:shipDate>
                }
                <quo:taxRate>{ $taxRate }</quo:taxRate>
                <quo:totalCost>{ quoteCalculator:calculateTotalPrice(
                				  $quoteCalculator,
                				  
                				  $taxRate,

                                  $availRequest/requestedQuantity,

                                  $priceRequest/price,

                                  $availRequest/quantityAvail) }</quo:totalCost>
            </quo:quoteResponse>
    }
    </quo:quote>
};
