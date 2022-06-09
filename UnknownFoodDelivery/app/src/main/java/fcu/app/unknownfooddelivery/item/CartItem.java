package fcu.app.unknownfooddelivery.item;

public class CartItem {
  private String tv_shopname_mealname;
  private String tv_money_num;

  public CartItem(String tv_shopname_mealname, String tv_money_num) {
    this.tv_shopname_mealname = tv_shopname_mealname;
    this.tv_money_num = tv_money_num;
  }

  public String getTv_shopname_mealname() {
    return tv_shopname_mealname;
  }

  public String getTv_money_num() {
    return tv_money_num;
  }

}
