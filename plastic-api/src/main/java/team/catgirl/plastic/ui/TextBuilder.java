package team.catgirl.plastic.ui;

public abstract class TextBuilder {
    /**
     * Add formatted text to builder
     * @param text content
     * @param textFormatting to format text
     * @param action for text
     * @return text
     */
    public abstract TextBuilder add(String text, TextFormatting textFormatting, TextAction action);

    /**
     * Add formatted text to builder
     * @param text content
     * @return text
     */
    public abstract TextBuilder add(String text);

    /**
     * Add formatted text to builder
     * @param text content
     * @param textFormatting formatting
     * @return text
     */
    public abstract TextBuilder add(String text, TextFormatting textFormatting);

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

    public abstract String toJSON();
}
