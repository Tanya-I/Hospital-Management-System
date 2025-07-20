package usermain;

/**
 * Represents a user in the Hospital Management System.
 * This class includes user properties such as ID, password, role, and name,
 * along with methods to access and modify these properties.
 */
public class User {
    private String id;
    private String password;
    private String role;
    private String name;

    /**
     * Default constructor for the User class.
     * Initializes the user with a default password "password".
     */
    public User() {
        password = "password";
    }

    /**
     * Constructor to initialize a User object with specific attributes.
     *
     * @param id       The unique ID of the user.
     * @param password The password of the user.
     * @param role     The role of the user (e.g., Patient, Doctor).
     * @param name     The name of the user.
     */
    public User(String id, String password, String role, String name) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    /**
     * Constructor to initialize a User object with an ID and name.
     *
     * @param id   The unique ID of the user.
     * @param name The name of the user.
     */
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor to initialize a User object with only an ID.
     *
     * @param id The unique ID of the user.
     */
    public User(String id) {
        this.id = id;
    }

    /**
     * Sets the password for the user.
     *
     * @param pass The new password to set.
     */
    public void setPassword(String pass) {
        password = pass;
    }

    /**
     * Retrieves the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the unique ID of the user.
     *
     * @return The ID of the user.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the role of the user.
     *
     * @return The role of the user (e.g., Patient, Doctor).
     */
    public String getRole() {
        return role;
    }

    /**
     * Checks whether a given password matches the user's password.
     *
     * @param pass The password to check.
     * @return {@code true} if the provided password matches; {@code false} otherwise.
     */
    public boolean checkPassword(String pass) {
        return this.password.equals(pass);
    }

    /**
     * Retrieves the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }
}

