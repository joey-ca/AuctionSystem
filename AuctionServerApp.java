import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

public class AuctionServerApp extends JFrame implements DialogClientInterface{
   private JTextField descriptionField, bidField, bidderField, statusField;
   private JButton addButton, removeButton, stopButton, upForBidButton;
   private JLabel pictureLabel;
   private JList inventoryList;
   
   private ListSelectionListener listListener;
   private int selectedIndex;
   
   private AuctionServer server; 
   private AuctionItemDialog dialog;
   private AuctionItem newAuctionItem; 
   
   private static String BLANK_IMAGE = "blankItem.jpg";
   
   public AuctionServerApp(){this(new AuctionServer(new Auction()));}
   public AuctionServerApp(AuctionServer s){
      super("The Auction Server");
      server = s;
      server.registerForUpdates(this);
      if(server.getAuction().getInventory().size() > 0)
         selectedIndex = 0;
      else
         selectedIndex = -1;
      
      initializeComponents();
      
      addButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            handleAddEvent();
         }
      });
      
      removeButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            handleRemoveEvent();
         }
      });
      
      stopButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            handleStopEvent();
         }
      });
      
      upForBidButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            handleUpForBidEvent();
         }
      });
      
      inventoryList.addListSelectionListener(listListener = new ListSelectionListener(){
         public void valueChanged(ListSelectionEvent e){
            handleListEvent();
         }
      });
      
      addWindowListener(new WindowAdapter(){
         public void windowOpened(WindowEvent e){
            if(server.goOnline())
               updateStatusField("Auction Server Online");
            else
               updateStatusField("Error: Problem Getting Auction Server Online");
         }
      });
      
      addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent e){
            if(!server.goOffline())
               updateStatusField("Auction Server Offline");
            else
               updateStatusField("Error: Problem Going Offline");
         }
      });
      
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setSize(360, 490);
      update();     
   }
   
   public void update(){
      inventoryList.removeListSelectionListener(listListener);
      updateList();
      updatePictureLabel();
      updateDescriptionField();
      updateBidField();
      updateBidderField();
      //updateStatusField();
      updateRemoveButton();
      updateStopButton();
      updateUpForBidButton();
      inventoryList.addListSelectionListener(listListener);   
   }
   
   private void handleAddEvent(){
      newAuctionItem = new AuctionItem();
      dialog = new AuctionItemDialog(this, newAuctionItem);
      dialog.setVisible(true);
   }
   
   public void dialogFinished(){
      server.getAuction().add(newAuctionItem);
      selectedIndex = server.getAuction().getInventory().size() - 1;
      update();
   }
   
   public void dialogCancelled(){}

   private void handleRemoveEvent(){
      if(selectedIndex != -1){
         server.getAuction().getInventory().remove(selectedIndex);
         selectedIndex--;
         update();
      }
   }
   
   private void handleStopEvent(){
      server.getAuction().stopBidding();
      update();
   }
   
   private void handleUpForBidEvent(){
      if(selectedIndex != -1){
         server.getAuction().placeUpForBid((AuctionItem)server.getAuction().getInventory().get(selectedIndex));
         update();
      }
   }
   
   private void handleListEvent(){
      selectedIndex = inventoryList.getSelectedIndex();
      update();
   }
   
   private void updateList(){
      inventoryList.setListData(new Vector(server.getAuction().getInventory()));
      inventoryList.setSelectedIndex(selectedIndex);
   }
   
   private void updatePictureLabel(){
      if(server.getAuction().hasBidItem())
         pictureLabel.setIcon(new ImageIcon(server.getAuction().getBidItem().getPicture()));
      else
         pictureLabel.setIcon(new ImageIcon(BLANK_IMAGE));
   }
   
   private void updateDescriptionField(){
      if(server.getAuction().hasBidItem())
         descriptionField.setText(server.getAuction().getBidItem().getName());
      else
         descriptionField.setText("");
   }
   
   private void updateBidField(){
      if(server.getAuction().hasBidItem()){
         DecimalFormat formatter = new DecimalFormat("$0.00");
         bidField.setText(formatter.format(server.getAuction().latestBid()));
      }
      else
         bidField.setText("");
   }
   
   private void updateBidderField(){
      if(server.getAuction().hasBidItem())
         bidderField.setText(server.getAuction().latestBidderName());
      else
         bidderField.setText("");
   }
   
   private void updateStatusField(String s){
      statusField.setText(s);
   }
   
   private void updateRemoveButton(){
      removeButton.setEnabled(selectedIndex != -1);
   }
   
   private void updateStopButton(){
      stopButton.setEnabled(server.getAuction().hasBidItem());
   }
   
   private void updateUpForBidButton(){
      upForBidButton.setEnabled(!server.getAuction().hasBidItem());
   }
   
   private void initializeComponents(){
      GridBagLayout layout = new GridBagLayout();
      GridBagConstraints constraints = new GridBagConstraints();
      setLayout(layout);
      
      JLabel aLabel = new JLabel("Inventory"); 
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.gridwidth = 2;
      constraints.gridheight = 1;
      constraints.weightx = 0;
      constraints.weighty = 0;
      constraints.insets = new Insets(5, 5, 5, 5);
      constraints.anchor = GridBagConstraints.NORTHWEST;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);
      
      aLabel = new JLabel("Item Up For Bidding"); 
      constraints.gridx = 2;
      constraints.gridwidth = 1;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);
      
      pictureLabel = new JLabel();//don't need to initialize here 
      constraints.gridy = 1;
      constraints.weightx = 1;
      constraints.weighty = 1;
      constraints.fill = GridBagConstraints.BOTH;
      layout.setConstraints(pictureLabel, constraints);
      add(pictureLabel);
      
      aLabel = new JLabel("Item Description"); 
      constraints.gridy = 2;
      constraints.weightx = 0;
      constraints.weighty = 0;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);
      
      descriptionField = new JTextField();//don't need to initialize here 
      descriptionField.setEditable(false);
      constraints.gridy = 3;
      constraints.weightx = 1;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      layout.setConstraints(descriptionField, constraints);
      add(descriptionField);
      
      aLabel = new JLabel("Last Bid Amount"); 
      constraints.gridy = 4;
      constraints.weightx = 0;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);
      
      bidField = new JTextField();//don't need to initialize here
      bidField.setEditable(false);
      constraints.gridy = 5;
      constraints.weightx = 1;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      layout.setConstraints(bidField, constraints);
      add(bidField);
      
      aLabel = new JLabel("Last Bidder"); 
      constraints.gridy = 6;
      constraints.weightx = 0;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(aLabel, constraints);
      add(aLabel);
      
      bidderField = new JTextField(); //don't need to initialize here
      bidderField.setEditable(false);
      constraints.gridy = 7;
      constraints.weightx = 1;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      layout.setConstraints(bidderField, constraints);
      add(bidderField);
      
      stopButton = new JButton("Stop Bidding");
      constraints.gridy = 8;
      constraints.weightx = 0;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(stopButton, constraints);
      add(stopButton);

      inventoryList = new JList();//don't need to initialize here
      JScrollPane scrollPane = new JScrollPane(inventoryList, 
                                               ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
                                               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      constraints.gridx = 0;
      constraints.gridy = 1;
      constraints.gridwidth = 2;
      constraints.gridheight = 7;
      constraints.weightx = 0;
      constraints.weighty = 1;
      constraints.anchor = GridBagConstraints.CENTER;
      constraints.fill = GridBagConstraints.BOTH;
      layout.setConstraints(scrollPane, constraints);
      add(scrollPane);

      addButton = new JButton("Add");
      addButton.setMnemonic('A');
      constraints.gridy = 8;
      constraints.gridwidth = 1;
      constraints.gridheight = 1;
      constraints.weighty = 0;
      constraints.anchor = GridBagConstraints.NORTHWEST;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(addButton, constraints);
      add(addButton);

      upForBidButton = new JButton("Place up for Bidding");
      constraints.gridy = 9;
      constraints.gridwidth = 2;
      layout.setConstraints(upForBidButton, constraints);
      add(upForBidButton);

      statusField = new JTextField();
      statusField.setEditable(false);
      statusField.setBackground(new Color(255,255,255));
      statusField.setForeground(new Color(160,0,0));
      constraints.gridy = 10;
      constraints.gridwidth = 3;
      constraints.weightx = 1;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      layout.setConstraints(statusField, constraints);
      add(statusField);

      removeButton = new JButton("Remove");
      removeButton.setMnemonic('R');
      constraints.gridx = 1;
      constraints.gridy = 8;
      constraints.gridwidth = 1;
      constraints.weightx = 0;
      constraints.ipadx = 10;
      constraints.ipady = 0;
      constraints.fill = GridBagConstraints.NONE;
      layout.setConstraints(removeButton, constraints);
      add(removeButton);    
   }
   
   public static void main(String args[]){
      new AuctionServerApp(new AuctionServer(Auction.example1())).setVisible(true);
   }
   
}