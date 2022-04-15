package edu.ncsu.csc.iTrust2.services;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.BasicHealthMetrics;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.Drug;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.ICDCode;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Provides tests for the Payment Service and Repository
 *
 * @author Nikolaus Johnson
 *
 */
@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class PaymentServiceTest {
    /** Service holding CPT codes */
    @Autowired
    private PaymentService            service;

    @Autowired
    private BillService               billService;
    // All of the following services are used to construct an office visit for
    // testing
    @Autowired
    private OfficeVisitService        officeVisitService;

    @Autowired
    private BasicHealthMetricsService basicHealthMetricsService;

    @Autowired
    private HospitalService           hospitalService;

    @Autowired
    private UserService<User>         userService;

    @Autowired
    private ICDCodeService            icdCodeService;

    @Autowired
    private DrugService               drugService;

    @Autowired
    private PrescriptionService       prescriptionService;
    /** Office visit used in testing */
    private OfficeVisit               visit;

    @Before
    public void setUp () throws Exception {
        // clear databases
        service.deleteAll();
        officeVisitService.deleteAll();

        // All of this is used for constructing the test office visit
        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );

        userService.saveAll( List.of( hcp, alice ) );

        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hospitalService.save( hosp );

        visit = new OfficeVisit();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( userService.findByName( "hcp" ) );
        bhm.setPatient( userService.findByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        basicHealthMetricsService.save( bhm );

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hosp );
        visit.setPatient( userService.findByName( "AliceThirteen" ) );
        visit.setHcp( userService.findByName( "AliceThirteen" ) );
        visit.setDate( ZonedDateTime.now() );
        officeVisitService.save( visit );

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Top Quality" );

        icdCodeService.save( code );

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        officeVisitService.save( visit );

        final Drug drug = new Drug();

        drug.setCode( "1234-4321-89" );
        drug.setDescription( "Lithium Compounds" );
        drug.setName( "Li2O8" );
        drugService.save( drug );

        final Prescription pres = new Prescription();
        pres.setDosage( 3 );
        pres.setDrug( drug );

        final LocalDate now = LocalDate.now();
        pres.setEndDate( now.plus( Period.ofWeeks( 5 ) ) );
        pres.setPatient( userService.findByName( "AliceThirteen" ) );
        pres.setStartDate( now );
        pres.setRenewals( 5 );

        prescriptionService.save( pres );

        final List<Prescription> pr = new ArrayList<Prescription>();
        pr.add( pres );
        visit.setPrescriptions( pr );

        officeVisitService.save( visit );
    }

    /**
     * Tests saving and getting payments from the repository
     */
    @Test
    @Transactional
    public void testFind () {

        // Sets up the first bill for testing
        final Bill bill1 = new Bill();
        bill1.setId( 1L );
        bill1.setTotalDue( (long) 500 );
        bill1.setIsPaid( false );
        bill1.setVisit( visit );
        bill1.setPayments( new ArrayList<Payment>() );
        // Sets up the first payment for testing
        final Payment payment = new Payment();
        payment.setAmount( (long) 500 );
        payment.setDate( ZonedDateTime.now() );
        payment.setPaymentMethod( "Credit" );
        service.save( payment );
        bill1.addPayment( payment );
        billService.save( bill1 );
        final Payment payment2 = service.findAll().get( 0 );
        Assert.assertEquals( payment.getAmount(), payment2.getAmount() );
        Assert.assertEquals( payment.getDate(), payment2.getDate() );
        Assert.assertEquals( payment.getPaymentMethod(), payment2.getPaymentMethod() );
    }

    /**
     * Test for building payments from forms
     */
    @Test
    @Transactional
    public void testBuild () {
        // Sets up the first payment for testing
        final PaymentForm form = new PaymentForm();
        form.setAmount( (long) 500 );
        form.setDate( ZonedDateTime.now().toString() );
        form.setPaymentMethod( "Credit" );
        final Payment payment = service.build( form );
        Assert.assertEquals( payment.getAmount(), form.getAmount() );
        Assert.assertEquals( payment.getDate().toString(), form.getDate() );
        Assert.assertEquals( payment.getPaymentMethod(), form.getPaymentMethod() );
    }
}
