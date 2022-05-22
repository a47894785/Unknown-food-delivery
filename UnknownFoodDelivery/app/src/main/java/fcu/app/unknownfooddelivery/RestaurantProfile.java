package fcu.app.unknownfooddelivery;

public class RestaurantProfile {

  private String title;
  private String profile;

  public RestaurantProfile(String title, String profile) {
    this.title = title;
    this.profile = profile;
  }

  public String getTitle() {
    return title;
  }

  public String getProfile() {
    return profile;
  }

}
