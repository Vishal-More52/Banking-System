package service;

import domain.Account;
import domain.Transaction;
import service.impl.BankServiceImpl;

import java.util.List;

public interface BankService  {
    String openAccount(String name, String email, String accountType);
    List<Account> listAccounts();

    void deposit(String accountNumber, Double amount, String note);
    void withdraw(String accountNumber,Double amount, String withdrawal);
    void transfer(String from, String to, Double amount, String note);

    List<Transaction> getStatement(String account);

    List<Account> searchAccountByCustomerName(String q);
}
