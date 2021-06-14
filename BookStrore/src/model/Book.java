package model;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import controller.Controller;
public class Book {
  
  private int ISBN ;
  private String title ;
  private String publisher_name ;
  private ArrayList<String> authors;
  private String year ;
  private double selling_price ;
  private int no_of_copies ;
  private int threshold ;
  private String category ;
  private int salesNumber;
  
  public Book(){
    this.selling_price = 20;
    this.no_of_copies = 0;
    this.threshold = 0;
    authors = new ArrayList<>();
  }

  public int getISBN() {
    return ISBN;
  }

  public void setISBN(int ISBN) {
    this.ISBN = ISBN;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPublisher_name() {
    return publisher_name;
  }

  public void setPublisher_name(String publisher_name) {
    this.publisher_name = publisher_name;
  }

  public ArrayList<String> getAuthors() {
    return authors;
  }

  public void setAuthors(ArrayList<String> authors) {
    this.authors = authors;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public double getSelling_price() {
    return selling_price;
  }

  public void setSelling_price(double selling_price) {
    this.selling_price = selling_price;
  }

  public int getNo_of_copies() {
    return no_of_copies;
  }

  public void setNo_of_copies(int no_of_copies) {
    this.no_of_copies = no_of_copies;
  }

  public int getThreshold() {
    return threshold;
  }

  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public int getSalesNumber() {
    return salesNumber;
  }

  public void setSalesNumber(int salesNumber) {
    this.salesNumber = salesNumber;
  }

  public void modify_book() throws SQLException{
    String query = "Update book SET title = " +"\""+ this.title +"\""+ " ,"
        +"publisherName = " +"\""+ this.publisher_name +"\""+ " ,"
        +"category = " +"\""+ this.category +"\""+ " ,"
        +"numberOfCopies = " + this.no_of_copies + " ,"
        +"price = " + this.selling_price + " ,"
        +"threshold = " + this.threshold ;        
    if(!this.year.equals("")){
      query += " ,year = " + "\""+this.year+"\"" ;
    }
    query  += " WHERE ISBN =  " + this.ISBN;

    Controller.stmt.executeUpdate(query); 
    query = "delete from BookAuthor where "
        + "ISBN = "+ this.ISBN ;
    Controller.stmt.executeUpdate(query); 
    for(String author : this.authors){
      query = "Insert into BookAuthor (ISBN,authorName)"
          + "Value ( "+ this.ISBN+ " , " +"\'"+ author +"\'"+" )";
      Controller.stmt.executeUpdate(query); 
    }
  }

  public void add_book_to_Database() throws SQLException {
    String query ;
    query = "Insert into Book (ISBN,title,publisherName,category"
            + ",numberOfCopies,price,threshold)"
            + " values ( "
            +"\'"+ this.ISBN +"\'"+ " , "
            +"\'"+this.title +"\'"+ " , "
            +"\'"+this.publisher_name +"\'"+ " , "
            +"\'"+this.category +"\'"+ " , "
            +this.no_of_copies + " , "
            +this.selling_price + " , "
            +this.threshold + " ) ";

    Controller.stmt.executeUpdate(query);
    if(!this.year.equals("")){
      this.modify_book();
    }
    query = "Delete from BookAuthor Where "
            + "ISBN = "+ this.ISBN;
    Controller.stmt.executeUpdate(query);

    for(String author : this.authors){
      query = "Insert into BookAuthor (ISBN,authorName)"
              + "Value ( "+ this.ISBN+ " , " +"\'"+ author +"\'"+" )";
      Controller.stmt.executeUpdate(query);
    }

  }

  @Override
  public int hashCode()
  {
    return this.ISBN;
  }
  
  @Override
  public boolean equals( Object book ) {
    if(book  instanceof Book){
      return this.ISBN == ((Book)book).ISBN;
    }
    return false;
  }
}
