package fcu.app.unknownfooddelivery;

public class UserProfile {

  private String title;
  private String profile;

  public UserProfile(String title, String profile) {
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
