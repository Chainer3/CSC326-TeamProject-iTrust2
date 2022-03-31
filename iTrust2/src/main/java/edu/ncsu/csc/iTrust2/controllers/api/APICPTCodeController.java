package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with CPT codes. Exposes functionality to
 * add, edit, fetch, and archive CPT codes.
 *
 * @author Zach Harris
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APICPTCodeController extends APIController {

    /** LoggerUtil */
    @Autowired
    private LoggerUtil     loggerUtil;

    /** CPT Code service */
    @Autowired
    private CPTCodeService cptService;

    /**
     * Adds a new CPT code to the system.
     *
     * @param form
     *            The submission data form used to create the CPT code.
     * @return The response to the request.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @PostMapping ( BASE_PATH + "/cptcodes" )
    public ResponseEntity addCode ( @RequestBody final CPTCodeForm form ) {
        try {
            final CPTCode c = cptService.build( form );
            cptService.save( c );
            loggerUtil.log( TransactionType.CPT_CREATE, LoggerUtil.currentUser(),
                    "Created CPT code with id " + c.getId() );
            return new ResponseEntity( c, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.CPT_CREATE, LoggerUtil.currentUser(), "Failed to create prescription" );
            return new ResponseEntity( errorResponse( "Could not save the prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets the specified CPT code.
     *
     * @param code
     *            The CPT code to get.
     * @return The requested CPT code.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/cptcodes/{code}" )
    public ResponseEntity getCode ( @PathVariable final Long code ) {
        final CPTCode c = cptService.findByCode( code );

        if ( c == null ) {
            loggerUtil.log( TransactionType.CPT_VIEW, LoggerUtil.currentUser(), "Failed to find CPT code " + code );
            return new ResponseEntity( errorResponse( "No CPT code found for " + code ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.CPT_VIEW, LoggerUtil.currentUser(), "Viewed CPT code  " + code );
            return new ResponseEntity( c, HttpStatus.OK );
        }
    }

    /**
     * Gets all CPT codes.
     *
     * @return A list of all CPT codes.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/cptcodes/" )
    public List<CPTCode> getCodes () {
        // Return all CPT codes in system
        loggerUtil.log( TransactionType.CPT_VIEW, LoggerUtil.currentUser(), "User viewed a list of all CPT codes" );
        return cptService.findByIsArchived( false );
    }

    /**
     * Gets all archived CPT codes.
     *
     * @return A list of all archived CPT codes.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/cptarchive/" )
    public List<CPTCode> getArchivedCodes () {
        // Return all CPT codes in system
        loggerUtil.log( TransactionType.CPT_VIEW, LoggerUtil.currentUser(),
                "User viewed a list of all archived CPT codes" );
        return cptService.findByIsArchived( true );
    }

    /**
     * Archives the specified CPT code.
     *
     * @param code
     *            The code to archive.
     * @return The archived CPT code.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @DeleteMapping ( BASE_PATH + "/cptcodes/{code}" )
    public ResponseEntity archiveCode ( @PathVariable final Long code ) {
        final CPTCode c = cptService.findByCode( code );
        if ( c == null ) {
            return new ResponseEntity( errorResponse( "No CPT code found matching " + code ), HttpStatus.NOT_FOUND );
        }
        try {
            c.setIsArchived( true );
            cptService.save( c );
            loggerUtil.log( TransactionType.CPT_ARCHIVE, LoggerUtil.currentUser(), "Archived CPT code " + code );
            return new ResponseEntity( code, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.CPT_ARCHIVE, LoggerUtil.currentUser(), "Failed to archive CPT code" );
            return new ResponseEntity( errorResponse( "Failed to archive CPT code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits the specified code using the provided data form.
     *
     * @param code
     *            The code to edit.
     * @param form
     *            The new data to associate with the code.
     * @return The edited CPT code.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    @PutMapping ( BASE_PATH + "/cptcodes/{code}" )
    public ResponseEntity editCode ( @PathVariable final Long code, @RequestBody final CPTCodeForm form ) {
        try {
            final CPTCode c = cptService.build( form );
            final CPTCode saved = cptService.findByCode( code );
            if ( saved == null ) {
                loggerUtil.log( TransactionType.CPT_EDIT, LoggerUtil.currentUser(),
                        "No CPT code found matching " + c.getCode() );
                return new ResponseEntity( errorResponse( "No CPT code found matching " + c.getCode() ),
                        HttpStatus.NOT_FOUND );
            }
            c.setId( saved.getId() );
            cptService.save( c );
            loggerUtil.log( TransactionType.CPT_EDIT, LoggerUtil.currentUser(), "Edited CPT code " + c.getCode() );
            return new ResponseEntity( c, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Failed to update CPT code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }
}
