package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.services.BillService;
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

        final Personnel p = new Personnel( pTestUser );
        final Personnel p1 = new Personnel( pTestUser1 );
        final Patient p2 = new Patient( pTestUser2 );

        uService.save( p );
        uService.save( p1 );
        uService.save( p2 );

        officeVisitService.deleteAll();
        billService.deleteAll();

        final Hospital hosp = new Hospital();
        hosp.setAddress( "123 Raleigh Road" );
        hosp.setState( State.NC );
        hosp.setZip( "27514" );
        hosp.setName( "iTrust Test Hospital 2" );

        hospitalService.save( hosp );
    }

    /**
     * Creates an office visit for testing.
     *
     * @throws Exception
     */
    private void setupOfficeVisit () throws Exception {
        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setDate( "2030-11-19T04:50:00.000-05:00" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        /* Create the Office Visit */
        officeVisitService.save( officeVisitService.build( visit ) );
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
        final String allBillsContent = mvc.perform( get( "/api/v1/bills/" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        final List<Bill> allBills = TestUtils.gson().fromJson( allBillsContent, new TypeToken<List<Bill>>() {
        }.getType() );
        assertEquals( 1, allBills.size() );
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
    }

}
