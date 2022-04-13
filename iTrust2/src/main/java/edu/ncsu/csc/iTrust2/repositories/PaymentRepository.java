package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // public List<Payment> findByBill ( Bill bill );
}
