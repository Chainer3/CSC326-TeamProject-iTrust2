package edu.ncsu.csc.iTrust2.unit;

import java.util.List;

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
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for user model
 *
 */
@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class UserTest {

    @Autowired
    private UserService<User>   service;

    private static final String USER_1 = "testUser1";

    private static final String USER_2 = "testUser2";

    private static final String USER_3 = "testUser3";

    private static final String PW     = "123456";

    /**
     * Set up
     */
    @Before
    public void setup () {
        service.deleteAll();
    }

    /**
     * tests the user roles
     */
    @Test
    @Transactional
    public void testUserRoles () {

        Assert.assertEquals( "There should be no users in the system", 0, service.count() );

        final User user1 = new Personnel( new UserForm( USER_1, PW, Role.ROLE_HCP, 1 ) );

        service.save( user1 );

        Assert.assertEquals( "Creating a user should result in its presence in the database", 1, service.count() );

        user1.addRole( Role.ROLE_ER );

        service.save( user1 );

        Assert.assertEquals( "Giving a user a second role should still result in just a single user", 1,
                service.count() );

        Assert.assertEquals( "A user with two roles should be retrieved with two roles", 2,
                service.findByName( USER_1 ).getRoles().size() );

        final User user2 = new Patient( new UserForm( USER_2, PW, Role.ROLE_PATIENT, 1 ) );

        User user3 = new Personnel( new UserForm( USER_3, PW, Role.ROLE_LABTECH, 1 ) );
        service.saveAll( List.of( user2, user3 ) );

        Assert.assertEquals( "Creating multiple users should save them as expected", 3, service.count() );

        Assert.assertFalse( "A LabTech should not be a Doctor by default", user3.isDoctor() );

        user3 = service.findByName( USER_3 );

        user3.addRole( Role.ROLE_HCP );

        Assert.assertTrue( "A user with multiple roles should identify as a Doctor properly", user3.isDoctor() );

    }

    /**
     * Tests illegal role combinations (ie. admin and any other role)
     */
    @Test
    @Transactional
    public void testIllegalRoleCombinations () {
        try {
            final UserForm uf = new UserForm( USER_2, PW, Role.ROLE_ADMIN, 1 );
            uf.addRole( Role.ROLE_LABTECH.toString() );

            final User user2 = new Personnel( uf );

            // Otherwise we get compilation warnings
            Assert.assertNotNull( user2 );

            Assert.fail( "It should not be possible to create an Admin with a secondary role!" );
        }
        catch ( final Exception e ) {
            // expected
        }

        try {
            final UserForm uf = new UserForm( USER_2, PW, Role.ROLE_ADMIN, 1 );
            final User user2 = new Personnel( uf );

            user2.addRole( Role.ROLE_ER );

            Assert.fail( "It should not be possible to add another Role to an Admin user!" );
        }
        catch ( final Exception e ) {
            // expected
        }

        try {
            final UserForm uf = new UserForm( USER_2, PW, Role.ROLE_ER, 1 );
            final User user2 = new Personnel( uf );

            user2.addRole( Role.ROLE_ADMIN );

            Assert.fail( "It should not be possible to add the Admin role to any user!" );
        }
        catch ( final Exception e ) {
            // expected
        }
    }

    /**
     * Tests adding users with the new billing role. Tests both legal and
     * illegal role combinations.
     */
    @Test
    @Transactional
    public void testBillingRole () {
        Assert.assertEquals( "There should be no users in the system", 0, service.count() );

        final User user1 = new Personnel( new UserForm( USER_1, PW, Role.ROLE_BILLING, 1 ) );

        service.save( user1 );

        Assert.assertEquals( "Creating a user should result in its presence in the database", 1, service.count() );

        Assert.assertEquals( 1, service.findByName( USER_1 ).getRoles().size() );
        Assert.assertTrue( service.findByName( USER_1 ).getRoles().contains( Role.ROLE_BILLING ) );

        try {
            user1.addRole( Role.ROLE_HCP );
            Assert.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assert.assertEquals( "Billing Staff Members cannot also be HCPs", e.getMessage() );
        }

        final User user2 = new Personnel( new UserForm( USER_2, PW, Role.ROLE_HCP, 1 ) );

        service.save( user2 );
        Assert.assertEquals( 2, service.count() );

        try {
            user2.addRole( Role.ROLE_BILLING );
            Assert.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assert.assertEquals( "Billing Staff Members cannot also be HCPs", e.getMessage() );
        }

        final UserForm form3 = new UserForm( USER_3, PW, Role.ROLE_BILLING, 1 );
        form3.addRole( "ROLE_HCP" );
        User user3 = null;
        try {
            user3 = new Personnel( form3 );
            Assert.fail();
        }
        catch ( final IllegalArgumentException e ) {
            Assert.assertNull( user3 );
            Assert.assertEquals(
                    "Tried to create a user that was a Billing Staff Member and a HCP. A user cannot be both roles!",
                    e.getMessage() );
        }

    }
}
