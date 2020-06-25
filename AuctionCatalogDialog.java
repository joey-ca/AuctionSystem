import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class AuctionCatalogDialog extends JDialog {
   private JList catalogList;
   private JButton closeButton;
   private JLabel pictureLabel;
   
   private static ImageIcon BLANK_IMAGE = new ImageIcon("blank_image.jpg");
   private ArrayList<AuctionItem> catalog;
   
   public AuctionCatalogDialog(Frame owner, ArrayList<AuctionItem> items){
      super(owner, "Auction Catalog", true);
      catalog = items;
      
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
      
      catalogList.addListSelectionListener(new ListSelectionListener(){
         public void valueChanged(ListSelectionEvent e){
            if(catalogList.getSelectedValue() != null)
               pictureLabel.setIcon(new ImageIcon(((AuctionItem)catalogList.getSelectedValue()).getPicture()));
            else
               pictureLabel.setIcon(BLANK_IMAGE);
         }
      });
      
      closeButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
            dispose();
         }
      });
      
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setSize(200, 450);
      setResizable(false);
   }
   
   public static void main(String args[]){
      new AuctionCatalogDialog(null, Auction.example1().getInventory()).setVisible(true);
   }
}
 