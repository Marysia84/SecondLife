package com.greensoft.secondlife.user;

/**
 * Created by zebul on 6/24/17.
 */

public class User {

    public String FirstName;
    public String LastName;
    public String Email;
    public String Password;

    public User(String firstName, String lastName, String email, String password) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        Password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!FirstName.equals(user.FirstName)) return false;
        if (!LastName.equals(user.LastName)) return false;
        if (!Email.equals(user.Email)) return false;
        return Password.equals(user.Password);

    }

    @Override
    public int hashCode() {
        int result = FirstName.hashCode();
        result = 31 * result + LastName.hashCode();
        result = 31 * result + Email.hashCode();
        result = 31 * result + Password.hashCode();
        return result;
    }
}
