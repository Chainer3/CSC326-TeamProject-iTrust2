package edu.ncsu.csc.iTrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * Tests the CPTCode and CPTCodeForm class for correct functionality
 *
 * @author Leah Whaley
 *
 */
@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class CPTCodeTest {

    /**
     * Tests creating CPT codes with valid values for all fields
     */
    @Test
    @Transactional
    public void testValidCodes () {
        final CPTCodeForm form = new CPTCodeForm();
        form.setCode( 99202 );
        form.setDescription( "for office visits of 15-29 minutes" );
        form.setCost( 7500 );
        form.setVersion( 1 );
        form.setIsArchived( false );
        form.setTimeRangeMin( 15 );
        form.setTimeRangeMax( 29 );

        assertEquals( 99202, form.getCode() );
        assertEquals( "for office visits of 15-29 minutes", form.getDescription() );
        assertEquals( 7500, form.getCost() );
        assertEquals( 1, form.getVersion() );
        assertFalse( form.getIsArchived() );
        assertEquals( 15, form.getTimeRangeMin() );
        assertEquals( 29, form.getTimeRangeMax() );

        final CPTCode code = new CPTCode();
        code.setId( 1L );
        code.setCode( 99202 );
        code.setDescription( "for office visits of 15-29 minutes" );
        code.setCost( 7500 );
        code.setVersion( 1 );
        code.setIsArchived( false );
        code.setTimeRangeMin( 15 );
        code.setTimeRangeMax( 29 );

        assertEquals( 1L, (long) code.getId() );
        assertEquals( 99202, code.getCode() );
        assertEquals( "for office visits of 15-29 minutes", code.getDescription() );
        assertEquals( 7500, code.getCost() );
        assertEquals( 1, code.getVersion() );
        assertFalse( code.getIsArchived() );
        assertEquals( 15, code.getTimeRangeMin() );
        assertEquals( 29, code.getTimeRangeMax() );

        final CPTCode newCode = new CPTCode( form );
        assertNotNull( newCode );

        assertEquals( 99202, newCode.getCode() );
        assertEquals( "for office visits of 15-29 minutes", newCode.getDescription() );
        assertEquals( 7500, newCode.getCost() );
        assertEquals( 1, newCode.getVersion() );
        assertFalse( newCode.getIsArchived() );
        assertEquals( 15, newCode.getTimeRangeMin() );
        assertEquals( 29, newCode.getTimeRangeMax() );

        final CPTCodeForm newForm = new CPTCodeForm( code );

        assertEquals( 99202, newForm.getCode() );
        assertEquals( "for office visits of 15-29 minutes", newForm.getDescription() );
        assertEquals( 7500, newForm.getCost() );
        assertEquals( 1, newForm.getVersion() );
        assertFalse( newForm.getIsArchived() );
        assertEquals( 15, newForm.getTimeRangeMin() );
        assertEquals( 29, newForm.getTimeRangeMax() );

        assertTrue( code.equals( newCode ) );
        assertTrue( form.equals( newForm ) );

    }

    /**
     * Tests that an exception is thrown if a user tries to create a CPT code
     * with an invalid value for any field
     */
    @Test
    @Transactional
    public void testInvalidCodes () {
        final CPTCodeForm form = new CPTCodeForm();
        form.setCode( -34 );

        CPTCode code;
        try {
            code = new CPTCode( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "CPT Code must be a positive number: -34", e.getMessage() );
        }

        form.setCode( 99205 );
        form.setDescription(
                "This is a very long description it has a lot of words and it is way too long for any CPT Code. This is very excessive and it does not need to be this long. The writer should be more concise so that they can use less characters. That would make it easier to read the description." );

        try {
            code = new CPTCode( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Description length must be less than 250 characters!", e.getMessage() );
        }

        form.setDescription( "for office visits of 60-74 minutes" );
        form.setCost( -345 );

        try {
            code = new CPTCode( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Cost must be a positive number: -345", e.getMessage() );
        }

        form.setCost( 25000 );

        form.setVersion( 1 );
        form.setIsArchived( false );
        form.setTimeRangeMin( -45 );

        try {
            code = new CPTCode( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "The minimum number of minutes for the time range has to be positive: -45", e.getMessage() );
        }

        form.setTimeRangeMin( 60 );
        form.setTimeRangeMax( -34 );

        try {
            code = new CPTCode( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals(
                    "The maximum number of minutes for the time range has to be positive and greater than the minimum for the time range: -34",
                    e.getMessage() );
        }

        form.setTimeRangeMax( 45 );

        try {
            code = new CPTCode( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals(
                    "The maximum number of minutes for the time range has to be positive and greater than the minimum for the time range: 45",
                    e.getMessage() );
        }

        form.setTimeRangeMax( 74 );

        code = new CPTCode( form );

        assertEquals( 99205, code.getCode() );
        assertEquals( "for office visits of 60-74 minutes", code.getDescription() );
        assertEquals( 25000, code.getCost() );
        assertEquals( 1, code.getVersion() );
        assertEquals( 60, code.getTimeRangeMin() );
        assertEquals( 74, code.getTimeRangeMax() );
    }
}
