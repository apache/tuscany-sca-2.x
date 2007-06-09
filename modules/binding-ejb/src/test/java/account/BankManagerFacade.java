/**
 * 
 * (C) Copyright IBM Corp. 2004, 2006  All Rights Reserved
 * 
 */
package account;

import org.osoa.sca.annotations.Remotable;

/**
 * 
 * Compatible EJB interface
 *
 */
@Remotable
public interface BankManagerFacade   
{
    public java.lang.Double getAccountBalance( java.lang.String accountNo );
    public void changeAccountBalance( java.lang.String accountNo,java.lang.Double balance );
}