package loanapplication.client.impl;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import loanapplication.client.ClientService;
import loanapplication.message.Application;
import loanapplication.message.LoanPackage;
import loanapplication.message.impl.ApplicationImpl;
import loanapplication.provider.LoanService;
import loanapplication.provider.LoanServiceCallback;

/**
 * Demonstrates a client to the conversational loan service that receives a set of callbacks. This component
 * implementation is configured as a Tuscany "launched" component type. The standalone launcher will invoke  {@link
 * #main(String[])} when the application is started.
 */
@Service(ClientService.class)
@Scope("COMPOSITE")
public class ClientServiceImpl implements LoanServiceCallback {
    private LoanService loanService;

    /**
     * Instantiates a new client with a reference to the loan service
     *
     * @param loanService the loan service
     */
    public ClientServiceImpl(@Reference(name = "loanService")LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * The method invoked to initiate a new application process.
     *
     * @param args startup parameters
     * @return the return code
     */
    public int main(String[] args) {
        System.out.println("Client applying for loan");
        Application app = new ApplicationImpl();
        app.setCustomerID("12345");
        app.setAmount(100000);
        app.setTerm(30);
        app.setType(Application.FIXED);
        loanService.apply(app);
        return 1;
    }

    public void creditScoreResult(int code) {
        System.out.println("Callback: credit score was " + code);
    }

    public void applicationResult(int code) {
        if (code == APPROVED) {
            System.out.println("Callback: the loan has been approved");
            System.out.println("Client securing loan");
            loanService.secureLoan();
        } else {
            System.out.println("Callback:, the loan has been declined");
        }
    }

    public void loanPackage(LoanPackage loanPackage) {
        System.out.println("Callback: loan package received");
    }
}
