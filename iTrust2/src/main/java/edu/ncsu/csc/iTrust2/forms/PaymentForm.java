package edu.ncsu.csc.iTrust2.forms;

import edu.ncsu.csc.iTrust2.models.Payment;

/**
 * Form used for Payments. Has all the same fields as a Payment.
 *
 * @author Leah Whaley
 *
 */
public class PaymentForm {

    /**
     * The date this payment was made.
     */
    private String date;

    /**
     * The amount of this payment
     */
    private Long   amount;

    /**
     * How this payment was made.
     */
    private String paymentMethod;

    /**
     * Empty constructor for Hibernate
     */
    public PaymentForm () {

    }

    /**
     * Creates a PaymentForm from a payment
     *
     * @param payment
     *            the payment to get the values for the form from
     */
    public PaymentForm ( final Payment payment ) {
        setDate( payment.getDate().toString() );
        setAmount( payment.getAmount() );
        setPaymentMethod( payment.getPaymentMethod() );
    }

    /**
     * Gets the date that this payment was made
     *
     * @return the date of this payment
     */
    public String getDate () {
        return date;
    }

    /**
     * Sets the date of this payment to the parameter
     *
     * @param date
     *            the value to set the date to
     */
    public void setDate ( final String date ) {
        this.date = date;
    }

    /**
     * Gets the amount of this payment
     *
     * @return the amount of this payment
     */
    public Long getAmount () {
        return amount;
    }

    /**
     * Sets the amount of this payment to the parameter
     *
     * @param amount
     *            the value to set the amount to
     */
    public void setAmount ( final Long amount ) {
        this.amount = amount;
    }

    /**
     * Gets the way that this payment was made
     *
     * @return the method for this payment
     */
    public String getPaymentMethod () {
        return paymentMethod;
    }

    /**
     * Sets the method for this payment to the parameter. A valid method is
     * either cash, credit, insurance or check. Any other strings are invalid
     * and throw exceptions.
     *
     * @param method
     *            the value to set the method of this payment to
     */
    public void setPaymentMethod ( final String method ) {
        final boolean isCash = method.equalsIgnoreCase( "Cash" );
        final boolean isCredit = method.equalsIgnoreCase( "Credit" );
        final boolean isInsurance = method.equalsIgnoreCase( "Insurance" );
        final boolean isCheck = method.equalsIgnoreCase( "Check" );
        if ( isCash || isCredit || isInsurance || isCheck ) {
            this.paymentMethod = method;
        }
    }
}
