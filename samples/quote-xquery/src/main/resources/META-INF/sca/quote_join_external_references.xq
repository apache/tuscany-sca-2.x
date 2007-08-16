declare namespace quoteJoin="scaservice:java/xquery.quote.ExternalReferencesQuoteJoin";

declare namespace quoteCalculator="scareference:java/xquery.quote.QuoteCalculator";

declare namespace priceQuoteProvider="scareference:java/xquery.quote.PriceQuoteProviderNodeInfo";
declare namespace availQuoteProvider="scareference:java/xquery.quote.AvailQuoteProviderNodeInfo";

declare variable $quoteCalculator external;

declare variable $priceQuoteProvider external;
declare variable $availQuoteProvider external;

declare function quoteJoin:joinPriceAndAvailQuotes($taxRate) {
let $priceQuoteDoc := priceQuoteProvider:providePriceQuote($priceQuoteProvider)
let $availQuoteDoc := availQuoteProvider:provideAvailQuote($availQuoteProvider, 'dummy')
return
<quote>
    <name>{ data($priceQuoteDoc/priceQuote/customerName) }</name>
    <address>{ concat($priceQuoteDoc/priceQuote/shipAddress/@street , ",", $priceQuoteDoc/priceQuote/shipAddress/@city ,",", fn:upper-case($priceQuoteDoc/priceQuote/shipAddress/@state) , ",", $priceQuoteDoc/priceQuote/shipAddress/@zip) }</address>
    {
        for $priceRequest in $priceQuoteDoc/priceQuote/priceRequests/priceRequest,
            $availRequest in $availQuoteDoc/availQuote/availRequest
        where data($priceRequest/widgetId) = data($availRequest/widgetId)
        return
            <quoteResponse>
                <widgetId>{ data($priceRequest/widgetId) }</widgetId>
                <unitPrice>{ data($priceRequest/price) }</unitPrice>
                <requestedQuantity>{ data($availRequest/requestedQuantity) }</requestedQuantity>
                <fillOrder>{ data($availRequest/quantityAvail) }</fillOrder>
                {
                    for $shipDate in $availRequest/shipDate
                    return
                        <shipDate>{ data($shipDate) }</shipDate>
                }
                <taxRate>{ $taxRate }</taxRate>
                <totalCost>{ quoteCalculator:calculateTotalPrice(
                				  $quoteCalculator,
                				  
                				  $taxRate,

                                  $availRequest/requestedQuantity,

                                  $priceRequest/price,

                                  $availRequest/quantityAvail) }</totalCost>
            </quoteResponse>
    }
    </quote>
};