package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Class for testing the CPT code API.
 *
 * @author Zach Harris
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICPTCodeTest {
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
        final UserForm pTestUser = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        final UserForm pTestUser2 = new UserForm( "billing", "123456", Role.ROLE_BILLING, 1 );

        final Personnel p = new Personnel( pTestUser );
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
    public void testCPTCodeAPI () throws Exception {

        // Create two CPT codes for testing
        final CPTCodeForm cptForm1 = new CPTCodeForm();
        cptForm1.setCode( 90000 );
        cptForm1.setDescription( "TEST DESC 1" );
        cptForm1.setIsArchived( false );
        cptForm1.setCost( 5 );
        cptForm1.setTimeRangeMin( 5 );
        cptForm1.setTimeRangeMax( 15 );
        cptForm1.setVersion( (long) 1 );

        final CPTCodeForm cptForm2 = new CPTCodeForm();
        cptForm2.setCode( 90001 );
        cptForm2.setDescription( "TEST DESC 2" );
        cptForm2.setIsArchived( false );
        cptForm2.setCost( 10 );
        cptForm2.setTimeRangeMin( 0 );
        cptForm2.setTimeRangeMax( 0 );
        cptForm2.setVersion( (long) 1 );

        final CPTCodeForm cptForm3 = new CPTCodeForm();
        cptForm3.setCode( 90000 );
        cptForm3.setDescription( "TEST DESC 3" );
        cptForm3.setIsArchived( false );
        cptForm3.setCost( 15 );
        cptForm3.setTimeRangeMin( 0 );
        cptForm3.setTimeRangeMax( 0 );
        cptForm3.setVersion( (long) 1 );

        final String content1 = mvc
                .perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( cptForm1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response and ensure it is valid
        final CPTCode c1 = TestUtils.gson().fromJson( content1, CPTCode.class );
        final CPTCodeForm cf1 = new CPTCodeForm( c1 );
        assertEquals( cf1.getCode(), cptForm1.getCode() );
        assertEquals( cf1.getCost(), cptForm1.getCost() );
        assertEquals( cf1.getDescription(), cptForm1.getDescription() );
        assertEquals( cf1.getIsArchived(), cptForm1.getIsArchived() );
        assertEquals( cf1.getTimeRangeMin(), cptForm1.getTimeRangeMin() );
        assertEquals( cf1.getTimeRangeMax(), cptForm1.getTimeRangeMax() );
        assertEquals( cf1.getVersion(), cptForm1.getVersion() );
        assertEquals( 1, (long) cf1.getVersion() );

        // Try adding a new code with the same code number as an active code
        mvc.perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cptForm3 ) ) ).andExpect( status().isBadRequest() );

        final String content2 = mvc
                .perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( cptForm2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response and ensure it is valid
        final CPTCode c2 = TestUtils.gson().fromJson( content2, CPTCode.class );
        final CPTCodeForm cf2 = new CPTCodeForm( c2 );
        assertEquals( cf2.getCode(), cptForm2.getCode() );
        assertEquals( cf2.getCost(), cptForm2.getCost() );
        assertEquals( cf2.getDescription(), cptForm2.getDescription() );
        assertEquals( cf2.getIsArchived(), cptForm2.getIsArchived() );
        assertEquals( cf2.getTimeRangeMin(), cptForm2.getTimeRangeMin() );
        assertEquals( cf2.getTimeRangeMax(), cptForm2.getTimeRangeMax() );
        assertEquals( cf2.getVersion(), cptForm2.getVersion() );

        // Verify CPT codes have been added
        final String allCodesContent = mvc.perform( get( "/api/v1/cptcodes/" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final List<CPTCode> allCodes = TestUtils.gson().fromJson( allCodesContent, new TypeToken<List<CPTCode>>() {
        }.getType() );
        assertTrue( allCodes.size() == 2 );

        // Edit first CPT code
        c1.setCost( 8 );
        final String editContent1 = mvc
                .perform( put( "/api/v1/cptcodes/" + c1.getCode() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new CPTCodeForm( c1 ) ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final CPTCode edited1 = TestUtils.gson().fromJson( editContent1, CPTCode.class );
        assertEquals( c1.getCode(), edited1.getCode() );
        assertEquals( c1.getCost(), edited1.getCost() );
        assertEquals( 2, (long) edited1.getVersion() );

        // Get a CPTCode by code value
        final String getContent1 = mvc.perform( get( "/api/v1/cptcodes/" + c1.getCode() ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final CPTCode fetched1 = TestUtils.gson().fromJson( getContent1, CPTCode.class );
        assertEquals( c1.getCode(), fetched1.getCode() );

        // Archive a code
        mvc.perform( delete( "/api/v1/cptcodes/" + c1.getCode() ) ).andExpect( status().isOk() )
                .andExpect( content().string( "" + c1.getCode() ) );

        // Try adding a code with the same code number as a completely archived
        // code
        mvc.perform( post( "/api/v1/cptcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cptForm3 ) ) ).andExpect( status().isBadRequest() );

        // Get archived codes
        final String allArchivedCodesContent = mvc.perform( get( "/api/v1/cptarchive/" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final List<CPTCode> allArchivedCodes = TestUtils.gson().fromJson( allArchivedCodesContent,
                new TypeToken<List<CPTCode>>() {
                }.getType() );
        assertTrue( allArchivedCodes.size() == 2 );

        // Archive an invalid code
        mvc.perform( delete( "/api/v1/cptcodes/" + 0 ) ).andExpect( status().isNotFound() );

        // Edit invalid code
        mvc.perform( put( "/api/v1/cptcodes/" + 0 ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new CPTCodeForm( c2 ) ) ) ).andExpect( status().isNotFound() );

        // Get a code that doesn't exist
        mvc.perform( get( "/api/v1/cptcodes/345" ) ).andExpect( status().isNotFound() );

        // Edit archived code
        c1.setCost( 20 );
        mvc.perform( put( "/api/v1/cptcodes/" + c1.getCode() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new CPTCodeForm( c1 ) ) ) ).andExpect( status().isBadRequest() );
    }

}
