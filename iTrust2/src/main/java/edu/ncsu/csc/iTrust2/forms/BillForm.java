package edu.ncsu.csc.iTrust2.forms;

import java.util.List;

import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Payment;

/**
 * Form used for bills. Has all of the same fields as a Bill.
 *
 * @author Leah Whaley
 *
 */
public class BillForm {

    /**
     * The total amount due for the bill.
     */
    private Long          totalDue;

    /**
     * If the bill is paid off or not.
     */
    private boolean       isPaid;

    /**
     * The office visit associated with this bill
     */
    private OfficeVisit   visit;

    /**
     * The list of payments made on this bill
     */
    private List<Payment> payments;

    /**
     * Empty constructor for Hibernate
     */
    public BillForm () {

    }

    /**
     * Creates a BillForm from a Bill. Sets all fields to the value of the
     * fields of the Bill.
     *
     * @param bill
     *            the Bill to get the values from
     */
    public BillForm ( final Bill bill ) {
        setTotalDue( bill.getTotalDue() );
        setIsPaid( bill.getIsPaid() );
        setVisit( bill.getVisit() );
        setPayments( bill.getPayments() );
    }

    /**
     * Gets the total amount due for the bill
     *
     * @return the total amount due
     */
    public Long getTotalDue () {
        return totalDue;
    }

    /**
     * Sets the total amount due to the parameter
     *
     * @param total
     *            the value to set the total due to
     */
    public void setTotalDue ( final Long total ) {
        this.totalDue = total;
    }

    /**
     * Gets if the bill has been paid or not
     *
     * @return true if the bill has been paid and false otherwise
     */
    public boolean getIsPaid () {
        return isPaid;
    }

    /**
     * Sets if the bill has been paid to the parameter
     *
     * @param isPaid
     *            the value to set the isPaid field to
     */
    public void setIsPaid ( final boolean isPaid ) {
        this.isPaid = isPaid;
    }

    /**
     * Gets the office visit associated with this bill
     *
     * @return the office visit for this bill
     */
    public OfficeVisit getVisit () {
        return visit;
    }

    /**
     * Sets the office visit for this bill to the parameter
     *
     * @param visit
     *            the value to set the office visit field to
     */
    public void setVisit ( final OfficeVisit visit ) {
        this.visit = visit;
    }

    /**
     * Gets the list of payments made on this bill
     *
     * @return the list of payments for this bill
     */
    public List<Payment> getPayments () {
        return payments;
    }

    /**
     * Sets the list of payments to the parameter
     *
     * @param payments
     *            the value to set the list of payments to
     */
    public void setPayments ( final List<Payment> payments ) {
        this.payments = payments;
    }
}
