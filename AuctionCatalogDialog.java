import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import java.util.*;

/**
* This class pertaining to the client interface displays the auction's inventory catalog returned from the server.
*
* @author Guannan Zhao
* @version %I%, %G%
* @since 1.0
*/
@SuppressWarnings("unchecked")
public class AuctionCatalogDialog extends JDialog {
   private JList catalogList;
   private JButton closeButton;
   private JLabel pictureLabel;
   
   private static ImageIcon BLANK_IMAGE = new ImageIcon("blank_image.jpg");
   
   // Store a pointer to the model for changes later
   private ArrayList<AuctionItem> catalog;
   
   // A constructor that takes the model and client as parameters
   public AuctionCatalogDialog(Frame owner, ArrayList<AuctionItem> items){
      
      // Call the super constructor that does all the work of setting up the dialog
      super(owner, "Auction Catalog", true);
      
      // Store the model into the instance variable
      catalog = items;
      
      // Set the layoutManager and add the components
      setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
      add(new JLabel("Inventory"));
      catalogList = new JList(new Vector<AuctionItem>(catalog));
      catalogList.setPrototypeCellValue("xxxxxxxxxxxxxxxxxxxx");
      JScrollPane scrollPane = new JScrollPane(catalogList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
                                               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      catalogList.setAlignmentX(FlowLayout.LEFT);
      add(scrollPane);
      add(pictureLabel = new JLabel(""));
      add(closeButton = new JButton("CLOSE"));
      
      // Add a selection listener for the inventory list
      catalogList.addListSelectionListener(new ListSelectionListener(){
         public void valueChanged(ListSelectionEvent e){
            if(catalogList.getSelectedValue() != null)
               pictureLabel.setIcon(new ImageIcon(((AuctionItem)catalogList.getSelectedValue()).getPicture()));
            else
               pictureLabel.setIcon(BLANK_IMAGE);
         }
      });
      
      // Listen for close button click
      closeButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            dispose();
         }
      });
      
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setSize(200, 450);
      
      // Prevent the window from being resized
      setResizable(false);
   }
   
   public static void main(String args[]){
      new AuctionCatalogDialog(null, Auction.example1().getInventory()).setVisible(true);
   }
}
 
