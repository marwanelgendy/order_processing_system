package model;

import java.sql.SQLException;
import java.util.*;

import controller.Controller;

public class Publisher {
  private String name;
  private ArrayList<String> addresses ;
  private ArrayList<String> tel_numbers ;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<String> getAddresses() {
    return addresses;
  }

  public void setAddresses(ArrayList<String> addresses) {
    this.addresses = addresses;
  }

  public ArrayList<String> getTel_numbers() {
    return tel_numbers;
  }

  public void setTel_numbers(ArrayList<String> tel_numbers) {
    this.tel_numbers = tel_numbers;
  }

  public Publisher(String name){
    this.name = name;
    this.addresses = new ArrayList<String>();
    this.tel_numbers = new ArrayList<String>();
  }

  public Publisher(){
	  this.addresses = new ArrayList<String>();
	  this.tel_numbers = new ArrayList<String>();
  }

  public void add_Publisher() throws SQLException{
    String query = "Insert into publisher (name)"
            + "Value ( \'"+ this.name+"\' )";
    Controller.stmt.executeUpdate(query);

    for(String phone : this.tel_numbers){
      query = "Insert into publisherPhone (name,phone)"
              + "Value ( "
              +"\'"+ this.name+"\'"+ " , "
              + "\'"+phone +"\'"+" )";
      Controller.stmt.executeUpdate(query);
    }
    for(String address : addresses){
      query = "Insert into publisherAddress (name,address)"
              + "Value ( "
              +"\'"+ this.name+ "\'"+" , "
              +"\'"+ address +"\'"+" )";
      Controller.stmt.executeUpdate(query);
    }
  }

  public void add_tel_number(String phone){
    this.tel_numbers.add(phone);
  }

  public void add_address(String address){
    this.addresses.add(address);
  }
  
  
}
