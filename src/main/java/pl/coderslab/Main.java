package pl.coderslab;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.entity.User;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        User user = new User(0, "", "", "");
        UserDao userDao = new UserDao();
        System.out.println("Menu:");
        System.out.println("1. Tworzenie nowego użytkownika.");
        System.out.println("2. Wczytaj jeden wiersz.");
        System.out.println("3. Zmień dane użytkownika.");
        System.out.println("4. Pokaż wszystkie wiersze w tabeli.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.println("1. Tworzenie nowego użytkownika.");
            System.out.println("2. Wczytaj jeden wiersz.");
            System.out.println("3. Zmień dane użytkownika.");
            System.out.println("4. Pokaż wszystkie wiersze w tabeli.");
            System.out.print("Nieprawidłowe dane. Podaj jeszcze raz:");
        }
        int option = scanner.nextInt();
        switch (option) {
            case 1:
                userDao.create(user);
                break;
            case 2: {
                int userId = 0;
                while (!orPositive(userId)) {
                    userId = inputId();
                }
                if(userDao.read(userId)!=null){
                    user = userDao.read(userId);
                    System.out.println(user.getId() + " | " + user.getEmail() + " | " + user.getUsername() + " | " + user.getPassword());
                } else {
                    System.out.println("Nie znaleziono takiego id w bazie...");
                }
            }
            break;
            case 3:
                break;
            case 4:
            default:
                System.out.println("Niepoprawna wartość");
        }
    }

    public static boolean validationId(int input) {
        if (orPositive(input)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean orPositive(int input) {
        Pattern pattern = Pattern.compile("^[1-9]{1}[0-9]*$");
        Matcher matcher = pattern.matcher(String.valueOf(input));
        return matcher.find();
    }

    public static int inputId() {
        System.out.print("Podaj liczbę dodatnią: ");
        Scanner scan = new Scanner(System.in);
        while (!scan.hasNextInt()) {
            scan.next();
            System.out.print("Nieprawidłowe dane. Podaj jeszcze raz:");
        }
        int number = scan.nextInt();
        return number;
    }
}