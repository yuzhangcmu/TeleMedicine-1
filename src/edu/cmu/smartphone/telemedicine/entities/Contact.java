package edu.cmu.smartphone.telemedicine.entities;

public class Contact {
    private String type;
    private String email;
    private String phone;
    
    private String nation; // don't store nation id local.
    private String province;
    private String city;
    
    private String name;
    private String userID;
    
    private String intro;
    private String headPortrait; // store the picture of the user.
    
    private int age;
    private String passWord;
    
    // this constract a contact which only has name and loginID to show it 
    // on the screen. detail profile can be fatched from the database.
    public Contact (String name, String loginID) {
        this.name = name;
        this.userID = loginID;
    }
    
    public String getUserID() {
        return userID;
    }
    
    public String getType() {
        return type;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getNation() {
        return nation;
    }
    
    public String getProvince() {
        return province;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getName() {
        return name;
    }
    
    public String getIntro() {
        return intro;
    }
    
    public void setIntro(String intro) {
        this.intro = intro;
    }
    
    public String getHeadPortrait() {
        return headPortrait;
    }
    
    public int getAge() {
        return age;
    }
    
    public String getPassword() {
        return passWord;
    }
    
}
