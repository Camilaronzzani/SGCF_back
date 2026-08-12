package Service;

import Controller.DTOs.CustomerDto;
import Controller.DTOs.Request.CustomerRequest;
import Entity.Customer;
import Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
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

    public String salve(CustomerRequest customerRequest) {
        try {
            Customer customer = toCustomer(customerRequest);
            return "customer: " + customer.getName()+ " salve successful ";
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
}
