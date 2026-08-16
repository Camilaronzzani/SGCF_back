package SGCF_back.Camilaronzzani.com.github.sgcf_back.Service;

import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.CustomerDto;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Controller.DTOs.Request.CustomerRequest;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Customer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.CountryCustomer;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Entity.Enum.Language;
import SGCF_back.Camilaronzzani.com.github.sgcf_back.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<CustomerDto> findAll() {
        try {
        List<Customer> customerList = customerRepository.findAll();
        List<CustomerDto> customerDtoList = new ArrayList<>();
        customerList.forEach(customer -> {
           CustomerDto customerDto = CustomerDto.toDto(customer);
           customerDtoList.add(customerDto);
        });
        return customerDtoList;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public CustomerDto findById(long id) {
        try {
            Optional<Customer> customer = Optional.of(customerRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer no find")));
            return CustomerDto.toDto(customer.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String save(CustomerRequest customerRequest) {
        try {
            Customer customer = toCustomer(customerRequest);
            customerRepository.save(customer);
            return "customer: " + customer.getName()+ " save successful ";
        } catch (Exception e) {
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

    public void changeDataByCustomer(Customer oldCustomer, Customer newCustomer){
        oldCustomer.setCountryCustomer(newCustomer.getCountryCustomer());
        oldCustomer.setCnpj(newCustomer.getCnpj());
        oldCustomer.setCpf(newCustomer.getCpf());
        oldCustomer.setName(newCustomer.getName());
        oldCustomer.setEmail(newCustomer.getEmail());
        oldCustomer.setLanguageSpeak(newCustomer.getLanguageSpeak());
    }

    public String update(CustomerRequest customerRequest, long id) {
        try {
            Customer customerNew = toCustomer(customerRequest);
            Customer customerOld = Optional.of(customerRepository.findById(id).orElseThrow(()
                            -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer no find"))).get();
            changeDataByCustomer(customerOld , customerNew);
            customerRepository.save(customerOld);
            return "customer: " + customerOld.getName() + " save successful ";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String delete(long id) {
        try {
            Customer customer = customerRepository.findById(id).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer no find"));
            customer.setActive(false);
            customerRepository.save(customer);
            return "customer: " + customer.getName() + " delete successful ";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
            List<Customer> customerList = customerRepository.findByActiveTrue();
            List<CustomerDto> customerDtoList = new ArrayList<>();
            customerList.forEach(customer -> {
               CustomerDto customerDto = CustomerDto.toDto(customer);
               customerDtoList.add(customerDto);
            });
            return customerDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
