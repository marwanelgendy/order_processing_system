package View;

import javax.swing.*;

import controller.Controller;
import model.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Login extends JFrame implements WindowListener{
	
	private JFrame frame = this;
	
	public Login() {
	  Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	  this.setSize(new Dimension(500,250));
	  this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		getContentPane().setLayout(null);
		this.setTitle("Login");
		w = new JLabel();
		w.setText("Book Store Order Processing System");

		w.setBounds(170, 15, 246, 19);
		getContentPane().add(w);


		 welcome = new JLabel();
		welcome.setText("Username :");

		welcome.setBounds(100, 35, 246, 19);
		getContentPane().add(welcome);
		nameField = new JTextField();
		nameField.setBounds(100, 55, 246, 19);
		getContentPane().add(nameField);
		nameField.setBackground(Color.gray);
		nameField.setForeground(Color.black);
		nameField.setColumns(10);

		 ee = new JLabel("Password :");
		ee.setBounds(100, 75, 246, 19);
		getContentPane().add(ee);
		passwordField = new JPasswordField();
		passwordField.setBounds(100, 95, 246, 19);
		getContentPane().add(passwordField);
		passwordField.setBackground(Color.gray);
		passwordField.setForeground(Color.black);
		passwordField.setColumns(10);
		
		JButton loginButton = new JButton("Login");
		loginButton.setBounds(75, 142, 117, 25);
		getContentPane().add(loginButton);
		loginButton.setBackground(Color.black);
		loginButton.setForeground(Color.white);
		loginButton.addActionListener(new loginAction());
		
		signupButton = new JButton("Sign up");
		signupButton.setBounds(204, 142, 117, 25);
		signupButton.setBackground(Color.black);
		signupButton.setForeground(Color.white);
		getContentPane().add(signupButton);
		signupButton.addActionListener(new signupAction());
		
		controller =  Controller.getInstance();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField nameField;
	private JTextField passwordField;
	private JButton signupButton;
	private JLabel welcome;
	private JLabel ee;
	private JLabel w;
	private Controller controller;
	
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	
	
	private class loginAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			User user = controller.login(nameField.getText().toLowerCase(), passwordField.getText());
			if(user != null){
				MainView mainView = new MainView(user);
				mainView.setVisible(true);
				frame.setVisible(false);
			}
		}
		
	}
	
	private class signupAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			UserView userView = new UserView(new User(), true);
			userView.setVisible(true);
			frame.setVisible(false);
		}
		
	}
	
	public static void showError(String message){
		JOptionPane.showMessageDialog(null, message , "Error", JOptionPane.ERROR_MESSAGE);
	}
}
