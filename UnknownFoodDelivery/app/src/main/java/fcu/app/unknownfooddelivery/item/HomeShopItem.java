package fcu.app.unknownfooddelivery.item;

public class HomeShopItem {

  private String shopName;
  private String shopAddress;
  private String imgName;

  public HomeShopItem(String shopName, String shopAddress, String imgName) {
    this.shopName = shopName;
    this.shopAddress = shopAddress;
    this.imgName = imgName;
  }

  public String getShopName() {
    return shopName;
  }

  public String getShopAddress() {
    return shopAddress;
  }

  public String getImgName() {
    return imgName;
  }
}
