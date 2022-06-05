package fcu.app.unknownfooddelivery.item;

public class MealItem {

  private String mealName;
  private String mealPrice;
  private String mealImg;
  private String shopName;

  public MealItem(String mealName, String mealPrice, String mealImg, String shopName) {
    this.mealName = mealName;
    this.mealPrice = mealPrice;
    this.mealImg = mealImg;
    this.shopName = shopName;
  }

  public String getMealName() {
    return mealName;
  }

  public String getMealPrice() {
    return mealPrice;
  }

  public String getMealImg() {
    return mealImg;
  }

  public String getShopName() {
    return shopName;
  }
}
