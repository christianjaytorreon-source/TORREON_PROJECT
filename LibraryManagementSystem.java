import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        LibrarySystem lib = new LibrarySystem();
        if (lib.login()) {
            lib.showMenu();
        } else {
            System.out.println("Login failed. Exiting system...");
        }
    }
}

class Person {
    protected String id;
    protected String name;
    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

class User extends Person {
    private String password;
    private String role;
    private ArrayList<String> borrowedBooks = new ArrayList<>();
    public User(String id, String name, String password, String role) {
        super(id, name);
        this.password = password;
        this.role = role;
    }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isAdmin() { return role.equalsIgnoreCase("admin"); }
    public ArrayList<String> getBorrowedBooks() { return borrowedBooks; }
    public boolean canBorrowMore() { return borrowedBooks.size() < 3; }
    public void addBorrowedBook(String bookId) { borrowedBooks.add(bookId); }
    public void removeBorrowedBook(String bookId) { borrowedBooks.remove(bookId); }
}

class Book {
    private String bookId, title, author;
    private boolean available;
    public Book(String bookId, String title, String author, boolean available) {
        this.bookId = bookId; this.title = title; this.author = author; this.available = available;
    }
    public void displayBookDetails() {
        String status = available ? "Available" : "Borrowed";
        System.out.printf("%-8s %-25s %-20s %-10s%n", bookId, title, author, status);
    }
    public String getBookId() { return bookId; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

class Transaction {
    private String transactionId, userId, bookId, dateBorrowed, dateReturned;
    public Transaction(String tid, String uid, String bid, String db, String dr) {
        transactionId = tid; userId = uid; bookId = bid; dateBorrowed = db; dateReturned = dr;
    }
    public void setDateReturned(String dr) { dateReturned = dr; }
    public String getBookId() { return bookId; }
    public String getUserId() { return userId; }
    public String getDateReturned() { return dateReturned; }
    public void displayTransaction() {
        String returned = dateReturned.equals("null") ? "Not Returned" : dateReturned;
        System.out.printf("%-8s %-8s %-8s %-15s %-15s%n",
            transactionId, userId, bookId, dateBorrowed, returned);
    }
}

class LibrarySystem {
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private User loggedInUser;
    private Scanner sc = new Scanner(System.in);

    public LibrarySystem() {
        users.add(new User("U001", "John", "123", "user"));
        users.add(new User("A001", "Admin", "admin", "admin"));
        books.add(new Book("B001", "1984", "George Orwell", true));
        books.add(new Book("B002", "The Great Gatsby", "F. Scott Fitzgerald", true));
        books.add(new Book("B003", "To Kill a Mockingbird", "Harper Lee", false));
    }

    public boolean login() {
        System.out.print("Username: ");
        String name = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();
        for (User u : users) {
            if (u.getName().equalsIgnoreCase(name) && u.getPassword().equals(pass)) {
                loggedInUser = u;
                System.out.println("Login successful! Welcome, " + name + "!");
                return true;
            }
        }
        System.out.println("Login failed.");
        return false;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n1. View Books\n2. Borrow Book\n3. Return Book");
            if (loggedInUser.isAdmin()) System.out.println("4. View Transactions");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine();
            switch (ch) {
                case "1": viewBooks(); break;
                case "2": borrowBook(); break;
                case "3": returnBook(); break;
                case "4": if (loggedInUser.isAdmin()) viewTransactions(); break;
                case "0": return;
                default: System.out.println("Invalid."); 
            }
        }
    }

    private void viewBooks() {
        System.out.printf("%-8s %-25s %-20s %-10s%n", "ID", "Title", "Author", "Status");
        for (Book b : books) b.displayBookDetails();
    }

    private void borrowBook() {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine();
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(id)) {
                if (!b.isAvailable()) { System.out.println("Already borrowed."); return; }
                b.setAvailable(false);
                loggedInUser.addBorrowedBook(id);
                transactions.add(new Transaction("T" + (transactions.size()+1),
                        loggedInUser.getId(), id, today(), "null"));
                System.out.println("Book borrowed!");
                return;
            }
        }
        System.out.println("Book not found.");
    }

    private void returnBook() {
        System.out.print("Enter Book ID to return: ");
        String id = sc.nextLine();
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(id)) {
                b.setAvailable(true);
                loggedInUser.removeBorrowedBook(id);
                for (Transaction t : transactions)
                    if (t.getBookId().equals(id) && t.getUserId().equals(loggedInUser.getId()) &&
                        t.getDateReturned().equals("null"))
                        t.setDateReturned(today());
                System.out.println("Book returned!");
                return;
            }
        }
        System.out.println("Book not found.");
    }

    private void viewTransactions() {
        System.out.printf("%-8s %-8s %-8s %-15s %-15s%n", "T_ID", "User", "Book", "Borrowed", "Returned");
        for (Transaction t : transactions) t.displayTransaction();
    }

    private String today() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
                }
