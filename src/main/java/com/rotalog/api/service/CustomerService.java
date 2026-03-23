package com.rotalog.api.service;

import com.rotalog.api.dto.CustomerDTO;
import com.rotalog.api.exception.BusinessException;
import com.rotalog.api.exception.ResourceNotFoundException;
import com.rotalog.api.model.Customer;
import com.rotalog.api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerDTO.Response> listAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO.Response> listActive() {
        return customerRepository.findByActiveTrue()
                .stream()
                .map(CustomerDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerDTO.Response findById(Long id) {
        return customerRepository.findById(id)
                .map(CustomerDTO.Response::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public CustomerDTO.Response create(CustomerDTO.Request request) {
        if (request.getTaxId() != null && customerRepository.existsByTaxId(request.getTaxId())) {
            throw new BusinessException("A customer with tax ID already exists: " + request.getTaxId());
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .taxId(request.getTaxId())
                .type(request.getType() != null ? request.getType() : Customer.CustomerType.INDIVIDUAL)
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .birthDate(request.getBirthDate())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return CustomerDTO.Response.fromEntity(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDTO.Response update(Long id, CustomerDTO.Request request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (request.getTaxId() != null && !request.getTaxId().equals(customer.getTaxId())) {
            if (customerRepository.existsByTaxId(request.getTaxId())) {
                throw new BusinessException("A customer with tax ID already exists: " + request.getTaxId());
            }
        }

        customer.setName(request.getName());
        customer.setTaxId(request.getTaxId());
        if (request.getType() != null) customer.setType(request.getType());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setBirthDate(request.getBirthDate());
        if (request.getActive() != null) customer.setActive(request.getActive());

        return CustomerDTO.Response.fromEntity(customerRepository.save(customer));
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Transactional
    public CustomerDTO.Response toggleStatus(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setActive(!customer.getActive());
        return CustomerDTO.Response.fromEntity(customerRepository.save(customer));
    }

    public Customer findEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
