package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Class for testing the billing API.
 *
 * @author Zach Harris
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIBillTest {
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OfficeVisitService    officeVisitService;

    @Autowired
    private BillService           billService;

    @Autowired
    private CPTCodeService        cptService;

    @Autowired
    private HospitalService       hospitalService;

    @Autowired
    private UserService<User>     uService;

    /**
     * Performs setup operations for the tests.
     *
     * @throws Exception
     */
    @Before
    public void setup () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        final UserForm pTestUser = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        final UserForm pTestUser1 = new UserForm( "billing", "123456", Role.ROLE_BILLING, 1 );
        final UserForm pTestUser2 = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        final UserForm pTestUser3 = new UserForm( "patient2", "123456", Role.ROLE_PATIENT, 1 );

        final Personnel p = new Personnel( pTestUser );
        final Personnel p1 = new Personnel( pTestUser1 );
        final Patient p2 = new Patient( pTestUser2 );
        final Patient p3 = new Patient( pTestUser3 );

        uService.save( p );
        uService.save( p1 );
        uService.save( p2 );
        uService.save( p3 );

        officeVisitService.deleteAll();
        billService.deleteAll();

        final Hospital hosp = new Hospital();
        hosp.setAddress( "123 Raleigh Road" );
        hosp.setState( State.NC );
        hosp.setZip( "27514" );
        hosp.setName( "iTrust Test Hospital 2" );

        hospitalService.save( hosp );

        final CPTCode code1 = new CPTCode();
        code1.setId( 1L );
        code1.setCode( 99202 );
        code1.setDescription( "for office visits of 15-29 minutes" );
        code1.setCost( 7500 );
        code1.setVersion( 1 );
        code1.setIsArchived( false );
        code1.setTimeRangeMin( 15 );
        code1.setTimeRangeMax( 29 );
        // Saves the code into the service
        cptService.save( code1 );
    }

    /**
     * Creates an office visit for testing.
     *
     * @throws Exception
     */
    private void setupOfficeVisit () throws Exception {

        final LinkedList<CPTCodeForm> codes = new LinkedList<CPTCodeForm>();
        codes.add( new CPTCodeForm( cptService.findByCode( 99202 ) ) );

        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setDate( "2030-11-19T04:50:00.000-05:00" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );
        visit.setCptCodes( codes );

        /* Create the Office Visit */
        final OfficeVisit ov1 = officeVisitService.build( visit );
        officeVisitService.save( ov1 );

        final OfficeVisitForm visit2 = new OfficeVisitForm();
        visit2.setDate( "2030-11-19T04:50:00.000-05:00" );
        visit2.setHcp( "hcp" );
        visit2.setPatient( "patient2" );
        visit2.setNotes( "Test office visit" );
        visit2.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit2.setHospital( "iTrust Test Hospital 2" );
        visit2.setCptCodes( codes );

        /* Create the Office Visit */
        officeVisitService.save( officeVisitService.build( visit2 ) );
    }

    /**
     * Tests basic billing API end points.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "billing", roles = { "USER", "BILLING", "ADMIN" } )
    @Transactional
    public void testBillingAPI () throws Exception {
        setupOfficeVisit();

        // Verify bills have been added
        final String allBillsContent = mvc
                .perform( get( "/api/v1/bills/" ).contentType( MediaType.APPLICATION_JSON_VALUE ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final List<Bill> allBills = TestUtils.gson().fromJson( allBillsContent, new TypeToken<List<Bill>>() {
        }.getType() );
        assertEquals( 2, allBills.size() );
        assertEquals( 2, billService.findAll().size() );
        final Bill bill = billService.findAll().get( 0 );

        // Retrieve by ID
        final String billGetCheck = mvc.perform( get( "/api/v1/bills/" + bill.getId() ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final Bill retrievedBill = TestUtils.gson().fromJson( billGetCheck, new TypeToken<Bill>() {
        }.getType() );

        assertEquals( bill.getId(), retrievedBill.getId() );
        assertEquals( bill.getIsPaid(), retrievedBill.getIsPaid() );
        assertEquals( bill.getTotalDue(), retrievedBill.getTotalDue() );
        assertEquals( bill.getBalance(), retrievedBill.getBalance() );

        // get status
        final String status = mvc.perform( get( "/api/v1/bills/" + bill.getId() + "/status" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertEquals( bill.getStatus(), status );

        // get balance
        final String balanceContent = mvc.perform( get( "/api/v1/bills/" + bill.getId() + "/balance" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Long balance = TestUtils.gson().fromJson( balanceContent, new TypeToken<Long>() {
        }.getType() );

        assertEquals( bill.getBalance(), balance );

        // get payments
        String paymentsContent = mvc.perform( get( "/api/v1/bills/" + bill.getId() + "/payments" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        List<PaymentForm> payments = TestUtils.gson().fromJson( paymentsContent, new TypeToken<List<PaymentForm>>() {
        }.getType() );

        assertEquals( payments.size(), 0 );

        // add payment

        final PaymentForm payment = new PaymentForm();
        payment.setAmount( 5000L );
        payment.setDate( "2030-11-20T04:50:00.000-05:00" );
        payment.setPaymentMethod( "cash" );

        mvc.perform( post( "/api/v1/bills/" + bill.getId() + "/payments" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( payment ) ) ).andExpect( status().isOk() );

        // get payments
        paymentsContent = mvc.perform( get( "/api/v1/bills/" + bill.getId() + "/payments" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        payments = TestUtils.gson().fromJson( paymentsContent, new TypeToken<List<PaymentForm>>() {
        }.getType() );

        assertEquals( payments.size(), 1 );

    }

    /**
     * Tests billing API end points for patients.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "USER", "PATIENT" } )
    @Transactional
    public void testPatientBillingAPI () throws Exception {
        setupOfficeVisit();

        // Verify bills have been added
        final String allBillsContent = mvc.perform( get( "/api/v1/patient/bills/" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final List<Bill> allBills = TestUtils.gson().fromJson( allBillsContent, new TypeToken<List<Bill>>() {
        }.getType() );
        assertEquals( 1, allBills.size() );
        final Bill bill = billService.findById( allBills.get( 0 ).getId() );

        // Retrieve by ID
        final String billGetCheck = mvc.perform( get( "/api/v1/patient/bills/" + bill.getId() ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Bill retrievedBill = TestUtils.gson().fromJson( billGetCheck, new TypeToken<Bill>() {
        }.getType() );

        assertEquals( bill.getId(), retrievedBill.getId() );
        assertEquals( bill.getIsPaid(), retrievedBill.getIsPaid() );
        assertEquals( bill.getTotalDue(), retrievedBill.getTotalDue() );
        assertEquals( bill.getBalance(), retrievedBill.getBalance() );

        // get status
        final String status = mvc.perform( get( "/api/v1/patient/bills/" + bill.getId() + "/status" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertEquals( bill.getStatus(), status );

        // get balance
        final String balanceContent = mvc.perform( get( "/api/v1/patient/bills/" + bill.getId() + "/balance" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Long balance = TestUtils.gson().fromJson( balanceContent, new TypeToken<Long>() {
        }.getType() );

        assertEquals( bill.getBalance(), balance );

        // get payments
        final String paymentsContent = mvc.perform( get( "/api/v1/patient/bills/" + bill.getId() + "/payments" ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final List<PaymentForm> payments = TestUtils.gson().fromJson( paymentsContent,
                new TypeToken<List<PaymentForm>>() {
                }.getType() );

        assertEquals( payments.size(), 0 );

    }

}
