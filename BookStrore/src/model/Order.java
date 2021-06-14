package model;

import java.sql.SQLException;

import controller.Controller;

public class Order {
  private int order_id ;
  private int ISBN ;
  private int no_of_copies ;

  public int getOrder_id() {
    return order_id;
  }

  public void setOrder_id(int order_id) {
    this.order_id = order_id;
  }

  public int getISBN() {
    return ISBN;
  }

  public void setISBN(int ISBN) {
    this.ISBN = ISBN;
  }

  public int getNo_of_copies() {
    return no_of_copies;
  }

  public void setNo_of_copies(int no_of_copies) {
    this.no_of_copies = no_of_copies;
  }

  public void delete_order() throws SQLException{
    String query = "delete from `order`";
    query +=" WHERE orderId =  " + this.order_id;
    Controller.stmt.executeUpdate(query);  
  }

  public void add_to_order_table()  throws SQLException{
    String query ;
    query = "Insert into `order` (ISBN,orderId,quantity)"
            + " values ( " + this.ISBN + " , "
            +this.order_id + " , "
            +this.no_of_copies + " ) ";
    Controller.stmt.executeUpdate(query);
  }

}
