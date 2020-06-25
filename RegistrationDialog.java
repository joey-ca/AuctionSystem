import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class RegistrationDialog extends JDialog{
   private JTextField nameField, addressField, visaField, expireField;
   private JButton okButton, cancelButton;
   private Customer customer;
   
   public RegistrationDialog(Frame owner, Customer c){
      super(owner, "Auction Registration", true);
      customer = c;
      
      JPanel itemPanel = new JPanel();
      itemPanel.setLayout(new GridLayout(4, 2, 5, 5));
      nameField = new JTextField(customer.getName(), 15);
      addressField = new JTextField(customer.getAddress(), 15);
      visaField = new JTextField(customer.getVisa(), 15);
      expireField = new JTextField(customer.getExpire(), 15);
      
      itemPanel.add(new JLabel("Name:"));
      itemPanel.add(nameField);
      itemPanel.add(new JLabel("Address:"));
      itemPanel.add(addressField);
      itemPanel.add(new JLabel("VISA #:"));
      itemPanel.add(visaField);
      itemPanel.add(new JLabel("Expiry Date:"));
      itemPanel.add(expireField);
      
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
      buttonPanel.add(okButton = new JButton("OK"));
      buttonPanel.add(cancelButton = new JButton("CANCEL"));
      
      setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
      add(itemPanel);
      add(buttonPanel);
      
      okButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            okButtonClicked();
         }
      });
      
      cancelButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            cancelButtonClicked();
         }
      });

      addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent e){
            cancelButtonClicked();
         }
      });
      
      setSize(400, 200);
      setResizable(false);
      setVisible(true);
   }
   
   private void okButtonClicked(){
      customer.setName(nameField.getText());
      customer.setAddress(addressField.getText());
      customer.setVisa(visaField.getText());
      customer.setExpire(expireField.getText());
      if(getOwner() != null)
         ((DialogClientInterface)getOwner()).dialogFinished();
      dispose();
   }
   
   private void cancelButtonClicked(){
      if(getOwner() != null)
         ((DialogClientInterface)getOwner()).dialogCancelled();
      dispose();
   }

   
}



