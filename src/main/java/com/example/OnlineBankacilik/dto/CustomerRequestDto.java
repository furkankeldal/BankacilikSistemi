package com.example.OnlineBankacilik.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestDto {

	  @NotBlank
	    private String nameSurname;
	    @NotBlank @Size(min=11, max=11)
	    private String tcKimlikNo;
	    @NotBlank
	    private String number;
	    @Email @NotBlank
	    private String email;
}
