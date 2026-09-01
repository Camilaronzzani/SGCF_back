package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.CustomerDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.CustomerRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryCustomer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.CustomerRepository;
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
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<CustomerDto> findAll() {
        try {
        List<CustomerDto> customerDtoList = customerRepository.findAll()
                .stream()
                .map(CustomerDto ::toDto)
                .toList();

        log.info("Customers listed successfully");
        return customerDtoList;
        } catch (Exception e) {
            log.error("Error in CustomerService.findAll()", e);
            throw new RuntimeException(e);
        }
    }

    public CustomerDto findById(long id) {
        try {
            Customer customer = customerRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));
            log.info("Customer found successfully {}" , customer);
            return CustomerDto.toDto(customer);
        } catch (Exception e) {
            log.error("Error in CustomerService.findById()", e);
            throw new RuntimeException(e);
        }
    }

    public String save(CustomerRequest customerRequest) {
        try {
            Customer customer = toCustomer(customerRequest);
            customerRepository.save(customer);
            log.info("Customer saved  successfully: {}" , customer);
            return "Customer: " + customer.getName()+ " saved successfully ";
        } catch (Exception e) {
            log.error("Error in CustomerService.save()", e);
            throw new RuntimeException(e);
        }
    }
    public Customer toCustomer(CustomerRequest customerRequest){
        Customer customer = new Customer();
        customer.setCountryCustomer(customerRequest.getCountryCustomer());
        customer.setCnpj(customerRequest.getCnpj());
        customer.setCpf(customerRequest.getCpf());
        customer.setName(customerRequest.getName());
        customer.setLanguageSpeak(customerRequest.getLanguageSpeak());
        customer.setEmail(customerRequest.getEmail());
        customer.setActive(true);
        return customer;
    }

    public Customer changeDataByCustomer(long id, CustomerRequest newCustomer){
        Customer customerOld = customerRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));

        customerOld.setCountryCustomer(newCustomer.getCountryCustomer());
        customerOld.setCnpj(newCustomer.getCnpj());
        customerOld.setCpf(newCustomer.getCpf());
        customerOld.setName(newCustomer.getName());
        customerOld.setEmail(newCustomer.getEmail());
        customerOld.setLanguageSpeak(newCustomer.getLanguageSpeak());

        return customerOld ;
    }

    @Transactional
    public String update(CustomerRequest customerRequest, long id) {
        try {
            Customer customer = changeDataByCustomer( id , customerRequest);

            log.info("Customer {} saved successfully ", customer.getName());
            return "Customer: " + customer.getName() + " saved successfully ";

        } catch (Exception e) {
            log.error("Error in CustomerService.update()", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String delete(long id) {
        try {
            Customer customer = customerRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));

            customer.setActive(false);

            log.info("Customer {} deactivated successfully" , customer.getName());
            return "Customer: " + customer.getName() + " deactivated successful ";

        } catch (Exception e) {
            log.error("Error in CustomerService.delete " , e );
            throw new RuntimeException(e);
        }
    }

    //speak with the  teacher to delete this
    public String applyPartialUpdate(long id, Map<String, Object> customer) {
        try {
            Customer customer1 = customerRepository.findById(id).orElseThrow(()
                        ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "customer no find"));
            customer.forEach((key , value) ->{
                switch (key){
                    case "cnpj" -> customer1.setCnpj((String) value);

                    case "cpf" -> customer1.setCpf((String) value) ;

                    case "name" -> customer1.setName((String) value);

                    case "languageSpeak" -> customer1.setLanguageSpeak((List<Language>) value);

                    case "countryCustomer" -> customer1.setCountryCustomer((CountryCustomer) value);

                    case "email" -> customer1.setEmail((String) value);
                }
            });
            customerRepository.save(customer1);
            return "customer: " + customer1.getName() + " delete successful ";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<CustomerDto> findAllActive() {
        try {
            log.info("Customers listed actived successfully");
            return customerRepository.findByActiveTrue()
                    .stream()
                    .map(CustomerDto::toDto)
                    .toList();

        } catch (Exception e) {
            log.error("Error in CustomerService.findAllActive " , e );
            throw new RuntimeException(e);
        }
    }
}
