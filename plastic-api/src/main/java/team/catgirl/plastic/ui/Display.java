package team.catgirl.plastic.ui;

import team.catgirl.plastic.Plastic;

public interface Display {
    /**
     * Display a status message on screen
     * @param message to display
     */
    void displayStatusMessage(String message);

    /**
     * Display a status message on screen
     * @param message to display
     */
    void displayStatusMessage(TextBuilder message);

    /**
     * Send a message to the chat console
     * @param message to send
     */
    void displayMessage(TextBuilder message);

    /**
     * Send a info message to the chat console
     * @param message to send
     */
    default void displayInfoMessage(String message) {
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextFormatting.GRAY));
    }

    /**
     * Send a warning message to the chat console
     * @param message to send
     */
    default void displayWarningMessage(String message) {
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextFormatting.YELLOW));
    }

    /**
     * Send a error message to the chat console
     * @param message to send
     */
    default void displayErrorMessage(String message) {
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextFormatting.RED));
    }

    /**
     * Send a success message to the chat console
     * @param message to send
     */
    default void displayMessage(String message) {
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextFormatting.GRAY));
    }

    /**
     * @return new text builder
     */
    TextBuilder newTextBuilder();

    /**
     * @param json representing the text
     * @return new text builder from JSON
     */
    TextBuilder textBuilderFromJSON(String json);

    /**
     * Create a text builder from formatted string
     * @param text
     * @return new text builder
     */
    TextBuilder textBuilderFromFormattedString(String text);
}
