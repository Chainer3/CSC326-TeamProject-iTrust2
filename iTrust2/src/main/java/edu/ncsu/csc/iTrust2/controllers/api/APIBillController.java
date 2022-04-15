package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.PaymentService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with bills and payments. Exposes
 * functionality to view bills, their status, and their payments as well as
 * adding new payments.
 *
 * @author Zach Harris
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIBillController extends APIController {

    /** LoggerUtil */
    @Autowired
    private LoggerUtil         loggerUtil;

    /** Bill service */
    @Autowired
    private BillService        billService;

    /** Payment service */
    @Autowired
    private PaymentService     paymentService;

    /** OfficeVisit service */
    @Autowired
    private OfficeVisitService officeVisitService;

    /** User service */
    @Autowired
    private UserService        userService;

    /**
     * Gets the specified Bill.
     *
     * @param id
     *            The Bill to get.
     * @return The requested Bill.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/bills/{id}" )
    public ResponseEntity getBill ( @PathVariable final Long id ) {
        final Bill b = billService.findById( id );

        if ( b == null ) {
            loggerUtil.log( TransactionType.BILL_VIEW, LoggerUtil.currentUser(), "Failed to find bill with id " + id );
            return new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.BILL_VIEW, LoggerUtil.currentUser(), "Viewed bill " + id );
            return new ResponseEntity( b, HttpStatus.OK );
        }
    }

    /**
     * Gets all bills.
     *
     * @return A list of all bills.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/bills/" )
    public List<Bill> getBills () {
        // Return all bills in system
        loggerUtil.log( TransactionType.BILL_VIEW, LoggerUtil.currentUser(), "User viewed a list of all bills" );
        return billService.findAll();
    }

    /**
     * Gets a bill's status.
     *
     * @param id
     *            The Bill to get.
     * @return The status of the bill.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/bills/{id}/status" )
    public ResponseEntity getBillStatus ( @PathVariable final Long id ) {
        final Bill b = billService.findById( id );

        if ( b == null ) {
            loggerUtil.log( TransactionType.BILL_VIEW_STATUS, LoggerUtil.currentUser(),
                    "Failed to find bill with id " + id );
            return new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.BILL_VIEW_STATUS, LoggerUtil.currentUser(),
                    "Viewed bill " + id + " status" );
            return new ResponseEntity( b.getStatus(), HttpStatus.OK );
        }
    }

    /**
     * Gets a bill's balance.
     *
     * @param id
     *            The Bill to get.
     * @return The balance of the bill.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING', 'ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/bills/{id}/balance" )
    public ResponseEntity getBillBalance ( @PathVariable final Long id ) {
        final Bill b = billService.findById( id );

        if ( b == null ) {
            loggerUtil.log( TransactionType.BILL_VIEW_BALANCE, LoggerUtil.currentUser(),
                    "Failed to find bill with id " + id );
            return new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.BILL_VIEW_BALANCE, LoggerUtil.currentUser(),
                    "Viewed bill " + id + " balance" );
            return new ResponseEntity( b.getBalance(), HttpStatus.OK );
        }
    }

    /**
     * Gets a bill's payments.
     *
     * @param id
     *            The Bill to get.
     * @return The payments on the bill.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/bills/{id}/payments" )
    public ResponseEntity getBillPayments ( @PathVariable final Long id ) {
        final Bill b = billService.findById( id );

        if ( b == null ) {
            loggerUtil.log( TransactionType.BILL_VIEW_PAYMENTS, LoggerUtil.currentUser(),
                    "Failed to find bill with id " + id );
            return new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.BILL_VIEW_PAYMENTS, LoggerUtil.currentUser(),
                    "Viewed bill " + id + " payments" );
            return new ResponseEntity( b.getPayments(), HttpStatus.OK );
        }
    }

    /**
     * Gets a patient's bills.
     *
     * @param username
     *            The user to query.
     * @return The bills of the specified patient.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/patients/{username}/bills" )
    public ResponseEntity getPatientBills ( @PathVariable final String username ) {
        final User user = userService.findByName( username );
        if ( user == null ) {
            loggerUtil.log( TransactionType.VIEW_PATIENT_BILLS, LoggerUtil.currentUser(),
                    "Failed to find patient " + username );
            return new ResponseEntity( errorResponse( "No patient found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }

        final List<OfficeVisit> officeVisits = officeVisitService.findByPatient( user );
        final List<Bill> bills = new LinkedList<Bill>();
        for ( final OfficeVisit visit : officeVisits ) {
            if ( visit.getBill() != null ) {
                bills.add( visit.getBill() );
            }
        }
        loggerUtil.log( TransactionType.VIEW_PATIENT_BILLS, LoggerUtil.currentUser(),
                "Viewed bills of patient " + username );
        return new ResponseEntity( bills, HttpStatus.OK );
    }

    /**
     * Gets the currently signed in patient's bills.
     *
     * @return The bills of the specified patient.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/patient/bills" )
    public List<Bill> getUserBills () {
        final User user = userService.findByName( LoggerUtil.currentUser() );
        final List<OfficeVisit> officeVisits = officeVisitService.findByPatient( user );
        final List<Bill> bills = new LinkedList<Bill>();
        for ( final OfficeVisit visit : officeVisits ) {
            if ( visit.getBill() != null ) {
                bills.add( visit.getBill() );
            }
        }
        loggerUtil.log( TransactionType.PATIENT_VIEW_BILLS, LoggerUtil.currentUser(), "Patient viewed bills" );
        return bills;
    }

    /**
     * Gets one of the currently signed in patient's bills.
     *
     * @return The bill of the specified patient.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/patient/bills/{id}" )
    public ResponseEntity getUserBill ( @PathVariable final Long id ) {
        final User user = userService.findByName( LoggerUtil.currentUser() );
        final List<OfficeVisit> officeVisits = officeVisitService.findByPatient( user );
        final List<Bill> bills = new LinkedList<Bill>();
        for ( final OfficeVisit visit : officeVisits ) {
            if ( visit.getBill() != null ) {
                bills.add( visit.getBill() );
            }
        }
        Bill bill = null;
        for ( int i = 0; i < bills.size(); i++ ) {
            if ( bills.get( i ).getId().equals( id ) ) {
                bill = bills.get( i );
            }
        }
        loggerUtil.log( TransactionType.PATIENT_VIEW_BILLS, LoggerUtil.currentUser(), "Patient viewed bill" );
        return new ResponseEntity( bill, HttpStatus.OK );
    }

    /**
     * Gets a bill's status for the current user.
     *
     * @param id
     *            The Bill to get.
     * @return The status of the bill.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/patient/bills/{id}/status" )
    public ResponseEntity getUserBillStatus ( @PathVariable final Long id ) {
        final Bill b = billService.findById( id );
        final User user = userService.findByName( LoggerUtil.currentUser() );
        if ( b == null || b.getVisit().getPatient() != user ) {
            loggerUtil.log( TransactionType.BILL_VIEW_STATUS, LoggerUtil.currentUser(),
                    "Failed to find bill with id " + id );
            return new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.BILL_VIEW_STATUS, LoggerUtil.currentUser(),
                    "Viewed bill " + id + " status" );
            return new ResponseEntity( b.getStatus(), HttpStatus.OK );
        }
    }

    /**
     * Gets a bill's balance for the current user.
     *
     * @param id
     *            The Bill to get.
     * @return The balance of the bill.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/patient/bills/{id}/balance" )
    public ResponseEntity getUserBillBalance ( @PathVariable final Long id ) {
        final Bill b = billService.findById( id );
        final User user = userService.findByName( LoggerUtil.currentUser() );
        if ( b == null || b.getVisit().getPatient() != user ) {
            loggerUtil.log( TransactionType.BILL_VIEW_BALANCE, LoggerUtil.currentUser(),
                    "Failed to find bill with id " + id );
            return new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.BILL_VIEW_BALANCE, LoggerUtil.currentUser(),
                    "Viewed bill " + id + " balance" );
            return new ResponseEntity( b.getBalance(), HttpStatus.OK );
        }
    }

    /**
     * Gets a bill's payments for the current user.
     *
     * @param id
     *            The Bill to get.
     * @return The payments on the bill.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/patient/bills/{id}/payments" )
    public ResponseEntity getUserBillPayments ( @PathVariable final Long id ) {
        final Bill b = billService.findById( id );
        final User user = userService.findByName( LoggerUtil.currentUser() );
        if ( b == null || b.getVisit().getPatient() != user ) {
            loggerUtil.log( TransactionType.BILL_VIEW_PAYMENTS, LoggerUtil.currentUser(),
                    "Failed to find bill with id " + id );
            return new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.BILL_VIEW_PAYMENTS, LoggerUtil.currentUser(),
                    "Viewed bill " + id + " payments" );
            return new ResponseEntity( b.getPayments(), HttpStatus.OK );
        }
    }

    /**
     * Adds a new payment to the specified bill.
     *
     * @param form
     *            The submission data form used to create the payment.
     * @return The response to the request.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @PostMapping ( BASE_PATH + "/bills/{id}/payments" )
    public ResponseEntity addPayment ( @PathVariable final Long id, @RequestBody final PaymentForm form ) {
        try {
            final Payment p = paymentService.build( form );
            paymentService.save( p );
            final Bill b = billService.findById( id );

            if ( b == null ) {
                loggerUtil.log( TransactionType.BILL_ADD_PAYMENT, LoggerUtil.currentUser(),
                        "Failed to find bill with id " + id );
                return new ResponseEntity( errorResponse( "No Bill found for id " + id ), HttpStatus.NOT_FOUND );
            }
            else {
                try {
                    b.addPayment( p );
                    loggerUtil.log( TransactionType.BILL_ADD_PAYMENT, LoggerUtil.currentUser(),
                            "Added payment to bill " + id );
                    return new ResponseEntity( p, HttpStatus.OK );
                }
                catch ( final IllegalArgumentException e ) {
                    loggerUtil.log( TransactionType.BILL_ADD_PAYMENT, LoggerUtil.currentUser(),
                            "Failed to add payment" );
                    return new ResponseEntity( errorResponse( "Could not create payment for bill: " + e.getMessage() ),
                            HttpStatus.BAD_REQUEST );
                }
            }
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.BILL_ADD_PAYMENT, LoggerUtil.currentUser(), "Failed to add payment" );
            return new ResponseEntity( errorResponse( "Could not create payment for bill: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
