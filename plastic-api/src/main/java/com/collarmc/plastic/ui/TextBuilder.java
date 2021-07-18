package com.collarmc.plastic.ui;

public abstract class TextBuilder {
    /**
     * Add formatted text to builder
     * @param text content
     * @param color to style text
     * @param style to style text
     * @param action for text
     * @return text
     */
    public abstract TextBuilder add(String text, TextColor color, TextStyle style, TextAction action);

    /**
     * Add formatted text to builder
     * @param text content
     * @return text
     */
    public abstract TextBuilder add(String text);

    /**
     * Add formatted text to builder
     * @param text content
     * @param textStyle formatting
     * @return text
     */
    public abstract TextBuilder add(String text, TextStyle textStyle);

    /**
     * Add formatted text to builder
     * @param text content
     * @param color formatting
     * @return text
     */
    public abstract TextBuilder add(String text, TextColor color);

    /**
     * Add formatted text to builder
     * @param text content
     * @param color formatting
     * @param textStyle formatting
     * @return text
     */
    public abstract TextBuilder add(String text, TextColor color, TextStyle textStyle);

    /**
     * Add formatted text to builder
     * @param text content
     * @param action for text
     * @return text
     */
    public abstract TextBuilder add(String text, TextAction action);

    /**
     * @return formatted text
     */
    public abstract String formattedString();

    /**
     * @return serialize to json
     */
    public abstract String toJSON();
}
