package controller;

import java.sql.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.*;

public class Controller {

  public static Statement stmt;
  public static Connection con;
  private User user;
  private static Controller controller ;

  private Controller() {
    startController();
  }

  public static Controller getInstance(){
    if(controller == null){
      controller = new Controller();
    }
    return controller;
  }

  private void startController() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      this.con = DriverManager.getConnection(
          "jdbc:mysql://localhost:3306/Order_Processing_System?characterEncoding=latin1&useConfigs=maxPerformance", "root", "root");
      Controller.stmt = this.con.createStatement();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getMessage());

    }

    // delete dates longer than 3 months
    String query = "delete from `sales`"
        + "WHERE sellingDate  < (current_Date() - Interval 3 Month)";
    try {
      Controller.stmt.executeUpdate(query);
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e.getMessage());

    }
  }

  public User login(String name, String password) {
    String query = "select * from `user`" + "WHERE"
        + " name  = " +" \'"+ name+"\' "+" and password = "+"\'"+  password+"\'";
    try {
      User us = null;
      ResultSet rs = Controller.stmt.executeQuery(query);
      if (rs.next()) {
        boolean isManager = rs.getBoolean("isManager");
        if (isManager) {
          us = new Manager();
        } else {
          us = new User();
        }
        us.setEmail(rs.getString("Email"));
        us.setF_name(rs.getString("Fname"));
        us.setL_name(rs.getString("Lname"));
        us.setPassword(rs.getString("password"));
        us.setTel_number(rs.getString("phoneNumber"));
        us.setShipping_address(rs.getString("shippingAddress"));
        us.setUser_id(rs.getString("name"));
      }
      if(us == null){
        JOptionPane.showMessageDialog(null, "Wrong UserName/Password");
      }
      this.user = us;
      return us;
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e.getMessage());
    }
    JOptionPane.showMessageDialog(null, "Wrong UserName/Password");

    return null;
  }

  public boolean signup(User user) {
    String query;
    if (user.getShipping_address() != null && user.getTel_number() != null) {
      query = "Insert into `user` (name,password,Lname,Fname"
          + ",Email,phoneNumber,shippingAddress)" + " "
          + "values ( "
          + "\'"+ user.getUser_id() +"\'"+  " ,"
          + "\'"+ user.getPassword() +"\'"+ " , "
          + "\'"+ user.getL_name() +"\'"+  " , "
          + "\'"+ user.getF_name()+"\'"+ " , "
          + "\'"+ user.getEmail()    +"\'" + " , " 
          + "\'"+ user.getTel_number() +"\'"+  " , "
          + "\'"+ user.getShipping_address() +"\'"+  " ) ";
    } else if (user.getShipping_address() == null
        && user.getTel_number() == null) {
      query = "Insert into `user` (name,password,Lname,Fname" + ",Email)"
          + " values ( " 
          + "\'"+user.getUser_id() +"\'"+ " , "
          + "\'"+user.getPassword() +"\'"+ " , " 
          + "\'"+user.getL_name() +"\'"+ " , "
          + "\'"+user.getF_name()+"\'"+ " , "
          + "\'"+user.getEmail() +"\'"+ " ) ";
    } else if (user.getShipping_address() == null) {
      query = "Insert into `user` (name,password,Lname,Fname"
          + ",Email,phoneNumber)" + " values ( " 
          +"\'"+ user.getUser_id() +"\'"+ " , "
          +"\'"+ user.getPassword() +"\'"+ " , "
          +"\'"+ user.getL_name() +"\'"+ " , "
          +"\'"+ user.getF_name() +"\'"+ " , "
          +"\'"+user.getEmail() +"\'"+ " , "
          +"\'"+ user.getTel_number() + "\'"+" ) ";
    } else {
      query = "Insert into `user` (name,password,Lname,Fname"
          + ",Email,shippingAddress)" + " values ( " 
          +"\'"+ user.getUser_id() +"\'"+ " , "
          +"\'"+ user.getPassword() +"\'"+ " , " 
          +"\'"+ user.getL_name() +"\'"+ " , "
          +"\'"+ user.getF_name() +"\'"+ " , "
          +"\'"+ user.getEmail() +"\'"+ " , "
          +"\'"+ user.getShipping_address() + "\'"+" ) ";
    }
    try {
      Controller.stmt.executeUpdate(query);
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e.getMessage());
      return false;
    }
    return true;

  }

  public boolean editInformation(User us) {
    try {
      us.edit_information();
      this.user = us;
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e.getMessage());
      return false;
    }
    return true;
  }

  public ArrayList<Book> searchBook(String attrubite, String value) {
    try {
      ResultSet rs = (this.user).search_book(attrubite, value);
      ArrayList<Book> books = new ArrayList<>();
      while (rs.next()) {
        Book book = new Book();
        book.setCategory(rs.getString("category"));
        book.setISBN(rs.getInt("ISBN"));
        book.setNo_of_copies(rs.getInt("numberOfCopies"));
        book.setSelling_price(rs.getDouble("price"));
        book.setPublisher_name(rs.getString("publisherName"));
        book.setThreshold(rs.getInt("threshold"));
        book.setTitle(rs.getString("title"));
        book.setYear(rs.getString("year"));
        books.add(book);
       
      }
      for(Book book:books){
        String query = " Select authorName from bookauthor where ISBN = "+book.getISBN() ;
        ResultSet rs2 =  Controller.stmt.executeQuery(query); 
        ArrayList<String> authors = new ArrayList<>();
        while (rs2.next()) {
          authors.add(rs2.getString("authorName"));
        }
        book.setAuthors(authors);
      }
      return books;
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, e.getMessage());
    }
    return null;
  }

  public boolean checkout() {
    try {
      this.con.setAutoCommit(false);
      this.user.check_out();
      this.con.commit();
      JOptionPane.showMessageDialog(null,"Commit Done");

    } catch (SQLException e1) {
      try {
        this.con.rollback();
        JOptionPane.showMessageDialog(null,"RollBack Done");

        return false;
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;
      }
    } finally {

      try {
        this.con.setAutoCommit(true);
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;
      }
    }
    return true;
  }

  // -----------------------------------manager--------------------------

  public boolean place_order(Order order) {
    if (user instanceof Manager) {
      try {
        ((Manager) this.user).place_order(order);
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
      return false;
    }
    return true;
  }

  public boolean confirm_order(Order order) {
    if (user instanceof Manager) {
      try {
        ((Manager) this.user).confirm_order(order);
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
      return false;
    }
    return true;
  }

  public ArrayList<User> get_top_five_customers() {
    if (user instanceof Manager) {
      try {
        ResultSet rs = ((Manager) this.user).get_top_five_customers();
        ArrayList<User> users = new ArrayList<>();
        while (rs.next()) {
          User us = new User();
          us.setSales_number(rs.getInt("sum(salesNumber)"));
          us.setUser_id(rs.getString("userName"));
          users.add(us);
        }
        return users;
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
    }
    return null;
  }

  public ArrayList<Book> get_top_ten_books() {
    if (user instanceof Manager) {
      try {
        ResultSet rs = ((Manager) this.user).get_top_ten_books();
        ArrayList<Book> books = new ArrayList<>();
        while (rs.next()) {
          Book book = new Book();
          book.setISBN(rs.getInt("ISBN"));
          book.setSalesNumber(rs.getInt("sum(salesNumber)"));
          books.add(book);
        }
        return books;
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
    }
    return null;
  }

  public ArrayList<User> get_all_customers() {
    if (user instanceof Manager) {
      try {
        ResultSet rs = ((Manager) this.user).get_all_customers();
        ArrayList<User> users = new ArrayList<>();
        while (rs.next()) {
          boolean isManager = rs.getBoolean("isManager");
          User us;
          if (isManager) {
            us = new Manager();
          } else {
            us = new User();
          }
          us.setEmail(rs.getString("Email"));
          us.setF_name(rs.getString("Fname"));
          us.setL_name(rs.getString("Lname"));
          us.setTel_number(rs.getString("phoneNumber"));
          us.setShipping_address(rs.getString("shippingAddress"));
          us.setUser_id(rs.getString("name"));
          users.add(us);
        }
        return users;
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
    }
    return null;
  }

  public boolean promote(User us) {
    if (user instanceof Manager) {
      try {
        us.promote();

      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;

      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
      return false;

    }
    return true;

  }

  public boolean add_publisher(Publisher publisher) {
    if (user instanceof Manager) {
      try {
        ((Manager) this.user).add_publisher(publisher);

      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;

      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
      return false;

    }
    return true;

  }

  public ArrayList<Order> get_all_order(){
    ArrayList<Order> orders = new ArrayList<Order>();
    if (user instanceof Manager) {
      try {
        ResultSet rs = ((Manager) this.user).get_all_order();

        while (rs.next()) {
          Order o = new Order();
          o.setOrder_id(rs.getInt("OrderId"));
          o.setISBN(rs.getInt("ISBN"));
          o.setNo_of_copies(rs.getInt("quantity"));
          orders.add(o);
          
        }
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return null;

      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
      return null;

    }
    return orders;
  }

  public void close(){
    try {
    con.close();
    System.out.println("closed");
  } catch (SQLException e) {
    e.printStackTrace();
  }
  }

  public boolean add_book(Book book) {
    if (user instanceof Manager) {
      try {
        ((Manager) this.user).add_book(book);
      } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
      return false;
    }
    return true;
  }

  public boolean modify_book(Book book) {
    if (user instanceof Manager) {
      try {
        ((Manager) this.user).modify_book(book);
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
      return false;
    }
    return true;
  }

  public boolean promote_customer(User user) {
    if (user instanceof Manager) {
      try {
        ((Manager) this.user).promote_customer(user);
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
        return false;
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
      return false;
    }
    return true;

  }

  public Double get_total_sales() {
    if (user instanceof Manager) {
      try {
        ResultSet rs = ((Manager) this.user).get_total_sales();
        if (rs.next()) {
          return rs.getDouble(1);
        } else {
          return 0.0;
        }
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
      }
    } else {
      JOptionPane.showMessageDialog(null, "Unauthorized Access");
    }
    return null;
  }

}
