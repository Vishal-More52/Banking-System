package service.impl;

import domain.Account;
import domain.Customer;
import domain.Transaction;
import domain.Type;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.ValidationException;
import repository.AccountRepository;
import repository.CustomerRepository;
import repository.TransactionRepository;
import service.BankService;
import util.Validation;

import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BankServiceImpl implements BankService {
private final AccountRepository accountRepository = new AccountRepository();
private final TransactionRepository transactionRepository = new TransactionRepository();
private final CustomerRepository customerRepository = new CustomerRepository();

//Name validation
private final Validation<String> validateName = name ->{
  if(name == null || name.isBlank()) throw new ValidationException("Name is required");
};
//Email validation
    private final Validation<String> validateEmail = email ->{
        if (email == null || !email.contains("@")) throw new ValidationException("Email is required");
};
//    Account type validation
    private final Validation<String> validateType = type ->{
    if(type == null || !(type.equalsIgnoreCase("SAVINGS") || type.equalsIgnoreCase("CURRENT")))
        throw new ValidationException("Type must be SAVINGS or CURRENT");

};
//    Validate Positive Amount
private final Validation<Double> validateAmountPositive = amount ->{
    if (amount == null || amount <= 0) throw new ValidationException("Please Enter valid amount");
};


//    Open Account Functionality
    @Override
    public String openAccount(String name, String email, String accountType) {
        validateName.validate(name);
        validateEmail.validate(email);
        validateType.validate(accountType);

        String customerId = UUID.randomUUID().toString();

        //create customer
        Customer c = new Customer(customerId,name,email);
        customerRepository.save(c);
        String accountNumber = getAccountNumber();
        Account account = new Account(accountNumber, customerId, 0.0, accountType);
        accountRepository.save(account);
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
        validateAmountPositive.validate(amount);
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        account.setBalance(account.getBalance() + amount);
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), Type.DEPOSIT,
                account.getAccountNumber(), amount, LocalDateTime.now(), note);
        transactionRepository.add(transaction);
    }

    @Override
    public void withdraw(String accountNumber, Double amount, String note) {
        validateAmountPositive.validate(amount);
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        account.setBalance(account.getBalance() - amount);
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), Type.WITHDRAW,
                account.getAccountNumber(), amount, LocalDateTime.now(), note);
        transactionRepository.add(transaction);
    }

    @Override
    public void transfer(String fromAcc, String toAcc, Double amount, String note) {
        validateAmountPositive.validate(amount);
        if (fromAcc.equals(toAcc))
            throw new ValidationException("Cannot transfer to your own account");
        Account from = accountRepository.findByNumber(fromAcc)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + fromAcc));
        Account to = accountRepository.findByNumber(toAcc)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + toAcc));
        if (from.getBalance() < amount)
            throw new InsufficientFundsException("Insufficient Balance");

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        Transaction fromTransaction = new Transaction(UUID.randomUUID().toString(), Type.TRANSFER_OUT,
                from.getAccountNumber(), amount, LocalDateTime.now(), note);
        transactionRepository.add(fromTransaction);

        Transaction toTransaction = new Transaction(UUID.randomUUID().toString(), Type.TRANSFER_IN,
                to.getAccountNumber(), amount, LocalDateTime.now(), note);
        transactionRepository.add(toTransaction);


    }

    @Override
    public List<Transaction> getStatement(String account) {
        return transactionRepository.findByAccount(account).stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> searchAccountByCustomerName(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        String query = q.trim().toLowerCase();
        return customerRepository.findAll().stream()
                .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(query))
                .flatMap(c -> accountRepository.findByCustomerId(c.getId()).stream())
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    public Validation<Double> getValidateAmountPositive() {
        return validateAmountPositive;
    }


}
