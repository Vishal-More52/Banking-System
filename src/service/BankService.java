package service;

import service.impl.BankServiceImpl;

public interface BankService  {
    String openAccount(String name, String email, String accountType);
}
