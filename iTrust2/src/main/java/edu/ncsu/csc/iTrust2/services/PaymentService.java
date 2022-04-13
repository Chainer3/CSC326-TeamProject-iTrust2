package edu.ncsu.csc.iTrust2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.repositories.PaymentRepository;

@Component
@Transactional
public class PaymentService extends Service<Payment, Long> {
    @Autowired
    private PaymentRepository repository;

    @Override
    protected JpaRepository<Payment, Long> getRepository () {
        return repository;
    }

    // public List<Payment> findByBill ( final Bill bill ) {
    // return repository.findByBill( bill );
    // }
    /**
     * Builds a payment from a form
     *
     * @param form
     *            Form to create a payment from
     * @return a payment with the information from the form
     */
    public Payment build ( final PaymentForm form ) {
        final Payment payment = new Payment();
        payment.setAmount( form.getAmount() );
        payment.setDate( form.getDate() );
        payment.setPaymentMethod( form.getPaymentMethod() );
        return payment;
    }
}
