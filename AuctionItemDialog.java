import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class AuctionItemDialog extends JDialog{
   private JTextField nameField, bidField, pictureField;
   private JButton okButton, cancelButton;
   private AuctionItem item;
   
   public AuctionItemDialog(Frame owner, AuctionItem auctionItem){
      super(owner, "New Auction Item", true);
      item = auctionItem;
      
      JPanel fieldPanel = new JPanel();
      fieldPanel.setLayout(new GridLayout(3, 2, 5, 5));
      nameField = new JTextField(item.getName(), 15);
      bidField = new JTextField(item.getBid() + "", 15);
      bidField.setHorizontalAlignment(JTextField.RIGHT);
      pictureField = new JTextField(item.getPicture(), 15);
           
      JLabel aLabel;
      fieldPanel.add(aLabel = new JLabel("Item Name:"));
      fieldPanel.add(nameField);
      fieldPanel.add(aLabel = new JLabel("Starting Bid($):"));
      fieldPanel.add(bidField);
      fieldPanel.add(aLabel = new JLabel("Picture file(gif/jpg):"));
      fieldPanel.add(pictureField);
      
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
      buttonPanel.add(okButton = new JButton("OK"));
      buttonPanel.add(cancelButton = new JButton("CANCEL"));
      
      setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
      add(fieldPanel);
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
      
      setResizable(false);
      setSize(400, 150);
      setVisible(true);
   }
   
   public AuctionItem getItem(){return item;}
   
   private void okButtonClicked(){
      item.setName(nameField.getText());
      item.setBid(Float.parseFloat(bidField.getText()));
      item.setPicture(pictureField.getText());
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












