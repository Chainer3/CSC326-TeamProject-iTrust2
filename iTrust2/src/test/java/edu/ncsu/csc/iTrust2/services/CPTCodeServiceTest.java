package edu.ncsu.csc.iTrust2.services;

import static org.junit.Assert.assertNull;

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
import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * Provides tests for the CPT Code Service and Repository
 *
 * @author Nikolaus Johnson
 *
 */
@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class CPTCodeServiceTest {
    /** Service holding CPT codes */
    @Autowired
    private CPTCodeService service;

    /** List of CPT codes that are not archived */
    private List<CPTCode>  active;
    /** List of CPT codes that are archived */
    private List<CPTCode>  inactive;

    @Before
    public void setUp () throws Exception {
        active = service.findByIsArchived( false );
        inactive = service.findByIsArchived( true );
        service.deleteAll();
    }

    // @Test
    // public void testAddCodes () {
    // final CPTCode code1 = new CPTCode();
    // code1.setId( 1L );
    // code1.setCode( 99202 );
    // code1.setDescription( "for office visits of 15-29 minutes" );
    // code1.setCost( 7500 );
    // code1.setVersion( "1.1.0" );
    // code1.setIsArchived( false );
    // code1.setTimeRangeMin( 15 );
    // code1.setTimeRangeMax( 29 );
    // // Saves the first code into the service
    // service.save( code1 );
    // final CPTCode code2 = new CPTCode();
    // code2.setId( 2L );
    // code2.setCode( 99300 );
    // code2.setDescription( "for vacciations of 10-15 minutes" );
    // code2.setCost( 5000 );
    // code2.setVersion( "1.1.0" );
    // code2.setIsArchived( false );
    // code2.setTimeRangeMin( 10 );
    // code2.setTimeRangeMax( 15 );
    // // Saves the second code into the service
    // service.save( code2 );
    //
    // }

    /**
     * Tests saving and getting non-archived CPT codes from the repository
     */
    @Test
    @Transactional
    public void testActiveCodes () {
        // Ensures both lists are empty
        active.clear();
        inactive.clear();
        // Sets up the first code for testing
        final CPTCode code1 = new CPTCode();
        code1.setId( 1L );
        code1.setCode( 99202 );
        code1.setDescription( "for office visits of 15-29 minutes" );
        code1.setCost( 7500 );
        code1.setVersion( (long) 1 );
        code1.setIsArchived( false );
        code1.setTimeRangeMin( 15 );
        code1.setTimeRangeMax( 29 );
        // Saves the first code into the service
        service.save( code1 );
        // Gets codes that are not archived from the service
        active = service.findByIsArchived( false );
        // Ensures that only 1 code was obtained from the service
        Assert.assertEquals( 1, active.size() );
        // Ensures that the code in the service matches the one saved
        Assert.assertEquals( active.get( 0 ), code1 );

        // Sets up the second code for testing
        final CPTCode code2 = new CPTCode();
        code2.setId( 2L );
        code2.setCode( 99300 );
        code2.setDescription( "for vacciations of 10-15 minutes" );
        code2.setCost( 5000 );
        code2.setVersion( (long) 1 );
        code2.setIsArchived( false );
        code2.setTimeRangeMin( 10 );
        code2.setTimeRangeMax( 15 );
        // Saves the second code into the service
        service.save( code2 );
        // Gets codes that are not archived from the service
        active = service.findByIsArchived( false );
        // Ensures that there are now 2 codes in the service
        Assert.assertEquals( 2, active.size() );
        // Ensures that the second code in the service matches the one saved
        Assert.assertEquals( active.get( 1 ), code2 );
    }

    /**
     * Tests saving and getting archived CPT codes from the repository
     */
    @Test
    @Transactional
    public void testInactiveCodes () {
        // Ensures both lists are empty
        active.clear();
        inactive.clear();

        // Sets up the first code for testing
        final CPTCode code1 = new CPTCode();
        code1.setId( 1L );
        code1.setCode( 99202 );
        code1.setDescription( "for office visits of 15-29 minutes" );
        code1.setCost( 7500 );
        code1.setVersion( (long) 1 );
        code1.setIsArchived( true );
        code1.setTimeRangeMin( 15 );
        code1.setTimeRangeMax( 29 );
        // Saves the first code into the service
        service.save( code1 );

        // Gets codes that are archived from the service
        inactive = service.findByIsArchived( true );
        // Ensures that only 1 code was obtained from the service
        Assert.assertEquals( 1, inactive.size() );
        // Ensures that the code in the service matches the one saved
        Assert.assertEquals( inactive.get( 0 ), code1 );

        // Sets up the second code for testing
        final CPTCode code2 = new CPTCode();
        code2.setId( 2L );
        code2.setCode( 99300 );
        code2.setDescription( "for vacciations of 10-15 minutes" );
        code2.setCost( 5000 );
        code2.setVersion( (long) 1 );
        code2.setIsArchived( true );
        code2.setTimeRangeMin( 10 );
        code2.setTimeRangeMax( 15 );
        // Saves the second code into the service
        service.save( code2 );
        // Gets codes that are archived from the service
        inactive = service.findByIsArchived( true );
        // Ensures that there are now 2 codes in the service
        Assert.assertEquals( 2, inactive.size() );
        // Ensures that the second code in the service matches the one saved
        Assert.assertEquals( inactive.get( 1 ), code2 );

    }

    /**
     * Tests saving and getting CPT codes from the repository based on code
     * number
     */
    @Test
    @Transactional
    public void testSingleCodes () {
        // Ensures both lists are empty
        active.clear();
        inactive.clear();
        // Sets the first code for testing
        final CPTCode code1 = new CPTCode();
        code1.setId( 1L );
        code1.setCode( 99202 );
        code1.setDescription( "for office visits of 15-29 minutes" );
        code1.setCost( 7500 );
        code1.setVersion( (long) 1 );
        code1.setIsArchived( false );
        code1.setTimeRangeMin( 15 );
        code1.setTimeRangeMax( 29 );
        // Saves the first code
        service.save( code1 );
        // Looks for the code in the service
        final CPTCode code = service.findByCode( code1.getCode() );
        // Ensures the code matches the code saved
        Assert.assertEquals( code, code1 );

        // Sets the second code for testing
        final CPTCode code2 = new CPTCode();
        code2.setId( 2L );
        code2.setCode( 99300 );
        code2.setDescription( "for vacciations of 10-15 minutes" );
        code2.setCost( 5000 );
        code2.setVersion( (long) 1 );
        code2.setIsArchived( false );
        code2.setTimeRangeMin( 10 );
        code2.setTimeRangeMax( 15 );
        // Saves the second code
        service.save( code2 );
        // Looks for the code in the service
        final CPTCode newcode = service.findByCode( code2.getCode() );
        // Ensures the code matches the code saved
        Assert.assertEquals( newcode, code2 );

        final CPTCode code3 = service.findByCode( 2326 );
        assertNull( code3 );
    }

}
