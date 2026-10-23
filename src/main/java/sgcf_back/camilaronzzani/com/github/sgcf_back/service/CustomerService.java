package sgcf_back.camilaronzzani.com.github.sgcf_back.service;

import sgcf_back.camilaronzzani.com.github.sgcf_back.exception.ServiceException;
import sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.CustomerRequest;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.Customer;
import sgcf_back.camilaronzzani.com.github.sgcf_back.repositories.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


import static sgcf_back.camilaronzzani.com.github.sgcf_back.controller.dto.request.CustomerRequest.toCustomer;

@Slf4j
@Service
public class CustomerService {

    private static final String CUSTOMER_NOT_FOUND = "customer not found";

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAll() {
        try {
        List<Customer> customerList = customerRepository.findAll();

        log.info("Customers listed successfully");
        return customerList;
        } catch (Exception e) {
            log.error("Error in CustomerService.findAll()", e);
            throw new ServiceException(e);
        }
    }

    public Customer findById(long id) {
        try {
            Customer customer = customerRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_NOT_FOUND));
            log.info("Customer found successfully {}" , customer);
            return customer;
        } catch (Exception e) {
            log.error("Error in CustomerService.findById()", e);
            throw new ServiceException(e);
        }
    }

    public void save(CustomerRequest customerRequest) {
        try {
            Customer customer = toCustomer(customerRequest);
            customerRepository.save(customer);
            log.info("Customer saved  successfully: {}" , customer);
        } catch (Exception e) {
            log.error("Error in CustomerService.save()", e);
            throw new ServiceException(e);
        }
    }


    public Customer changeDataByCustomer(long id, CustomerRequest newCustomer){
        Customer customerOld = customerRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_NOT_FOUND));

        customerOld.setCountryCustomer(newCustomer.countryCustomer());
        customerOld.setCnpj(newCustomer.cnpj());
        customerOld.setCpf(newCustomer.cpf());
        customerOld.setName(newCustomer.name());
        customerOld.setEmail(newCustomer.email());
        customerOld.setLanguageSpeak(newCustomer.languageSpeak());

        return customerOld ;
    }

    @Transactional
    public Customer update(CustomerRequest customerRequest, long id) {
        try {
            Customer customer = changeDataByCustomer( id , customerRequest);

            log.info("Customer {} saved successfully ", customer.getName());
            return customer;

        } catch (Exception e) {
            log.error("Error in CustomerService.update()", e);
            throw new ServiceException(e);
        }
    }

    @Transactional
    public void delete(long id) {
        try {
            Customer customer = customerRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_NOT_FOUND));

            customer.setActive(false);

            log.info("Customer {} deactivated successfully" , customer.getName());

        } catch (Exception e) {
            log.error("Error in CustomerService.delete " , e );
            throw new ServiceException(e);
        }
    }

       public List<Customer> findAllActive() {
        try {
            log.info("Customers listed actived successfully");
            return customerRepository.findByActiveTrue();

        } catch (Exception e) {
            log.error("Error in CustomerService.findAllActive " , e );
            throw new ServiceException(e);
        }
    }
}
