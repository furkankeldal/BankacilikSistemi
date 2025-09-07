package com.example.OnlineBankacilik.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CustomerResponseDto {

	    private Long customerId;
	    private String nameSurname;
	    private String tcKimlikNo;
	    private String number;
	    private String email;
	    private int numberOfAccount;
	    private LocalDateTime registrationDate;

}
