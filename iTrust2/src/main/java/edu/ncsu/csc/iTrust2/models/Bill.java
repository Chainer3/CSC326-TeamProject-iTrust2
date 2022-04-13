package edu.ncsu.csc.iTrust2.models;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Class for Bills. A bill has a total that is stored as a long, a boolean for
 * if it is paid or not, an OfficeVisit that it is attached to, and a list of
 * payments.
 *
 * @author Leah Whaley
 * @author Zach Harris
 *
 */
@Entity
@Table ( name = "Bill" )
public class Bill extends DomainObject {

    /** The number of days that must pass before a bill becomes delinquent. */
    private static final Long DAYS_UNTIL_DELINQUENCY = 60L;

    /**
     * The id for this bill.
     */
    @Id
    @GeneratedValue
    private Long              id;

    /**
     * The total amount due for this bill.
     */
    private Long              totalDue;

    /**
     * A flag that says if the bill has been paid or not.
     */
    private boolean           isPaid;

    /**
     * The OfficeVisit that this bill is paying for.
     */
    @OneToOne
    @JoinColumn ( name = "visit_id" )
    @JsonBackReference
    private OfficeVisit       visit;

    /**
     * The list of payment that have been made on this bill.
     */
    @OneToMany ( cascade = CascadeType.ALL )
    private List<Payment>     payments;

    /**
     * Empty constructor for Hibernate.
     */
    public Bill () {

    }

    /**
     * Constructs a bill from an OfficeVisit and fills all fields.
     *
     * @param visit
     *            the visit to get the values of the fields from.
     */
    public Bill ( final OfficeVisit visit ) {
        setIsPaid( false );
        setVisit( visit );
        setPayments( new ArrayList<Payment>() );
        Long due = (long) 0;
        for ( int i = 0; i < visit.getCptCodes().size(); i++ ) {
            due += visit.getCptCodes().get( i ).getCost();
        }
        setTotalDue( due );
        visit.setBill( this );
    }

    /**
     * Gets the id of this bill
     *
     * @return the id of the bill
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of this bill to the parameter
     *
     * @param id
     *            the value to set the id to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the total amount due for this bill
     *
     * @return the total amount due for this bill
     */
    public Long getTotalDue () {
        return totalDue;
    }

    /**
     * Sets the total amount due for this bill to the parameter
     *
     * @param total
     *            the value to set the total of this bill to
     */
    public void setTotalDue ( final Long total ) {
        this.totalDue = total;
    }

    /**
     * Gets if the bill is paid or not
     *
     * @return true if the bill is paid off and false otherwise
     */
    public boolean getIsPaid () {
        return isPaid;
    }

    /**
     * Sets the isPaid field of this bill to the parameter
     *
     * @param isPaid
     *            the value to set the isPaid field to
     */
    public void setIsPaid ( final boolean isPaid ) {
        this.isPaid = isPaid;
    }

    /**
     * Gets the office visit that this bill is attached to
     *
     * @return the office visit that this bill is for
     */
    public OfficeVisit getVisit () {
        return visit;
    }

    /**
     * Sets the office visit of this bill to the parameter
     *
     * @param visit
     *            the value to set the office visit of this bill to
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
     * Sets the list of payments for this bill to the parameter
     *
     * @param payments
     *            the value to set the list of payments to
     */
    public void setPayments ( final List<Payment> payments ) {
        this.payments = payments;
    }

    /**
     * Adds a payment to the list of payments for this bill. Throws an exception
     * if the payment is invalid or if the amount of the payment exceeds the
     * amount remaining on the bill.
     *
     * @param payment
     *            the payment to add to the list of payments for the bill.
     */
    public void addPayment ( final Payment payment ) {
        if ( payment != null ) {
            Long paid = (long) 0;
            for ( int i = 0; i < payments.size(); i++ ) {
                paid += payments.get( i ).getAmount();
            }

            final Long remainder = this.totalDue - paid;

            if ( payment.getAmount() < remainder ) {
                this.payments.add( payment );
            }
            else if ( payment.getAmount().equals( remainder ) ) {
                this.payments.add( payment );
                this.isPaid = true;
            }
            else {
                throw new IllegalArgumentException( "Tried to overpay." );
            }

        }
        else {
            throw new IllegalArgumentException( "Invalid payment." );
        }

    }

    /**
     * Gets the status of this bill.
     *
     * @return The status of this bill
     */
    public String getStatus () {
        if ( this.isPaid ) {
            return "Paid";
        }
        else if ( Duration.between( visit.getDate(), ZonedDateTime.now() ).toDays() > DAYS_UNTIL_DELINQUENCY ) {
            return "Delinquent";
        }
        else {
            return "Unpaid";
        }
    }

    /**
     * Gets the remaining balance of this bill.
     *
     * @return The remaining amount due on this bill.
     */
    public Long getBalance () {
        Long paid = 0L;
        for ( final Payment p : payments ) {
            paid += p.getAmount();
        }
        Long due = totalDue - paid;
        if ( due < 0L ) {
            due = 0L;
        }
        return due;
    }
}
