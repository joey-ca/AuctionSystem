import java.util.*;

/**
* The auction model class defines the auctioning business logic. 
* It keeps track of inventory of items to be auctioned, the current item up for auction as well as the customers and their purchases.
*
* @author Guannan Zhao
* @version %I%, %G%
* @since 1.0
*/
public class Auction{
   private AuctionItem bidItem;
   private HashMap<Customer, ArrayList<AuctionItem>> purchases;
   private ArrayList<AuctionItem> inventory;
   
   public Auction(ArrayList<AuctionItem> initialInventory){
      inventory = initialInventory;
      purchases = new HashMap<Customer, ArrayList<AuctionItem>>();
      bidItem = null;
   }
   
   public Auction(){this(new ArrayList<AuctionItem>());}
   
   public AuctionItem getBidItem(){return bidItem;}
   public HashMap<Customer, ArrayList<AuctionItem>> getPurchases(){return purchases;}
   public ArrayList<AuctionItem> getInventory(){return inventory;}
   
   // Palce the given item up for bidding, customers can then bid on it
   public void placeUpForBid(AuctionItem item){
      if(item.isSold()) return;
      bidItem = item;
   }
   
   // Register the given Customer with the Auction
   public void register(Customer c){purchases.put(c, new ArrayList<AuctionItem>());}
   
   // Register the Customer with the given information to Auction
   public void register(String name, String address, String visa, String expire){
      register(new Customer(name, address, visa, expire));
   }
   
   // Find the Customer with the given name
   public Customer customerWithName(String s){
      for(Customer c: purchases.keySet()){
         if(c.getName().equals(s))
            return c;
      }
      return null;
   }
   
   // Return whether or not there is currently an item up for bidding
   public boolean hasBidItem(){
      return bidItem != null;
   }
   
   // Return the latest bid
   public float latestBid(){
      if(bidItem != null)
         return bidItem.getBid(); 
      else
         return 0;
   }
   
   // Return the latest bidder
   public Customer latestBidder(){
      if(bidItem != null)
         return bidItem.getPurchaser();
      else
         return null;
   }
   
   // Return the name of the latest bidder
   public String latestBidderName(){
      if((bidItem == null)||(bidItem.getPurchaser() == null))
         return "";
      else
         return bidItem.getPurchaser().getName();      
   }
   
   // Add the given item to the inventory
   public void add(AuctionItem item){inventory.add(item);}
   
   // Accept an incoming bid for the item up for bid.
   // If it is a valid bid, remember who made the bid and increase the latest bid amount.
   public boolean makeBid(String customerName, float bidPrice){
      // If nothing is up for bidding, don't accept the bid
      if(bidItem == null)
         return false;
      
      // First make sure the bid is actually valid
      else if(bidPrice <= latestBid())
         return false;
      
      // Now make sure the customer is valid
      Customer c = customerWithName(customerName);
      if(c == null)
         return false;
      else
         bidItem.setBid(bidPrice);
         bidItem.setPurchaser(c);
         return true;
   }
   
   // Once the bidding has stopped, the item is considered to be sold to the last bidder, if there was one.
   public void stopBidding(){
      if(latestBidder() != null){
         purchases.get(latestBidder()).add(bidItem);
         inventory.remove(bidItem);
         bidItem.setSold();
      }
      bidItem = null;
   }
   
   // Returns an initialized Auction object
   public static Auction example1(){
      Auction a = new Auction();
      AuctionItem first = new AuctionItem("Antique Table", 150.0f, "table.jpg");
      a.add(first);
      a.add(new AuctionItem("JVC VCR", 65.0f, "vcr.jpg"));
      a.add(new AuctionItem("Antique Cabinet", 400.0f, "cabinet.jpg"));
      a.add(new AuctionItem("5-piece Drumset", 190.0f, "drumset.jpg"));
      a.add(new AuctionItem("Violin & Case", 100.0f, "violin.jpg"));
      a.add(new AuctionItem("13\" TV VCR Combo", 100.0f, "tvvcr.jpg"));
      a.add(new AuctionItem("486Dx2-66 Laptop", 125.0f, "486laptop.jpg"));
      a.add(new AuctionItem("Rocking Chair", 80.0f, "rockingchair.jpg"));
      a.placeUpForBid(first);
      return a;
   }
   
   
}
