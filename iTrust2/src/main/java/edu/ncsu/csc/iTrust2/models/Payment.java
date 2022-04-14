package edu.ncsu.csc.iTrust2.models;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.forms.PaymentForm;

/**
 * Class for a Payment. A Payment has an amount stored as a long, a date that
 * the payment was made, and a method that says how the payment was made.
 * Payments will always be associated with bills.
 *
 * @author Leah Whaley
 *
 */
@Entity
public class Payment extends DomainObject {

    /**
     * The id for this payment
     */
    @Id
    @GeneratedValue
    private Long          id;

    /**
     * The date this payment was made.
     */
    private ZonedDateTime date;

    /**
     * The amount paid for this payment.
     */
    private Long          amount;

    /**
     * How this payment was made.
     */
    private String        paymentMethod;

    /**
     * Empty constructor for Hibernate.
     */
    public Payment () {

    }

    /**
     * Creates a payment from a payment form
     *
     * @param form
     *            the form with the values of the payment
     */
    public Payment ( final PaymentForm form ) {
        setDate( ZonedDateTime.parse( form.getDate() ) );
        setAmount( form.getAmount() );
        setPaymentMethod( form.getPaymentMethod() );
    }

    /**
     * Gets the id of the payment.
     *
     * @return the id of the payment.
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of the payment to the parameter
     *
     * @param id
     *            the value to set the id to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the date this payment was made
     *
     * @return the date this payment was made
     */
    public ZonedDateTime getDate () {
        return date;
    }

    /**
     * Sets the date of this payment to the parameter.
     *
     * @param date
     *            the value to set the date to
     */
    public void setDate ( final ZonedDateTime date ) {
        this.date = date;
    }

    /**
     * Gets the amount of the payment
     *
     * @return the amount of the payment
     */
    public Long getAmount () {
        return amount;
    }

    /**
     * Sets the amount of the payment to the parameter. Throws an exception if
     * the amount is less than zero.
     *
     * @param amount
     *            the value to set the amount to
     */
    public void setAmount ( final Long amount ) {
        if ( amount > 0 ) {
            this.amount = amount;
        }
        else {
            throw new IllegalArgumentException( "Payment amount must be positive." );
        }
    }

    /**
     * Gets the way that this payment was made
     *
     * @return the payment method of the payment
     */
    public String getPaymentMethod () {
        return paymentMethod;
    }

    /**
     * Sets the payment method to the parameter. A valid payment method is
     * either cash, credit, insurance, or check. An exception is thrown if the
     * method is invalid.
     *
     * @param method
     *            the value to set the payment method to.
     */
    public void setPaymentMethod ( final String method ) {
        final boolean isCash = method.equalsIgnoreCase( "Cash" );
        final boolean isCredit = method.equalsIgnoreCase( "Credit" );
        final boolean isInsurance = method.equalsIgnoreCase( "Insurance" );
        final boolean isCheck = method.equalsIgnoreCase( "Check" );
        if ( isCash || isCredit || isInsurance || isCheck ) {
            this.paymentMethod = method;
        }
        else {
            throw new IllegalArgumentException( "Invalid payment method" );
        }
    }

}
