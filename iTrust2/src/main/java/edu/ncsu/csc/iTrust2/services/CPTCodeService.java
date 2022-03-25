package edu.ncsu.csc.iTrust2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.models.OfficeVisit;
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
     * This method asks the repository to return a list of CPT codes added to an
     * office visit.
     *
     * @param v
     *            Office visit being looked into
     * @return a list of CPT codes that were added to the office visit.
     */
    public List<CPTCode> findByOfficeVisit ( final OfficeVisit v ) {
        return repository.findByOfficeVisit( v );
    }

    /**
     * This method asks the repository to return all the active CPT codes in the
     * program
     *
     * @return all of the CPT codes in the program
     */
    public List<CPTCode> findAllNotArchived () {
        return repository.findAllNotArchived();
    }

    /**
     * This method asks the repository to return all the archived CPT codes in
     * the program
     *
     * @return all of the archived CPT codes in the program
     */
    public List<CPTCode> findAllArchived () {
        return repository.findAllArchived();
    }
}
