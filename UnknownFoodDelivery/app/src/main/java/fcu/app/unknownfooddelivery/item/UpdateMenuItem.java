package fcu.app.unknownfooddelivery.item;

public class UpdateMenuItem {

  private String mealName;
  private String mealImg;
  private String mealPrice;
  private String mealId;

  public UpdateMenuItem(String mealName, String mealImg, String mealPrice, String mealId) {
    this.mealName = mealName;
    this.mealImg = mealImg;
    this.mealPrice = mealPrice;
    this.mealId = mealId;
  }

  public String getMealName() {
    return mealName;
  }

  public String getMealImg() {
    return mealImg;
  }

  public String getMealPrice() {
    return mealPrice;
  }

  public String getMealId() {
    return mealId;
  }
}
