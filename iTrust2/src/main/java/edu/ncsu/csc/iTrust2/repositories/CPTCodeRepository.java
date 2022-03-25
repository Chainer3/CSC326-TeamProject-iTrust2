package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.OfficeVisit;

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
     * This method returns a list of CPT codes added to an office visit.
     *
     * @param v
     *            Office visit being looked into
     * @return a list of CPT codes that were added to the office visit.
     */
    public List<CPTCode> findByOfficeVisit ( OfficeVisit v );

    /**
     * This method returns all the active CPT codes in the program
     *
     * @return all of the CPT codes in the program
     */
    public List<CPTCode> findAllNotArchived ();

    /**
     * This method returns all the archived CPT codes in the program
     *
     * @return all of the archived CPT codes in the program
     */
    public List<CPTCode> findAllArchived ();
}
