import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.Timer;
import java.util.*;

public class AuctionClientApp extends JFrame implements DialogClientInterface{
   private JTextField itemField, lastBidAmountField, lastBidderField, bidAmountField, statusField;
   private JButton registerButton, catalogButton, bidButton;
   private JLabel pictureLabel;
   private static ImageIcon BLANK_IMAGE = new ImageIcon("blankItem.jpg");

   private AuctionItem bidItem;
   private AuctionClient client;
   private Timer aTimer;
   
   public AuctionClientApp(){this(new AuctionClient());}
   public AuctionClientApp(AuctionClient c){
      super("UNREGISTERED Client");
      client = c;
      aTimer = new Timer(1000, new ActionListener(){
         public void actionPerformed(ActionEvent e){
            AuctionItem prevItem = client.getLatestBidItem();
            bidItem = client.sendForUpdate();
            
            if(prevItem == null){
               if(bidItem != null)
                  updateStatusField("New Item Up For Bidding");
            }
            else {
               if(bidItem == null){
                  if(prevItem.getPurchaser().getName().equals(client.getCustomer().getName()))
                     updateStatusField(prevItem.getName() + "sold to you for $" + prevItem.getBid());
                  else
                     updateStatusField("Item No Longer Up For Bidding");
               }
               else{
                  if(!prevItem.getName().equals(bidItem.getName())){
                     if(prevItem.getPurchaser().getName().equals(client.getCustomer().getName()))
                        updateStatusField(prevItem.getName() + "sold to you for $" + prevItem.getBid());
                     else
                        updateStatusField("New Item Up For Bidding");
                  }                    
               }                  
            }
            update();
         }
      });
      
      initializeComponents();
      addListeners();
      
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setSize(250, 460);
      update();
      aTimer.start();     
   }
   
   private void initializeComponents(){
      GridBagLayout layout = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();
      setLayout(layout);
      
      registerButton = new JButton("Register");
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.gridwidth = 1;
      constraints.gridheight = 1;
      constraints.weightx = 0;
      constraints.weighty = 0;
      constraints.fill = GridBagConstraints.NONE;
      constraints.anchor = GridBagConstraints.NORTHWEST;
      constraints.insets = new Insets(5, 5, 5, 5);
      layout.setConstraints(registerButton, constraints);
      add(registerButton);
      
      catalogButton = new JButton("Catalog");
      constraints.gridx = 1;
      constraints.ipadx = 10;
      constraints.ipady = 0;
      constraints.anchor = GridBagConstraints.NORTHEAST;
      layout.setConstraints(catalogButton, constraints);
      add(catalogButton);
      
      JLabel aLabel = new JLabel("Item Up For Bidding");
      constraints.gridx = 0;
      constraints.gridy = 1;
      constraints.gridwidth = 2;
      constraints.anchor = GridBagConstraints.NORTHWEST;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);
      
      pictureLabel = new JLabel();
      constraints.gridy = 2;
      constraints.gridwidth = 1;
      constraints.fill = GridBagConstraints.BOTH;
      constraints.weightx = 1;
      constraints.weighty = 1;
      layout.setConstraints(pictureLabel, constraints);
      add(pictureLabel);
      
      aLabel = new JLabel("Item Description");
      constraints.gridy = 3;
      constraints.fill = GridBagConstraints.NONE;
      constraints.weightx = 0;
      constraints.weighty = 0;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);
      
      itemField = new JTextField("", 15);
      itemField.setEditable(false);
      constraints.gridy = 4;
      constraints.gridwidth = 2;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      layout.setConstraints(itemField, constraints);
      add(itemField);
      
      aLabel = new JLabel("Last Bid Amount");
      constraints.gridy = 5;
      constraints.gridwidth = 1;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);

      lastBidAmountField = new JTextField("", 15);
      lastBidAmountField.setEditable(false);
      constraints.gridy = 6;
      constraints.gridwidth = 2;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      layout.setConstraints(lastBidAmountField, constraints);
      add(lastBidAmountField);
      
      aLabel = new JLabel("Last Bidder");
      constraints.gridy = 7;
      constraints.gridwidth = 1;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);

      lastBidderField = new JTextField("", 15);
      lastBidderField.setEditable(false);
      constraints.gridy = 8;
      constraints.gridwidth = 2;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      layout.setConstraints(lastBidderField, constraints);
      add(lastBidderField);
      
      bidAmountField = new JTextField();
      constraints.gridy = 9;
      constraints.gridwidth = 1;
      layout.setConstraints(bidAmountField, constraints);
      add(bidAmountField);
      
      statusField = new JTextField();
      statusField.setEditable(false);
      statusField.setBackground(new Color(255, 255, 255));
      statusField.setForeground(new Color(160, 0, 0));
      constraints.gridy = 10;
      constraints.gridwidth = 2;
      constraints.weightx = 10;
      layout.setConstraints(statusField, constraints);
      add(statusField);
      
      bidButton = new JButton("Make Bid");
      constraints.gridx = 1;
      constraints.gridy = 9;
      constraints.gridwidth = 1;
      constraints.weightx = 0;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(bidButton, constraints);
      add(bidButton);
      
   }
   
   private void addListeners(){
      registerButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            Customer customer = client.getCustomer();
            new RegistrationDialog(AuctionClientApp.this, customer);
         }
      });
      
      catalogButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            ArrayList<AuctionItem> items = client.sendForCatalog();
            new AuctionCatalogDialog(AuctionClientApp.this, items).setVisible(true);
         }
      });
      
      bidButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            client.sendBid(client.getCustomer().getName(), Float.parseFloat(bidAmountField.getText()));
            updateStatusField(client.getServerReply().toString());
            update();
         }
      }); 
      
      bidAmountField.getDocument().addDocumentListener(new DocumentListener(){
         public void changedUpdate(DocumentEvent e){updateBidButton();}
         public void insertUpdate(DocumentEvent e){updateBidButton();}
         public void removeUpdate(DocumentEvent e){updateBidButton();}
      });     
   }
   
   public void dialogFinished(){
      if(client.registerForClient())
         setTitle("Client: " + client.getCustomer().getName()); 
      updateStatusField(client.getServerReply().toString()); 
   }
   
   public void dialogCancelled(){}
   
   private void update(){
      updatePictureLabel();
      updateItemField();
      updateLastBidAmountField();
      updateLastBidderField();
      updateBidButton();
      updateBidAmountField();
   }
   
   private void updatePictureLabel(){
      if(bidItem != null)
         pictureLabel.setIcon(new ImageIcon(bidItem.getPicture()));
      else
         pictureLabel.setIcon(BLANK_IMAGE);        
   }
   
   private void updateItemField(){
      if(bidItem != null)
         itemField.setText(bidItem.getName());
      else
         itemField.setText("");
   }
   
   private void updateLastBidAmountField(){
      if(bidItem != null)
         lastBidAmountField.setText(String.valueOf(bidItem.getBid()));
      else
         lastBidAmountField.setText("");
   }
   
   private void updateLastBidderField(){
      if((bidItem != null)&&(bidItem.getPurchaser() != null))
         lastBidderField.setText(bidItem.getPurchaser().getName());
      else
         lastBidderField.setText("");
   }
   
   private void updateBidAmountField(){
      if(bidItem == null) bidAmountField.setText("");
   }
   
   public void updateStatusField(String s){
      statusField.setText(s);
   }
   
   private void updateBidButton(){
      try{
         bidButton.setEnabled((Float.parseFloat(bidAmountField.getText()) != 0)&&(bidItem != null));
      }
      catch(NumberFormatException e){bidButton.setEnabled(false);}
   }
   
   public static void main(String args[]){
      new AuctionClientApp(new AuctionClient(new Customer("Mark", "67 Elm St", "7876 3232 8798 3434", "09/03"))).setVisible(true);
   }
}

