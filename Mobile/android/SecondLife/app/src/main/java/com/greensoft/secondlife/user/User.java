package com.greensoft.secondlife.user;

import java.io.Serializable;

/**
 * Created by zebul on 6/24/17.
 */

public class User implements Serializable{

    public String Id;
    public String FirstName;
    public String LastName;
    public String Email;
    public String Password;

    public User(String id, String firstName, String lastName, String email, String password) {
        Id = id;
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

        if (Id != null ? !Id.equals(user.Id) : user.Id != null) return false;
        if (FirstName != null ? !FirstName.equals(user.FirstName) : user.FirstName != null)
            return false;
        if (LastName != null ? !LastName.equals(user.LastName) : user.LastName != null)
            return false;
        if (Email != null ? !Email.equals(user.Email) : user.Email != null) return false;
        return Password != null ? Password.equals(user.Password) : user.Password == null;

    }

    @Override
    public int hashCode() {
        int result = Id != null ? Id.hashCode() : 0;
        result = 31 * result + (FirstName != null ? FirstName.hashCode() : 0);
        result = 31 * result + (LastName != null ? LastName.hashCode() : 0);
        result = 31 * result + (Email != null ? Email.hashCode() : 0);
        result = 31 * result + (Password != null ? Password.hashCode() : 0);
        return result;
    }
}
