import java.util.*;

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

   public void placeUpForBid(AuctionItem item){
      if(item.isSold()) return;
      bidItem = item;
   }
   
   public void register(Customer c){purchases.put(c, new ArrayList<AuctionItem>());}
   
   public void register(String name, String address, String visa, String expire){
      register(new Customer(name, address, visa, expire));
   }
   
   public Customer customerWithName(String s){
      for(Customer c: purchases.keySet()){
         if(c.getName().equals(s))
            return c;
      }
      return null;
   }
   
   public boolean hasBidItem(){
      return bidItem != null;
   }
   
   public float latestBid(){
      if(bidItem != null)
         return bidItem.getBid(); 
      else
         return 0;
   }
   
   public Customer latestBidder(){
      if(bidItem != null)
         return bidItem.getPurchaser();
      else
         return null;
   }
   
   public String latestBidderName(){
      if((bidItem == null)||(bidItem.getPurchaser() == null))
         return "";
      else
         return bidItem.getPurchaser().getName();      
   }
   
   public void add(AuctionItem item){inventory.add(item);}
   
   public boolean makeBid(String customerName, float bidPrice){
      if(bidItem == null)
         return false;
      else if(bidPrice <= latestBid())
         return false;
      Customer c = customerWithName(customerName);
      if(c == null)
         return false;
      else
         bidItem.setBid(bidPrice);
         bidItem.setPurchaser(c);
         return true;
   }
   
   public void stopBidding(){
      if(latestBidder() != null){
         purchases.get(latestBidder()).add(bidItem);
         inventory.remove(bidItem);
         bidItem.setSold();
      }
      bidItem = null;
   }
   
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