package edu.ncsu.csc.iTrust2.api;

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

import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
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
    private UserService<User>     uService;

    /**
     * Performs setup operations for the tests.
     *
     * @throws Exception
     */
    @Before
    public void setup () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        final UserForm pTestUser1 = new UserForm( "billing", "123456", Role.ROLE_BILLING, 1 );
        final UserForm pTestUser2 = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );

        final Personnel p = new Personnel( pTestUser1 );
        final Personnel p2 = new Personnel( pTestUser2 );

        uService.save( p );
        uService.save( p2 );
    }

    /**
     * Tests basic CPT code APIs.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "billing", roles = { "USER", "BILLING", "ADMIN" } )
    @Transactional
    public void testBillingAPI () throws Exception {

    }

}
