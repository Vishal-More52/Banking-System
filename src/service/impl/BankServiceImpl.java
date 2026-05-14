package service.impl;

import domain.Account;
import domain.Transaction;
import domain.Type;
import repository.AccountRepository;
import repository.TransactionRepository;
import service.BankService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BankServiceImpl implements BankService {
private final AccountRepository accountRepository = new AccountRepository();
private final TransactionRepository transactionRepository = new TransactionRepository();
//    Open Account Functionality
    @Override
    public String openAccount(String name, String email, String accountType) {
        String customerId = UUID.randomUUID().toString();
        //change later
//        String accountNumber = UUID.randomUUID().toString();
        String accountNumber = getAccountNumber();
        Account account = new Account(accountNumber, customerId, 0.0, accountType);
        accountRepository.save(account);
        System.out.println("Account Created Successfully");

        return accountNumber;
    }
    private String getAccountNumber() {
        int size = accountRepository.findAll().size() + 1;
        return String.format("AC%06d", size);

    }

    @Override
    public List<Account> listAccounts() {
        return accountRepository.findAll().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @Override
    public void deposit(String accountNumber, Double amount, String note) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be a positive number.");
        }
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        account.setBalance(account.getBalance() + amount);
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), Type.DEPOSIT,
                account.getAccountNumber(), amount, LocalDateTime.now(), note);
        transactionRepository.add(transaction);
    }


}
