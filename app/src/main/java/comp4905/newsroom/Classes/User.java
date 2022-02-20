package comp4905.newsroom.Classes;

import java.util.ArrayList;

public class User {

    //member variables
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private ArrayList<String> favorites;

    //constructors
    public User(){}

    public User(String firstName, String lastName, String userName, String email, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String userName, String email, String password, ArrayList<String> favorites)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.favorites = favorites;
    }

    //getters
    public String getFirstName() {return firstName; }
    public String getLastName() {return lastName; }
    public String getUserName() {return userName; }
    public String getEmail() {return email; }
    public String getPassword() {return password; }
    public ArrayList<String> getFavorites() {return favorites; }

    //setters
    public void setFirstName(String firstName) {this.firstName = firstName; }
    public void setLastName(String lastName) {this.lastName = lastName; }
    public void setUserName(String userName) {this.userName = userName; }
    public void setEmail(String email) {this.email = email; }
    public void setPassword(String password) {this.password = password; }
    public void setFavorites(ArrayList<String> favorites) {this.favorites = favorites; }

    @Override
    public String toString() {
        return "User{" +
                "FirstName='" + firstName + '\'' +
                ", LastName='" + lastName + '\'' +
                ", UserName='" + userName + '\'' +
                ", Email='" + email + '\'' +
                ", Password='" + password + '\'' +
                ", FavoriteTopics=" + favorites +
                '}';
    }
}
