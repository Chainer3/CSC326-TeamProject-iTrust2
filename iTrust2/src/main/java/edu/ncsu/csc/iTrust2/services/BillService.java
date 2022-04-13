package edu.ncsu.csc.iTrust2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.repositories.BillRepository;

@Component
@Transactional
public class BillService extends Service<Bill, Long> {
    @Autowired
    private BillRepository repository;

    @Override
    protected JpaRepository<Bill, Long> getRepository () {
        return repository;
    }

    /**
     * This method asks the repository to return a bill associated with an
     * office visit
     *
     * @param o
     *            Office Visit to search with
     * @return the bill associated with that office visit
     */
    public Bill findByVisit ( final OfficeVisit o ) {
        return repository.findByVisit( o );
    }

    /**
     * This method asks the repository to return a list of bills depending on
     * their paid status
     *
     * @param isPaid
     *            States whether the user is looking for paid or unpaid bills
     * @return a list of bills depending on isPaid
     */
    public List<Bill> findByIsPaid ( final boolean isPaid ) {
        return repository.findByIsPaid( isPaid );
    }

    /**
     * Builds a bill from a form
     *
     * @param form
     *            Form to build the bill from
     * @return a bill with that form's information
     */
    public Bill build ( final BillForm form ) {
        final Bill bill = new Bill();
        final Bill existing = findByVisit( form.getVisit() );
        bill.setTotalDue( form.getTotalDue() );
        bill.setIsPaid( form.getIsPaid() );
        bill.setVisit( form.getVisit() );
        bill.setPayments( form.getPayments() );
        if ( existing != null ) {
            bill.setId( existing.getId() );
        }
        return bill;
    }
}
