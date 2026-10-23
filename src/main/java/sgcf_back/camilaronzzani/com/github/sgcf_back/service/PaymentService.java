package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import sgcf_back.camilaronzzani.com.github.sgcf_back.exception.ServiceException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.PaymentRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.enums.Status;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Payment;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.CustomerRepository;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Slf4j
@Service
public class PaymentService {

    private static final String PAYMENT_NOT_FOUND = "payment no find";
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public List<Payment> findAll() {
        try {
            log.info("Payment list found successfully");
            return paymentRepository.findAll();
        } catch (RuntimeException e) {
            log.error("Error in PaymentService.findAll",e);
            throw new ServiceException(e);
        }
    }

    public Payment findById(long id) {
        try {
            Payment payment = paymentRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PAYMENT_NOT_FOUND));
            log.info("Payment {} found successfully" , id);
            return payment;

        } catch (Exception e) {
            log.error("Error in PaymentService.findById",e);
            throw new ServiceException(e);
        }
    }

    public void save(PaymentRequest paymentRequest) {
        try {
            Payment payment = toPayment(paymentRequest);
            paymentRepository.save(payment);

            log.info("Payment saved successfully");

        } catch (Exception e) {
            log.error("Error in PaymentService.save ",e);
            throw new ServiceException(e);
        }
    }

    public Payment toPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setCustomer(findCustomer(paymentRequest.customerId()));
        payment.setTotalAccount(paymentRequest.totalAccount());
        payment.setStatus(paymentRequest.status() == null
                ? Status.Pending
                : paymentRequest.status());
        payment.setActive(true);
        return payment;
    }

    public Payment changeDataByPayment(long id, PaymentRequest newPayment) {
        Payment paymentOld = paymentRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, PAYMENT_NOT_FOUND));

        paymentOld.setCustomer(customerRepository.findById( newPayment.customerId()).orElseThrow(()
                                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found")));
        paymentOld.setTotalAccount(newPayment.totalAccount());
        paymentOld.setStatus(newPayment.status());

        return paymentOld ;
    }

    @Transactional
    public Payment update(PaymentRequest paymentRequest, long id) {
        try {
            Payment payment = changeDataByPayment(id, paymentRequest);

            log.info("Payment {} saved successfully", payment.getId());
            return payment;
        } catch (Exception e) {
            log.error("Error in PaymentService.update ",e);
            throw new ServiceException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {
            Payment payment = paymentRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, PAYMENT_NOT_FOUND));
            payment.setActive(false);

            log.info("Payment {} deleted successfully" , id);
        } catch (Exception e) {
            log.error("Error in PaymentService.delete ",e);
            throw new ServiceException(e);
        }
    }

    public List<Payment> findAllActive() {
        try {
            log.info("Payment list active found successfully");
            return paymentRepository.findByActiveTrue();

        } catch (Exception e) {
            log.error("Error in PaymentService.findAllActive ",e);
            throw new ServiceException(e);
        }
    }

    public List<Payment> findByCustomer(long customerId) {
        try {
            log.info("Fetched payments for customer {} " , customerId);
            return paymentRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            log.error("Error in PaymentService.findByCustomer ",e);
            throw new ServiceException(e);
        }
    }

    public List<Payment> findByStatus(Status status) {
        try {
            log.info("Fetched payments for status: {} " , status);
            return paymentRepository.findByStatus(status);
        } catch (Exception e) {
            log.error("Error in PaymentService.findByStatus ",e);
            throw new ServiceException(e);
        }
    }

    public Customer findCustomer(Long customerId) {
        try {
            if (customerId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId is required");
            }
            log.info("Fetched  customer {} " , customerId);
            return customerRepository.findById(customerId).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));

        } catch (Exception e) {
            log.error("Error in PaymentService.findCustomer ",e);
            throw new ServiceException(e);
        }
    }
}
