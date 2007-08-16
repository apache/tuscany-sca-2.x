declare namespace quoteJoin="scaservice:java/xquery.quote.QuoteJoin";

declare namespace quoteCalculator="scareference:java/xquery.quote.QuoteCalculator";

declare variable $quoteCalculator external;

declare function quoteJoin:joinPriceAndAvailQuotes($priceQuoteDoc, $availQuoteDoc, $taxRate) {
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