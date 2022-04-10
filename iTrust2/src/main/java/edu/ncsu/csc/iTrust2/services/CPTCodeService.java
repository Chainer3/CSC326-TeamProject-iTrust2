package edu.ncsu.csc.iTrust2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.repositories.CPTCodeRepository;

@Component
@Transactional
public class CPTCodeService extends Service<CPTCode, Long> {
    @Autowired
    private CPTCodeRepository repository;

    @Override
    protected JpaRepository<CPTCode, Long> getRepository () {
        return repository;
    }

    /**
     * This method asks the repository to return a CPT code based on the number
     * the CPT code is given
     *
     * @param code
     *            Code to look for
     * @return the CPT code with that number
     */
    public CPTCode findByCode ( final long code ) {
        return repository.findByCode( code );
    }

    /**
     * This method asks the repository to return all the active or inactive CPT
     * codes in the program
     *
     * @param isArchived
     *            Archive status to look for
     * @return all of the CPT codes in the program with a certain archive status
     */
    public List<CPTCode> findByIsArchived ( final boolean isArchived ) {
        return repository.findByIsArchived( isArchived );
    }

    /**
     * Builds a CPT Code from a form.
     *
     * @param the
     *            form with the information to build the CPT Code
     * @return the CPT Code that was built from the form
     */
    public CPTCode build ( final CPTCodeForm form ) {
        final CPTCode cpt = new CPTCode();
        final CPTCode existing = findByCode( form.getCode() );
        cpt.setCode( form.getCode() );
        cpt.setCost( form.getCost() );
        cpt.setDescription( form.getDescription() );
        cpt.setIsArchived( form.getIsArchived() );
        cpt.setTimeRangeMax( form.getTimeRangeMax() );
        cpt.setTimeRangeMin( form.getTimeRangeMin() );
        cpt.setVersion( form.getVersion() );
        if ( existing != null ) {
            cpt.setId( existing.getId() );
        }
        return cpt;
    }
}
