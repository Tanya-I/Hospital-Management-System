package usermenu;

import java.util.Scanner;

/**
 * The AbstractMenu class provides a base implementation for menus in the system.
 * It implements the {@link Menu} interface and includes common functionality
 * for displaying a logout option. Classes extending this abstract class
 * must implement the {@code displayMenu} method to define specific menu content.
 */
public abstract class AbstractMenu implements Menu {

    /**
     * A shared Scanner instance for user input across all menus.
     */
    protected static final Scanner sc = new Scanner(System.in);

    /**
     * Displays the logout option with a specified option number.
     *
     * @param x The number to display next to the logout option
     */
    protected void displayLogoutOption(int x) {
        System.out.println("(" + x + ") Logout");
    }

    /**
     * Abstract method to display specific menu content.
     * Subclasses must provide an implementation for this method.
     */
    public abstract void displayMenu();
}
