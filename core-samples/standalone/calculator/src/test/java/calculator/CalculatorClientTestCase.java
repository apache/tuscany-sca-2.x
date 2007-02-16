package calculator;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class CalculatorClientTestCase extends TestCase {
    private CalculatorClient client;
    private CalculatorService mockService;

    public void testAdd() throws Exception {
        double result = 5;
        EasyMock.expect(mockService.add(2, 3)).andReturn(result);
        EasyMock.replay(mockService);
        assertEquals(result, client.main("add", "2", "3"));
        EasyMock.verify(mockService);
    }

    protected void setUp() throws Exception {
        super.setUp();
        client = new CalculatorClient();
        mockService = EasyMock.createMock(CalculatorService.class);
        client.setCalculatorService(mockService);
    }
}
