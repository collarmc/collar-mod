package com.collarmc.plastic.ui;

import com.collarmc.plastic.Plastic;

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
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextColor.GRAY));
    }

    /**
     * Send a success message to the chat console
     * @param message to send
     */
    default void displaySuccessMessage(String message) {
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextColor.GREEN));
    }

    /**
     * Send a warning message to the chat console
     * @param message to send
     */
    default void displayWarningMessage(String message) {
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextColor.YELLOW));
    }

    /**
     * Send a error message to the chat console
     * @param message to send
     */
    default void displayErrorMessage(String message) {
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextColor.RED));
    }

    /**
     * Send a success message to the chat console
     * @param message to send
     */
    default void displayMessage(String message) {
        displayMessage(Plastic.getPlastic().display.newTextBuilder().add(message, TextColor.GRAY));
    }

    /**
     * @return new text builder
     */
    TextBuilder newTextBuilder();
}
