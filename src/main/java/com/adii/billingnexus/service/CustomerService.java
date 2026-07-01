package com.adii.billingnexus.service;

import com.adii.billingnexus.dto.request.CreateCustomerRequest;
import com.adii.billingnexus.dto.response.CustomerResponse;
import com.adii.billingnexus.entity.Customer;
import com.adii.billingnexus.exception.ResourceNotFoundException;
import com.adii.billingnexus.multitenancy.TenantContext;
import com.adii.billingnexus.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = Customer.builder()
                .tenantId(TenantContext.getTenantId()) // stamps ownership at creation time
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .build();

        customer = customerRepository.save(customer);
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deactivateCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getAddress(), c.isActive(), c.getCreatedAt());
    }
}