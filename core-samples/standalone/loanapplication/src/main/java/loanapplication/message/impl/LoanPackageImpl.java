package loanapplication.message.impl;

import java.io.Serializable;

import loanapplication.message.LoanPackage;

/**
 * A simple implementation of a LoanPackage
 */
public class LoanPackageImpl implements LoanPackage, Serializable {
    private static final long serialVersionUID = 51755060138169723L;
    private int type;
    private float amount;
    private float rate;
    private int term;
    private String loanNumber;

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }
}
