package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.PaymentDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.PaymentRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Status;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Payment;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.CustomerRepository;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public List<PaymentDto> findAll() {
        try {
            List<Payment> paymentList = paymentRepository.findAll();
            List<PaymentDto> paymentDtos = new ArrayList<>();
            paymentList.forEach(payment -> {
                PaymentDto paymentDto = PaymentDto.toDto(payment);
                paymentDtos.add(paymentDto);
            });
            return paymentDtos;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public PaymentDto findById(long id) {
        try {
            Optional<Payment> payment = Optional.of(paymentRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment no find")));
            return PaymentDto.toDto(payment.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(PaymentRequest paymentRequest) {
        try {
            Payment payment = toPayment(paymentRequest);
            paymentRepository.save(payment);
            return "Payment of customer: " + payment.getCustomer().getName() + " save successful ";
        } catch (Exception e) {
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

    public void changeDataByPayment(Payment paymentOld, Payment newPayment) {
        paymentOld.setCustomer(newPayment.getCustomer());
        paymentOld.setTotalAccount(newPayment.getTotalAccount());
        paymentOld.setStatus(newPayment.getStatus());
    }

    public String update(PaymentRequest paymentRequest, long id) {
        try {
            Payment payment = toPayment(paymentRequest);
            Payment paymentOld = Optional.of(paymentRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment no find"))).get();
            changeDataByPayment(paymentOld, payment);
            paymentRepository.save(paymentOld);
            return "Payment: " + paymentOld.getId() + " update successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String delete(long id) {
        try {
            Payment payment = paymentRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment no find"));
            payment.setActive(false);
            paymentRepository.save(payment);
            return "Payment: " + payment.getId() + " delete successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    public List<PaymentDto> findAllActive() {
        try {
            List<Payment> paymentList = paymentRepository.findByActiveTrue();
            List<PaymentDto> paymentDtoList = new ArrayList<>();
            paymentList.forEach(payment -> {
                PaymentDto paymentDto = PaymentDto.toDto(payment);
                paymentDtoList.add(paymentDto);
            });
            return paymentDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<PaymentDto> findByCustomer(long customerId) {
        try {
            return paymentRepository.findByCustomerId(customerId)
                    .stream()
                    .map(PaymentDto::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<PaymentDto> findByStatus(Status status) {
        try {
            return paymentRepository.findByStatus(status)
                    .stream()
                    .map(PaymentDto::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Customer findCustomer(Long customerId) {
        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId is required");
        }
        return customerRepository.findById(customerId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer no find"));
    }
}
