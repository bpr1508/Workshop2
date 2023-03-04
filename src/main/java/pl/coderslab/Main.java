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
        System.out.println("2. Wczytaj jeden wiersz o danym id.");
        System.out.println("3. Zmień dane użytkownika o danym id.");
        System.out.println("4. Usuń dane użytkownika o danym id.");
        System.out.println("5. Pokaż wszystkie dane użytkowników.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.println("1. Tworzenie nowego użytkownika.");
            System.out.println("2. Wczytaj jeden wiersz o danym id.");
            System.out.println("3. Zmień dane użytkownika o danym id.");
            System.out.println("4. Usuń dane użytkownika o danym id.");
            System.out.println("5. Pokaż wszystkie dane użytkowników.");
            System.out.print("Nieprawidłowe dane. Podaj jeszcze raz: ");
        }
        int option = scanner.nextInt();
        switch (option) {
            case 1:
                userDao.create(user);
                break;
            case 2: {
                int userId = userDao.inputId();
                if(userDao.read(userId)!=null){
                    user = userDao.read(userId);
                    System.out.println(user.getId() + " | " + user.getEmail() + " | " + user.getUsername() + " | " + user.getPassword());
                } else {
                    System.out.println("null");
                }
            }
            break;
            case 3:
                userDao.update(user);
                break;
            case 4:
                userDao.delete(userDao.inputId());
                break;
            case 5:
                User[] findAll = userDao.findAll();
                for (int i = 0; i < findAll.length; i++) {
                    System.out.println(findAll[i]);
                }
                break;
            default:
                System.out.println("Niepoprawna wartość");
                break;
        }
    }
}