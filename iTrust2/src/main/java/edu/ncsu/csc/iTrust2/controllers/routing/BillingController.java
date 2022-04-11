package edu.ncsu.csc.iTrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Controller class responsible for managing the behavior for the Billing
 * Landing Screen
 *
 * @author Lydia Pearson
 *
 */
@Controller
public class BillingController {
    /**
     * Returns the Landing screen for the Billing Staff
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "billing/index" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    public String index ( final Model model ) {
        return Role.ROLE_BILLING.getLanding();
    }

    /**
     * Returns the page for managing CPT codes
     *
     * @param model
     *            Data for the front end
     * @return The page to display to the user
     */
    @GetMapping ( "/billing/manageCPTCodes" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    public String manageCPTCodes ( final Model model ) {
        return "billing/manageCPTCodes";
    }

    /**
     * Returns the page for the archived CPT code list
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/billing/archivedCPTCodes" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    public String archivedCPTCodes ( final Model model ) {
        return "/billing/archivedCPTCodes";
    }

    /*
     * Returns the page for the Pay Bills page
     * @param model The data for the front end
     * @return The page to display to the user
     */
    @GetMapping ( "/billing/payBills" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILLING')" )
    public String payBills ( final Model model ) {
        return "billing/payBills";
    }

}
