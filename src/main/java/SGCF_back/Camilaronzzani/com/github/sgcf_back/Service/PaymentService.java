package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.PaymentDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PaymentRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Payment;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.CustomerRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {
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
            throw new RuntimeException(e);
        }
    }

    public Payment findById(long id) {
        try {
            Payment payment = paymentRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment no find"));
            log.info("Payment {} found successfully" , id);
            return payment;

        } catch (Exception e) {
            log.error("Error in PaymentService.findById",e);
            throw new RuntimeException(e);
        }
    }

    public String save(PaymentRequest paymentRequest) {
        try {
            Payment payment = toPayment(paymentRequest);
            paymentRepository.save(payment);

            log.info("Payment saved successfully");
            return "Payment of customer: " + payment.getCustomer().getName() + " saved successfully ";

        } catch (Exception e) {
            log.error("Error in PaymentService.save ",e);
            throw new RuntimeException(e);
        }
    }

    public Payment toPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setCustomer(findCustomer(paymentRequest.getCustomerId()));
        payment.setTotalAccount(paymentRequest.getTotalAccount());
        payment.setStatus(paymentRequest.getStatus() == null
                ? Status.Pending
                : paymentRequest.getStatus());
        payment.setActive(true);
        return payment;
    }

    public Payment changeDataByPayment(long id, PaymentRequest newPayment) {
        Payment paymentOld = paymentRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment no find"));

        paymentOld.setCustomer(customerRepository.findById( newPayment.getCustomerId()).orElseThrow(()
                                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found")));
        paymentOld.setTotalAccount(newPayment.getTotalAccount());
        paymentOld.setStatus(newPayment.getStatus());

        return paymentOld ;
    }

    @Transactional
    public String update(PaymentRequest paymentRequest, long id) {
        try {
            Payment payment = changeDataByPayment(id, paymentRequest);

            log.info("Payment {} saved successfully", payment.getId());
            return "Payment: " + payment.getId() + " updated successfully ";
        } catch (Exception e) {
            log.error("Error in PaymentService.update ",e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String delete(long id) {
        try {
            Payment payment = paymentRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment no find"));
            payment.setActive(false);

            log.info("Payment {} deleted successfully" , id);
            return "Payment: " + payment.getId() + " deleted successfully ";
        } catch (Exception e) {
            log.error("Error in PaymentService.delete ",e);
            throw new RuntimeException(e);
        }
    }

    // to delete
    public String applyPartialUpdate(long id, Map<String, Object> payment) {
        try {
            Payment payment1 = paymentRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment no find"));
            payment.forEach((key, value) -> {
                switch (key) {
                    case "totalAccount" -> payment1.setTotalAccount(Double.parseDouble(value.toString()));
                    case "status" -> payment1.setStatus(Status.valueOf(value.toString()));
                    case "customerId" -> payment1.setCustomer(findCustomer(Long.parseLong(value.toString())));
                }
            });
            paymentRepository.save(payment1);
            return "Payment: " + payment1.getId() + " update successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Payment> findAllActive() {
        try {
            log.info("Payment list active found successfully");
            return paymentRepository.findByActiveTrue();

        } catch (Exception e) {
            log.error("Error in PaymentService.findAllActive ",e);
            throw new RuntimeException(e);
        }
    }

    public List<Payment> findByCustomer(long customerId) {
        try {
            log.info("Fetched payments for customer {} " , customerId);
            return paymentRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            log.error("Error in PaymentService.findByCustomer ",e);
            throw new RuntimeException(e);
        }
    }

    public List<Payment> findByStatus(Status status) {
        try {
            log.info("Fetched payments for status: {} " , status);
            return paymentRepository.findByStatus(status);
        } catch (Exception e) {
            log.error("Error in PaymentService.findByStatus ",e);
            throw new RuntimeException(e);
        }
    }

    private Customer findCustomer(Long customerId) {
        try {
            if (customerId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId is required");
            }
            log.info("Fetched  customer {} " , customerId);
            return customerRepository.findById(customerId).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));

        } catch (Exception e) {
            log.error("Error in PaymentService.findCustomer ",e);
            throw new RuntimeException(e);
        }
    }
}
