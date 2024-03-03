package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class DemoApplication {
    @Autowired
    Bank bank;


    @Bean
    public CommandLineRunner startup() {
        return args -> {
            System.out.println("Hello");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        createAccount(scanner);
                        break;
                    case 2:
                        //deposit
                        depositOrWithdraw(scanner, 2);
                        break;
                    case 3:
                        //withdraw
                        depositOrWithdraw(scanner, 3);
                        break;
                    case 4:
                        checkBalance(scanner);
                        break;
                    case 5:
                        transfer(scanner);
                        break;
                    case 0:

                        System.out.println("Exiting the application. Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        };
    }

    private void displayMenu() {
        System.out.println("===== Banking System =====");
        System.out.println("1. Create Account");
        System.out.println("2. Perform Deposit");
        System.out.println("3. Perform Withdraw");
        System.out.println("4. Check Balance");
        System.out.println("5. Transfer");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private void createAccount(Scanner scanner) {
        System.out.print("Enter account holder's name: ");
        String accountHolder = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        double initialBalance = scanner.nextDouble();
        String accountNum = bank.createAccount(accountHolder, initialBalance);
        System.out.println("Account created successfully! Account Number: " + accountNum);
    }

    private void transfer(Scanner scanner) {
        System.out.print("Enter your account number: ");
        String accountHolder = scanner.nextLine();

        System.out.print("Enter target account number: ");
        String targetAccount = scanner.nextLine();


        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();

        bank.transfer(accountHolder, targetAccount, amount);

        System.out.println("Transfer completed successfully! Account holder: " + accountHolder + " Target account" + targetAccount);
    }

    private void depositOrWithdraw(Scanner scanner, int transactionType) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter transaction amount: ");
        double amount = scanner.nextDouble();

        if (transactionType == 2) {
            bank.deposit(accountNumber, amount);
            System.out.println("Deposit successful!");
        } else if (transactionType == 3) {
            bank.withdraw(accountNumber, amount);
            System.out.println("Withdrawal successful!");
        } else {
            System.out.println("Invalid transaction type.");
        }
    }

    private void checkBalance(Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        double balance = bank.getBalance(accountNumber);

        if (balance >= 0) {
            System.out.println("Account Balance: $" + balance);
        } else {
            System.out.println("Account not found.");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

    }
}

