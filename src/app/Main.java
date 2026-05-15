package app;

import domain.Account;
import service.BankService;
import service.impl.BankServiceImpl;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankService bankService = new BankServiceImpl();

        boolean running = true;

        System.out.println("Welcome to Console Bank");
        while (running) {
            System.out.println("""
                    1) Open Account
                    2) Deposit
                    3) Withdraw
                    4) Transfer
                    5) Account Statement
                    6) List Accounts
                    7) Search Accounts By Customer Name
                    0) Exit                    
                    """);

            System.out.println("CHOOSE: ");
            String choice = scanner.nextLine().trim();
            System.out.println("Choice : " + choice);

            switch (choice) {
                case "0" -> running = false;
                case "1" -> openAccount(scanner, bankService);
                case "2" -> deposit(scanner, bankService);
                case "3" -> withdraw(scanner, bankService);
                case "4" -> transfer(scanner, bankService);
                case "5" -> statement(scanner, bankService);
                case "6" -> listAccounts(scanner, bankService);
                case "7" -> searchAccounts(scanner, bankService);

            }
        }
    }

    private static void openAccount(Scanner scanner, BankService bankService) {
        System.out.println("Customer name: ");
        String name = scanner.nextLine().trim();
        System.out.println("Customer email: ");
        String email = scanner.nextLine().trim();
        System.out.println("Account Type (SAVINGS/CURRENT: ");
        String type = scanner.nextLine().trim();
        System.out.println("Initial deposit (optional, blank for 0): ");
        String amountStr = scanner.nextLine().trim();
        double initial = 0;
        if (!amountStr.isEmpty()) {
            try {
                initial = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number; opening with 0 balance.");
            }
        }
        if (initial < 0) {
            System.out.println("Initial deposit cannot be negative; using 0.");
            initial = 0;
        }
        String accountNumber = bankService.openAccount(name, email, type);
        if (initial > 0) {
            try {
                bankService.deposit(accountNumber, initial, "Initial Deposit");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Account opened: " + accountNumber);

    }

    private static void deposit(Scanner scanner, BankService bankService) {
        System.out.println("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.println("Amount: ");
        String amountStr = scanner.nextLine().trim();
        try {
            double parsed = Double.parseDouble(amountStr);
            bankService.deposit(accountNumber, parsed, "Deposit");
            System.out.println("Deposited");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }


    private static void withdraw(Scanner scanner, BankService bankService) {
        System.out.println("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        System.out.println("Amount: ");
        String amountStr = scanner.nextLine().trim();
        try {
            double parsed = Double.parseDouble(amountStr);
            bankService.withdraw(accountNumber, parsed, "Withdrawal");
            System.out.println("Withdrawn");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void transfer(Scanner scanner, BankService bankService) {
        System.out.println("From Account: ");
        String from = scanner.nextLine().trim();
        System.out.println("To Account: ");
        String to = scanner.nextLine().trim();
        System.out.println("Amount: ");
        Double amount = Double.valueOf(scanner.nextLine().trim());
        bankService.transfer(from, to, amount, "Transfer");
    }

    private static void statement(Scanner scanner, BankService bankService) {
        System.out.println("Account number: ");
        String account = scanner.nextLine().trim();
        bankService.getStatement(account).forEach(t -> {
            System.out.println(t.getTimestamp() + " | " + t.getType() + " | " + t.getAmount() + " | " + t.getNote());
        });
    }

    private static void listAccounts(Scanner scanner, BankService bankService) {
        bankService.listAccounts().forEach(a -> {
            System.out.println(a.getAccountNumber() + " | " + a.getAccountType() + " | " + a.getBalance());
        });
    }

    private static void searchAccounts(Scanner scanner, BankService bankService) {
        System.out.println("Customer name Contains: ");
        String q = scanner.nextLine().trim();
        List<Account> results = bankService.searchAccountByCustomerName(q);
        if (results.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        results.forEach(account ->
                System.out.println(account.getAccountNumber() + " | " + account.getAccountType() + " | " + account.getBalance())
        );
    }
}
