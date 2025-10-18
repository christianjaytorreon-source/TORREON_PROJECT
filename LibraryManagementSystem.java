import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        LibrarySystem lib = new LibrarySystem();
        if (lib.login()) lib.menu();
        else System.out.println("Login failed. Exiting...");
    }
}

class Person {
    protected String id, name;
    public Person(String id, String name) { this.id = id; this.name = name; }
    public String getId(){return id;} public String getName(){return name;}
    public void displayInfo(){ System.out.println(id + " - " + name); }
}

class User extends Person {
    private String pass, role;
    private ArrayList<String> borrowed = new ArrayList<>();
    public User(String id,String n,String p,String r){ super(id,n); pass=p; role=r; }
    public String getPass(){return pass;} public String getRole(){return role;}
    public boolean isAdmin(){return role.equalsIgnoreCase("admin");}
    public boolean canBorrow(){return borrowed.size()<3;}
    public void addBook(String id){ borrowed.add(id); }
    public void remBook(String id){ borrowed.remove(id); }
}

class Book {
    private String id,title,author; boolean avail;
    public Book(String i,String t,String a,boolean v){ id=i;title=t;author=a;avail=v; }
    public String getId(){return id;} public boolean isAvail(){return avail;}
    public void setAvail(boolean v){avail=v;}
    public void show(){System.out.printf("%-6s %-25s %-20s %-10s%n",id,title,author,(avail?"Available":"Borrowed"));}
}

class Trans {
    String tid,uid,bid,db,dr;
    public Trans(String t,String u,String b,String db,String dr){this.tid=t;this.uid=u;this.bid=b;this.db=db;this.dr=dr;}
    public void setReturn(String d){dr=d;}
    public void show(){System.out.printf("%-5s %-5s %-5s %-12s %-12s%n",tid,uid,bid,db,dr.equals("null")?"Not Returned":dr);}
}

class LibrarySystem {
    private ArrayList<User> users=new ArrayList<>();
    private ArrayList<Book> books=new ArrayList<>();
    private ArrayList<Trans> trans=new ArrayList<>();
    private User current; private Scanner sc=new Scanner(System.in);
    private DateTimeFormatter f=DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LibrarySystem(){
        users.add(new User("A001","Admin","admin123","admin"));
        users.add(new User("U001","John Doe","pass123","user"));
        users.add(new User("U002","Jane Smith","abc123","user"));
        books.add(new Book("B001","The Great Gatsby","F. Scott Fitzgerald",true));
        books.add(new Book("B002","To Kill a Mockingbird","Harper Lee",true));
        books.add(new Book("B003","1984","George Orwell",false));
    }

    public boolean login(){
        System.out.println("Welcome to Library System\n--------------------------");
        for(int i=3;i>0;i--){
            System.out.print("Username: ");String n=sc.nextLine();
            System.out.print("Password: ");String p=sc.nextLine();
            for(User u:users)
                if(u.getName().equalsIgnoreCase(n)&&u.getPass().equals(p)){
                    current=u; System.out.println("Login successful!\n"); return true;
                }
            System.out.println("Invalid. Attempts left: "+(i-1));
        }
        return false;
    }

    public void menu(){
        while(true){
            System.out.println("\n1.View Books\n2.Borrow\n3.Return");
            if(current.isAdmin()) System.out.println("4.View Transactions");
            System.out.println("0.Exit");
            System.out.print("Choice: "); String c=sc.nextLine();
            switch(c){
                case"1":view();break;
                case"2":borrow();break;
                case"3":ret();break;
                case"4":if(current.isAdmin())viewTrans();break;
                case"0":System.out.println("Goodbye!");return;
                default:System.out.println("Invalid");
            }
        }
    }

    private void view(){
        System.out.printf("%-6s %-25s %-20s %-10s%n","ID","Title","Author","Status");
        for(Book b:books)b.show();
    }

    private void borrow(){
        System.out.print("Book ID: ");String id=sc.nextLine();
        for(Book b:books)if(b.getId().equalsIgnoreCase(id)){
            if(!b.isAvail()){System.out.println("Already borrowed.");return;}
            if(!current.canBorrow()){System.out.println("Limit reached (3).");return;}
            b.setAvail(false); current.addBook(id);
            String tid="T"+String.format("%03d",trans.size()+1);
            trans.add(new Trans(tid,current.getId(),id,LocalDate.now().format(f),"null"));
            System.out.println("Borrowed successfully!");return;
        }
        System.out.println("Book not found.");
    }

    private void ret(){
        System.out.print("Book ID to return: ");String id=sc.nextLine();
        for(Book b:books)if(b.getId().equalsIgnoreCase(id)){
            b.setAvail(true); current.remBook(id);
            for(Trans t:trans)if(t.uid.equals(current.getId())&&t.bid.equals(id)&&t.dr.equals("null"))
                t.setReturn(LocalDate.now().format(f));
            System.out.println("Returned successfully!");return;
        }
        System.out.println("Book not found.");
    }

    private void viewTrans(){
        System.out.printf("%-5s %-5s %-5s %-12s %-12s%n","TID","User","Book","Borrowed","Returned");
        for(Trans t:trans)t.show();
    }
}
