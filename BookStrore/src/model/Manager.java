package model;

import java.sql.*;

import controller.Controller;

public class Manager extends User {

  public void add_book(Book book) throws SQLException {
    book.add_book_to_Database();
  }

  public void modify_book(Book book) throws SQLException {
    book.modify_book();
  }

  public void place_order(Order order) throws SQLException {
    order.add_to_order_table();

  }

  public ResultSet get_top_five_customers() throws SQLException {
    String query = "Select userName , sum(salesNumber) from sales "
        + "group by userName "
        + "order by  2 DESC "
        + "LIMIT 5";
    return Controller.stmt.executeQuery(query);
  }

  public ResultSet get_top_ten_books() throws SQLException {
    String query = "Select ISBN , sum(salesNumber) from sales "
        + "group by ISBN "
        + "order by  2 DESC "
        + "limit 10";
    return Controller.stmt.executeQuery(query);
  }

  public ResultSet get_all_customers() throws SQLException {
    String query = "Select * from `user` where isManager = false";
    return Controller.stmt.executeQuery(query);
  }

  public void add_publisher(Publisher publisher) throws SQLException {
      publisher.add_Publisher();
  }

  public void confirm_order(Order order) throws SQLException {
    order.delete_order();
  }

  public void promote_customer(User user) throws SQLException {
    user.promote();
  }

  public ResultSet get_total_sales() throws SQLException {
    String query = "Select sum(salesNumber*price) from sales where  "
            + "sellingDate between (current_Date() - Interval 1 Month) "
            + "And Current_Date()";
    return Controller.stmt.executeQuery(query);

  }

  public ResultSet get_all_order() throws SQLException {
    String query = "Select * from `order`";
    return Controller.stmt.executeQuery(query);

  }


}
