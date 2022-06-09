package fcu.app.unknownfooddelivery.item;

public class HistoryItem {
  private String tv_shopname;
  private String tv_mealname;
  private String tv_mealprice;
  private String tv_mealnum;

  public HistoryItem(String tv_shopname, String tv_mealname,String tv_mealprice,String tv_mealnum) {
    this.tv_shopname = tv_shopname;
    this.tv_mealname = tv_mealname;
    this.tv_mealprice = tv_mealprice;
    this.tv_mealnum = tv_mealnum;
  }

  public String getTv_shopname() {
    return tv_shopname;
  }

  public String getTv_mealname() {
    return tv_mealname;
  }

  public String getTv_mealprice() {
    return tv_mealprice;
  }

  public String getTv_mealnum() {
    return tv_mealnum;
  }
}
