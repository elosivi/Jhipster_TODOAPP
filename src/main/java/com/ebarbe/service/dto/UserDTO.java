package com.ebarbe.service.dto;

import com.ebarbe.domain.User;
import java.io.Serializable;

/**
 * A DTO representing a user, with only the public attributes.
 */
public class UserDTO implements Serializable {

    /*    P R O P R I E T E S    */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String login;
    // added by ebarbe
    private String firstName;
    private String lastName;
    private String email;
    private boolean activated;
    private String imageUrl;

    /*    C O N S T R U C T E U R S  */
    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.activated = user.isActivated();
        this.imageUrl = user.getImageUrl();
    }

    /*    GETTER SETTER */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return (
            "UserDTO{" +
            "id=" +
            id +
            ", login='" +
            login +
            '\'' +
            ", firstName='" +
            firstName +
            '\'' +
            ", lastName='" +
            lastName +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", activated=" +
            activated +
            ", imageUrl='" +
            imageUrl +
            '\'' +
            '}'
        );
    }
}
