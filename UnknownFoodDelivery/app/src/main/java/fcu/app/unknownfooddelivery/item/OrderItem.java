package fcu.app.unknownfooddelivery.item;

public class OrderItem {

  private String orderId;
  private String userName;
  private String shopName;
  private String mealName;
  private String mealPrice;
  private String mealNum;
  private String orderStatus;

  public OrderItem(String orderId, String userName, String shopName, String mealName, String mealPrice, String mealNum, String orderStatus) {
    this.orderId = orderId;
    this.userName = userName;
    this.shopName = shopName;
    this.mealName = mealName;
    this.mealPrice = mealPrice;
    this.mealNum = mealNum;
    this.orderStatus = orderStatus;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getUserName() {
    return userName;
  }

  public String getShopName() {
    return shopName;
  }

  public String getMealName() {
    return mealName;
  }

  public String getMealPrice() {
    return mealPrice;
  }

  public String getMealNum() {
    return mealNum;
  }

  public String getOrderStatus() {
    return orderStatus;
  }
}
