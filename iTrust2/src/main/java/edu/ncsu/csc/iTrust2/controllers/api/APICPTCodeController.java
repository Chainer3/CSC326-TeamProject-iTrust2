package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APICPTCodeController extends APIController {

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
    public CPTCode getCode ( @PathVariable final Long code ) {
        return null;
    }

    /**
     * Gets all CPT codes.
     *
     * @return A list of all CPT codes.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/cptcodes/" )
    public List<CPTCode> getCodes () {

    }

    /**
     * Gets all archived CPT codes.
     *
     * @return A list of all archived CPT codes.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_BILLING')" )
    @GetMapping ( BASE_PATH + "/cptarchive/" )
    public List<CPTCode> getArchiveCodes () {

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

    }
}
