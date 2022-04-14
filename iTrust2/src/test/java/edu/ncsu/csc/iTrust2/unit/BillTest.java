package edu.ncsu.csc.iTrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import javax.transaction.Transactional;

import org.junit.Test;

import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Payment;

public class BillTest {

    @Test
    @Transactional
    public void testValidBill () {
        final OfficeVisit visit1 = new OfficeVisit();

        final Bill bill1 = new Bill();
        bill1.setIsPaid( false );
        bill1.setPayments( new ArrayList<Payment>() );
        bill1.setTotalDue( (long) 1000 );
        bill1.setVisit( visit1 );

        assertFalse( bill1.getIsPaid() );
        assertEquals( 0, bill1.getPayments().size() );
        assertEquals( 1000, (long) bill1.getTotalDue() );
        assertEquals( visit1, bill1.getVisit() );

        final OfficeVisit visit2 = new OfficeVisit();
        final ArrayList<CPTCode> codes = new ArrayList<CPTCode>();

        final CPTCode code1 = new CPTCode();
        code1.setCode( 99202 );
        code1.setDescription( "for office visits of 15-29 minutes" );
        code1.setCost( 7500 );
        code1.setVersion( 1 );
        code1.setIsArchived( false );
        code1.setTimeRangeMin( 15 );
        code1.setTimeRangeMax( 29 );

        final CPTCode code2 = new CPTCode();
        code2.setCode( 99205 );
        code2.setDescription( "for office visits of 60-74 minutes" );
        code2.setCost( 25000 );
        code2.setVersion( 1 );
        code2.setIsArchived( false );
        code2.setTimeRangeMin( 60 );
        code2.setTimeRangeMax( 74 );

        codes.add( code1 );
        codes.add( code2 );
        visit2.setCptCodes( codes );

        final Bill bill2 = new Bill( visit2 );
        visit2.setBill( bill2 );
        assertFalse( bill2.getIsPaid() );
        assertEquals( 0, bill2.getPayments().size() );
        assertEquals( 32500, (long) bill2.getTotalDue() );
        assertEquals( visit2, bill2.getVisit() );
        assertEquals( bill2, visit2.getBill() );

        final BillForm form1 = new BillForm( bill2 );
        assertFalse( form1.getIsPaid() );
        assertEquals( 0, form1.getPayments().size() );
        assertEquals( 32500, (long) form1.getTotalDue() );
        assertEquals( visit2, form1.getVisit() );

    }

    @Test
    @Transactional
    public void testValidPayments () {
        final Payment payment1 = new Payment();
        payment1.setAmount( (long) 10000 );
        final ZonedDateTime date = ZonedDateTime.now();
        payment1.setDate( date );
        payment1.setPaymentMethod( "Cash" );

        assertEquals( 10000, (long) payment1.getAmount() );
        assertEquals( date, payment1.getDate() );
        assertEquals( "Cash", payment1.getPaymentMethod() );

        final PaymentForm form1 = new PaymentForm( payment1 );
        assertEquals( 10000, (long) form1.getAmount() );
        assertEquals( date.toString(), form1.getDate() );
        assertEquals( "Cash", form1.getPaymentMethod() );

        final PaymentForm form2 = new PaymentForm();
        form2.setAmount( (long) 5000 );
        form2.setDate( date.toString() );
        form2.setPaymentMethod( "Check" );

        assertEquals( 5000, (long) form2.getAmount() );
        assertEquals( date.toString(), form2.getDate() );
        assertEquals( "Check", form2.getPaymentMethod() );

        final Payment payment2 = new Payment( form2 );
        assertEquals( 5000, (long) payment2.getAmount() );
        assertEquals( date, payment2.getDate() );
        assertEquals( "Check", payment2.getPaymentMethod() );
    }

    @Test
    @Transactional
    public void testInvalidPayments () {
        // Invalid payment type
        final Payment payment1 = new Payment();
        payment1.setAmount( (long) 10000 );
        payment1.setDate( ZonedDateTime.now() );
        try {
            payment1.setPaymentMethod( "Invalid" );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Invalid payment method", e.getMessage() );
        }

        // Negative payment amount
        final Payment payment2 = new Payment();
        try {
            payment2.setAmount( (long) -5000 );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Payment amount must be positive.", e.getMessage() );
        }
    }

    @Test
    @Transactional
    public void testAddValidPayments () {
        final OfficeVisit visit2 = new OfficeVisit();
        final ArrayList<CPTCode> codes = new ArrayList<CPTCode>();

        final CPTCode code1 = new CPTCode();
        code1.setCode( 99202 );
        code1.setDescription( "for office visits of 15-29 minutes" );
        code1.setCost( 7500 );
        code1.setVersion( 1 );
        code1.setIsArchived( false );
        code1.setTimeRangeMin( 15 );
        code1.setTimeRangeMax( 29 );

        final CPTCode code2 = new CPTCode();
        code2.setCode( 99205 );
        code2.setDescription( "for office visits of 60-74 minutes" );
        code2.setCost( 25000 );
        code2.setVersion( 1 );
        code2.setIsArchived( false );
        code2.setTimeRangeMin( 60 );
        code2.setTimeRangeMax( 74 );

        codes.add( code1 );
        codes.add( code2 );
        visit2.setCptCodes( codes );

        final Bill bill2 = new Bill( visit2 );
        assertFalse( bill2.getIsPaid() );
        assertEquals( 0, bill2.getPayments().size() );
        assertEquals( 32500, (long) bill2.getTotalDue() );
        assertEquals( visit2, bill2.getVisit() );

        final Payment payment1 = new Payment();
        payment1.setAmount( (long) 10000 );
        payment1.setPaymentMethod( "Cash" );
        payment1.setDate( ZonedDateTime.now() );

        bill2.addPayment( payment1 );
        assertFalse( bill2.getIsPaid() );
        assertEquals( 1, bill2.getPayments().size() );
        assertEquals( payment1, bill2.getPayments().get( 0 ) );
        assertEquals( 32500, (long) bill2.getTotalDue() );
        assertEquals( visit2, bill2.getVisit() );

        final Payment payment2 = new Payment();
        payment2.setAmount( (long) 10000 );
        payment2.setPaymentMethod( "Check" );
        payment2.setDate( ZonedDateTime.now() );

        final Payment payment3 = new Payment();
        payment3.setAmount( (long) 5000 );
        payment3.setPaymentMethod( "Insurance" );
        payment3.setDate( ZonedDateTime.now() );

        final Payment payment4 = new Payment();
        payment4.setAmount( (long) 7500 );
        payment4.setPaymentMethod( "Credit" );
        payment4.setDate( ZonedDateTime.now() );

        bill2.addPayment( payment2 );
        assertFalse( bill2.getIsPaid() );
        assertEquals( 2, bill2.getPayments().size() );

        bill2.addPayment( payment3 );
        assertFalse( bill2.getIsPaid() );
        assertEquals( 3, bill2.getPayments().size() );

        bill2.addPayment( payment4 );
        assertTrue( bill2.getIsPaid() );
        assertEquals( 4, bill2.getPayments().size() );

    }

    @Test
    @Transactional
    public void testAddInvalidPayments () {
        final OfficeVisit visit2 = new OfficeVisit();
        final ArrayList<CPTCode> codes = new ArrayList<CPTCode>();

        final CPTCode code1 = new CPTCode();
        code1.setCode( 99202 );
        code1.setDescription( "for office visits of 15-29 minutes" );
        code1.setCost( 7500 );
        code1.setVersion( 1 );
        code1.setIsArchived( false );
        code1.setTimeRangeMin( 15 );
        code1.setTimeRangeMax( 29 );

        final CPTCode code2 = new CPTCode();
        code2.setCode( 99205 );
        code2.setDescription( "for office visits of 60-74 minutes" );
        code2.setCost( 25000 );
        code2.setVersion( 1 );
        code2.setIsArchived( false );
        code2.setTimeRangeMin( 60 );
        code2.setTimeRangeMax( 74 );

        codes.add( code1 );
        codes.add( code2 );
        visit2.setCptCodes( codes );

        final Bill bill2 = new Bill( visit2 );
        assertFalse( bill2.getIsPaid() );
        assertEquals( 0, bill2.getPayments().size() );
        assertEquals( 32500, (long) bill2.getTotalDue() );
        assertEquals( visit2, bill2.getVisit() );

        // Overpay a bill
        final Payment payment2 = new Payment();
        payment2.setAmount( (long) 50000 );
        payment2.setDate( ZonedDateTime.now() );
        payment2.setPaymentMethod( "Credit" );

        try {
            bill2.addPayment( payment2 );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Tried to overpay.", e.getMessage() );
        }

        // Add a null payment
        final Payment payment3 = null;
        try {
            bill2.addPayment( payment3 );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Invalid payment.", e.getMessage() );
        }

    }
}
