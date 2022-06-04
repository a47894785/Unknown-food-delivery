package fcu.app.unknownfooddelivery.item;

public class MealItem {

  private String mealName;
  private String mealPrice;
  private String mealImg;

  public MealItem(String mealName, String mealPrice, String mealImg) {
    this.mealName = mealName;
    this.mealPrice = mealPrice;
    this.mealImg = mealImg;
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
}
