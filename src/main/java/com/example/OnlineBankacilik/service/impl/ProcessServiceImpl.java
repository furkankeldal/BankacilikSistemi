package com.example.OnlineBankacilik.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.OnlineBankacilik.dto.ProcessRequestDto;
import com.example.OnlineBankacilik.dto.ProcessResponseDto;
import com.example.OnlineBankacilik.entity.Account;
import com.example.OnlineBankacilik.entity.FuturesAccount;
import com.example.OnlineBankacilik.entity.Process;
import com.example.OnlineBankacilik.enums.TransactionType;
import com.example.OnlineBankacilik.exception.AccountNotFoundException;
import com.example.OnlineBankacilik.exception.InsufficientBalanceException;
import com.example.OnlineBankacilik.exception.InvalidAmountException;
import com.example.OnlineBankacilik.repository.AccountRepository;
import com.example.OnlineBankacilik.repository.ProcessRepository;
import com.example.OnlineBankacilik.service.ProcessService;
import lombok.experimental.var;


@Service
@Transactional
public class ProcessServiceImpl implements ProcessService {

	@Autowired
	private ProcessRepository processRepository;
	@Autowired
	private AccountRepository accountRepository;
	
	private Account getAccount(String accountNo) {
		return accountRepository.findById(accountNo).orElseThrow(()-> new AccountNotFoundException(accountNo));
	}
	
	private String nextTxnCode() {
		Long c = processRepository.count() + 1;
		return String.format("TXN-%03d", c);
		
	}
	
	private ProcessResponseDto toDto(Process pr) {
		ProcessResponseDto prd = new ProcessResponseDto();
		prd.setCustomerId(pr.getId());
		prd.setNumberOfAccount(pr.getAccount().getAccountNo());
		prd.setTransactionType(pr.getTransactionType());
		prd.setRegistrationDate(pr.getTransactionDate());
		prd.setAmount(pr.getAmount());
		prd.setPreviousBalance(pr.getPreviousBalance());
		prd.setNewBalance(pr.getNewBalance());
		prd.setExplanation(pr.getExplanation());
		prd.setSuccesfull(pr.isSuccessful());
		
		return prd; 
		
	}
	
	
	
	@Override
	public ProcessResponseDto deposit(ProcessRequestDto dto) {
		if (dto.getAmount().compareTo(BigDecimal.ZERO)<=0) throw new InvalidAmountException();
		Account ac= getAccount(dto.getAccountNo());
		var former= ac.getAmount();
		ac.deposit(dto.getAmount());
		accountRepository.save(ac);
		
		Process process= new Process();
		process.setTransactionCode(nextTxnCode());
		process.setAccount(ac);
		process.setAmount(dto.getAmount());
		process.setTransactionType(TransactionType.YATIRMA);
		process.setNewBalance(ac.getAmount());
		process.setPreviousBalance(former);
		process.setExplanation(dto.getExplanation());
			
		return toDto(processRepository.save(process));
	}

	@Override
	public ProcessResponseDto withdraw(ProcessRequestDto dto) {
		if (dto.getAmount().compareTo(BigDecimal.ZERO)<=0)throw new InvalidAmountException(); 
		Account account= getAccount(dto.getAccountNo());
		var former =account.getAmount();
		if(former.compareTo(dto.getAmount())<0) throw new InsufficientBalanceException(former, dto.getAmount());
		account.withdraw(dto.getAmount());
		accountRepository.save(account);
		
		Process process= new Process();
		process.setTransactionCode(nextTxnCode());
		process.setAccount(account);
		process.setAmount(dto.getAmount());
		process.setTransactionType(TransactionType.CEKME);
		process.setNewBalance(account.getAmount());
		process.setPreviousBalance(former);
		process.setExplanation(dto.getExplanation());
		
		return toDto(processRepository.save(process));
	}

	@Override
	@Transactional(readOnly = true)
	public ProcessResponseDto amount(String accountNo) {
		Account acc = getAccount(accountNo);
		ProcessResponseDto prd = new ProcessResponseDto();
		prd.setNumberOfAccount(accountNo);
		prd.setNewBalance(acc.getAmount());
		prd.setTransactionType(TransactionType.YATIRMA);
		return prd;
	}

	@Override
	public ProcessResponseDto earnInterest(String accountNo) {
		Account a = getAccount(accountNo);
        if (!(a instanceof FuturesAccount fa)) {
            throw new RuntimeException("Sadece vadeli hesapta faiz islemi yapilabilir");
        }
        var former = a.getAmount();
        fa.interestProcessing();
        accountRepository.save(fa);
        Process process= new Process();
        process.setAccount(a);
        process.setTransactionCode(nextTxnCode());
        process.setAmount(a.getAmount().subtract(former));
        process.setTransactionType(TransactionType.FAIZ_ISLEME);
        process.setNewBalance(a.getAmount());
        process.setPreviousBalance(former);
        
        var dto = toDto(processRepository.save(process));
        dto.setInterestRate(fa.getInterestRate());
        return dto;
		
	}

	@Override
	public List<ProcessResponseDto> accountHistory(String accountNo) {
		Account acc = getAccount(accountNo);
        return processRepository.findByAccount(acc).stream().map(this::toDto).toList();
	}

}
