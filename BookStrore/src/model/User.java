package model;
import controller.Controller;
import java.sql.*;
import java.util.*;

public class User {
  private String user_id ;
  private String password ;
  private String f_name ;
  private String l_name ;
  private String email ;
  private String tel_number ;
  private String shipping_address ;
  private int sales_number;

  private LinkedHashMap<Book,Integer> shoppingCart ;
  
  public User(){
    this.shoppingCart = new LinkedHashMap<>();
    
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getF_name() {
    return f_name;
  }

  public void setF_name(String f_name) {
    this.f_name = f_name;
  }

  public String getL_name() {
    return l_name;
  }

  public void setL_name(String l_name) {
    this.l_name = l_name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTel_number() {
    return tel_number;
  }

  public void setTel_number(String tel_number) {
    this.tel_number = tel_number;
  }

  public String getShipping_address() {
    return shipping_address;
  }

  public void setShipping_address(String shipping_address) {
    this.shipping_address = shipping_address;
  }

  public int getSales_number() {
    return sales_number;
  }

  public void setSales_number(int sales_number) {
    this.sales_number = sales_number;
  }

  public void setShoppingCart(LinkedHashMap<Book, Integer> shoppingCart) {
    this.shoppingCart = shoppingCart;
  }

  public LinkedHashMap<Book, Integer> getShoppingCart() {
    return this.shoppingCart;
  }

  public void remove_book_from_shopping_cart(int ISBN){
    for(Book book : this.shoppingCart.keySet()){
      if(book.getISBN() == ISBN){
        this.shoppingCart.remove(book);
        break;
      }
    }
  }

  public double get_shopping_cart_price(){
    double t = 0;
    for(Book book : this.shoppingCart.keySet()){
      t += book.getSelling_price()*this.shoppingCart.get(book);
    }
    return t;
  }

  public void check_out() throws SQLException {
    for(Book book : this.shoppingCart.keySet()){
      int to_buy = this.shoppingCart.get(book);
      int numberOfCopies = book.getNo_of_copies()-to_buy;
      String query = "Update book"
              +" SET  numberOfCopies = " + numberOfCopies ;
      query  += " WHERE ISBN =  " + book.getISBN();
      Controller.stmt.executeUpdate(query);
      String add = "Insert into sales (ISBN,userName,sellingDate,"
              + "sellingTime,salesNumber,price)"
              + "values (" + book.getISBN()+ " ,"
              + " "
              +"\'"+this.user_id +"\'"+ " ,"
              + " current_date() ,  current_time() , "+ to_buy + " , " + to_buy * book.getSelling_price() + " ) ";
      Controller.stmt.executeUpdate(add);
    }
    this.shoppingCart = new LinkedHashMap<>();

  }

  public void promote() throws SQLException{
    String query = "Update `user` SET isManager = true";
    query +=" WHERE name =  " + "\'"+this.user_id+"\'";
    Controller.stmt.executeUpdate(query);
  }

  public void remove_All_books(){
    this.shoppingCart = new LinkedHashMap<>();
  }

  public void edit_information() throws SQLException{
    String query = "Update `user` SET"
        + " password = " 
        +"\'"+ this.password +"\'"+" ,"
        +"FName = " 
        +"\'"+ this.f_name +"\'"+ " ,"
        +"LName = " 
        +"\'"+ this.l_name + "\' ,"
        +"email = " 
        +"\'"+ this.email +"\'";
    if(this.tel_number != null){
       query  += " , phoneNumber = " + "\'"+this.tel_number+"\'" ;
    }
    if(this.shipping_address != null){
      query  += " , shippingAddress = " +"\'"+ this.shipping_address +"\'";
    }
    query +=" WHERE name =  " + "\'"+this.user_id+"\'";
    Controller.stmt.executeUpdate(query);  
  }
  
  public ResultSet search_book(String attrubite , String value) throws SQLException{
    if(attrubite.equals("author")){
      return search_book_by_author(value);
    }
    
    String query;
    if(attrubite.equals("ISBN") ||attrubite.equals("price") || attrubite.equals("numberOfCopies") ||  attrubite.equals("threshold")){
      query= "Select * from Book where " + attrubite + " = " + value;
    }else{
      query= "Select * from Book where " + attrubite + " = " +"\'" +value+"\'";

    }
    
    return Controller.stmt.executeQuery(query); 
  }
  
  private ResultSet search_book_by_author(String author) throws SQLException{
    String query = "Select * from book where ISBN in ( select "
        + "ISBN from bookAuthor where authorName = " 
        +"\'"+ author +"\'"+ " )";
    return Controller.stmt.executeQuery(query); 
  }
  
  public void add_book_to_shopping_cart(Book book , int to_buy){
    this.shoppingCart.put(book,to_buy);
  }

  public void add_book_to_shopping_cart(Book book){
    this.shoppingCart.put(book,1);
  }

  
}
