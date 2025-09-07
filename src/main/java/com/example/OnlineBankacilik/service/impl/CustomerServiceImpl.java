package com.example.OnlineBankacilik.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.OnlineBankacilik.dto.CustomerRequestDto;
import com.example.OnlineBankacilik.dto.CustomerResponseDto;
import com.example.OnlineBankacilik.entity.Customer;
import com.example.OnlineBankacilik.exception.CustomerNotFoundException;
import com.example.OnlineBankacilik.repository.CustomerRepository;
import com.example.OnlineBankacilik.service.CustomerService;



@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	
	private CustomerResponseDto toDto(Customer c) {
		CustomerResponseDto crd = new CustomerResponseDto();
		crd.setCustomerId(c.getId());
		crd.setNameSurname(c.getNameSurname());
		crd.setNumber(c.getNumber());
		crd.setEmail(c.getEmail());
		crd.setTcKimlikNo(c.getTcKimlikNo());
		crd.setRegistrationDate(c.getRegistrationDate());
		crd.setNumberOfAccount(c.getAccounts()== null ? 0 : c.getAccounts().size());
		
		return crd;
			
	}

	@Override
	public CustomerResponseDto add(CustomerRequestDto dto) {
		Customer customer = new Customer();
		customer.setNameSurname(dto.getNameSurname());
		customer.setTcKimlikNo(dto.getTcKimlikNo());
		customer.setEmail(dto.getEmail());
		customer.setNumber(dto.getNumber());
		
		return toDto(customerRepository.save(customer));
	}

	@Override
	public List<CustomerResponseDto> allList() {
		
		List<Customer> customers= customerRepository.findAll();
		if(customers.isEmpty()) {
			throw new CustomerNotFoundException("kullanıcı bulunamadı");
		}
		return customers.stream().map(this::toDto).toList();
	}

	@Override
	public CustomerResponseDto getById(Long id) {
		
		return customerRepository.findById(id).map(this::toDto).orElseThrow(()-> new CustomerNotFoundException(id));
	}

	@Override
	public CustomerResponseDto update(Long id, CustomerRequestDto dto) {
		Customer customer = customerRepository.findById(id).orElseThrow(()-> new CustomerNotFoundException(id));
		customer.setNameSurname(dto.getNameSurname());
		customer.setEmail(dto.getEmail());
		customer.setNumber(dto.getNumber());
		customer.setTcKimlikNo(dto.getTcKimlikNo());
		
		return toDto(customerRepository.save(customer));
	}

	@Override
	public void delete(Long id) {
		if (!customerRepository.existsById(id)) throw new CustomerNotFoundException(id);
        customerRepository.deleteById(id);
	}
	

}
