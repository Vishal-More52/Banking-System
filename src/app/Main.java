package app;

import service.BankService;
import service.impl.BankServiceImpl;

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

            switch (choice){
                case  "0" -> running = false;
                case "1" -> openAccount(scanner,bankService);
                case "2" -> deposit(scanner);
                case "3" -> withdraw(scanner);
                case "4" -> transfer(scanner);
                case "5" -> statement(scanner);
                case "6" -> listAccounts(scanner,bankService);
                case "7" -> searchAccounts(scanner);

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
        String amoutStr = scanner.nextLine().trim();
        Double initial = Double.valueOf(amoutStr);
        String accountNumber = bankService.openAccount(name,email,type);

        System.out.println("Account opened: " + accountNumber);

    }

    private static void deposit(Scanner scanner) {
    }

    private static void withdraw(Scanner scanner) {
    }

    private static void transfer(Scanner scanner) {
    }

    private static void statement(Scanner scanner) {
    }

    private static void listAccounts(Scanner scanner, BankService bankService) {
        bankService.listAccounts().forEach(a -> {
            System.out.println(a.getAccountNumber() + " | " + a.getAccountType() + " | " + a.getBalance());
        });
    }

    private static void searchAccounts(Scanner scanner) {
    }
}
