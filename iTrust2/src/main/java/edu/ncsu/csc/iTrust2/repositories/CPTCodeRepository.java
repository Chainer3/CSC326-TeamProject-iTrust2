package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * This interface provides direct interactions with the database
 *
 * @author Nikolaus Johnson
 *
 */
public interface CPTCodeRepository extends JpaRepository<CPTCode, Long> {
    /**
     * This method returns a CPT code based on the number the CPT code is given
     *
     * @param code
     *            Code to look for
     * @return the CPT code with that number
     */
    public CPTCode findByCode ( long code );

    /**
     * This method returns a list of CPT codes based on the archive status of a
     * CPT code
     *
     * @param isArchived
     *            Archive status being searched for
     * @return a list of CPT codes with that status
     */
    public List<CPTCode> findByIsArchived ( boolean isArchived );
}
