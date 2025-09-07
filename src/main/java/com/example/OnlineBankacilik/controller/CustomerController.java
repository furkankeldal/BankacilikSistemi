package com.example.OnlineBankacilik.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.OnlineBankacilik.dto.CustomerRequestDto;
import com.example.OnlineBankacilik.dto.CustomerResponseDto;
import com.example.OnlineBankacilik.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@PostMapping
	public ResponseEntity<CustomerResponseDto> addCustomer(@Valid @RequestBody CustomerRequestDto dto){
		return ResponseEntity.ok(customerService.add(dto));
		
	}
	
	@GetMapping
	public ResponseEntity<List<CustomerResponseDto>> getAllCustomer(){
		return ResponseEntity.ok(customerService.allList());
	}
	
	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerResponseDto> getByCustomerId(@PathVariable Long customerId){
	
		return ResponseEntity.ok(customerService.getById(customerId));
	}
	
	@PutMapping("/{customerId}")
	public ResponseEntity<CustomerResponseDto> update(@PathVariable Long customerId ,@Valid @RequestBody CustomerRequestDto dto){
		return ResponseEntity.ok(customerService.update(customerId, dto));
	}
	
	@DeleteMapping("/{customerId}")
	public ResponseEntity<Void>  deleteCustomerById(@PathVariable Long customerId){
		customerService.delete(customerId);
		return ResponseEntity.noContent().build();
		
		
	}
	
	
}
