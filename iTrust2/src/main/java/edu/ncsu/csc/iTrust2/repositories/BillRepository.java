package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;

public interface BillRepository extends JpaRepository<Bill, Long> {
    public Bill findByVisit ( OfficeVisit o );

    public List<Bill> findByIsPaid ( boolean isPaid );
}
