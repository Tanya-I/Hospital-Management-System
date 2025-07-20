package usermenu;

/**
 * The Menu interface defines the structure for displaying a menu in the system.
 * Classes implementing this interface should provide their own implementation
 * of the displayMenu method to render specific menus for users.
 */
public interface Menu {

    /**
     * Displays the menu to the user.
     * This method should include the logic for rendering and interacting with the menu.
     */
    void displayMenu();
}
