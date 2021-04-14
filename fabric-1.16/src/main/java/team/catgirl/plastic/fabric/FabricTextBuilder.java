package team.catgirl.plastic.fabric;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import team.catgirl.plastic.ui.TextAction;
import team.catgirl.plastic.ui.TextAction.OpenLinkAction;
import team.catgirl.plastic.ui.TextBuilder;
import team.catgirl.plastic.ui.TextFormatting;

public final class FabricTextBuilder extends TextBuilder {

    MutableText text;

    public FabricTextBuilder(MutableText text) {
        this.text = text;
    }

    public FabricTextBuilder() {
        this.text = new LiteralText("");
    }

    @Override
    public TextBuilder add(String text, TextFormatting textFormatting, TextAction action) {
        MutableText component = new LiteralText(text);
        if (textFormatting != null) {
            component = component.formatted(from(textFormatting));
        }
        if (action != null) {
            if (action instanceof OpenLinkAction) {
                OpenLinkAction openLinkAction = (OpenLinkAction)action;
                Style style = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, openLinkAction.url));
                component.setStyle(style);
            } else {
                throw new IllegalArgumentException("unknown action type " + action.getClass().getSimpleName());
            }
        }
        this.text.append(component);
        return this;
    }

    @Override
    public TextBuilder add(String text) {
        this.text = this.text.append(text);
        return this;
    }

    @Override
    public TextBuilder add(String text, TextFormatting textFormatting) {
        return add(text, textFormatting, null);
    }

    @Override
    public TextBuilder add(String text, TextAction action) {
        return add(text, null, action);
    }

    @Override
    public String formattedString() {
        return text.getString();
    }

    @Override
    public String toJSON() {
        return Text.Serializer.toJson(text);
    }

    Formatting from(TextFormatting textFormatting) {
        if (textFormatting == null) {
            return null;
        }
        switch (textFormatting) {
            case RED:
                return Formatting.RED;
            case AQUA:
                return Formatting.AQUA;
            case BLACK:
                return Formatting.BLACK;
            case BLUE:
                return Formatting.BLUE;
            case GOLD:
                return Formatting.GOLD;
            case GRAY:
                return Formatting.GRAY;
            case GREEN:
                return Formatting.GREEN;
            case WHITE:
                return Formatting.WHITE;
            case YELLOW:
                return Formatting.YELLOW;
            case DARK_AQUA:
                return Formatting.DARK_AQUA;
            case DARK_BLUE:
                return Formatting.DARK_BLUE;
            case DARK_GRAY:
                return Formatting.DARK_GRAY;
            case DARK_GREEN:
                return Formatting.DARK_GREEN;
            case DARK_PURPLE:
                return Formatting.DARK_PURPLE;
            case DARK_RED:
                return Formatting.DARK_RED;
            case LIGHT_PURPLE:
                return Formatting.LIGHT_PURPLE;
            case BOLD:
                return Formatting.BOLD;
            case ITALIC:
                return Formatting.ITALIC;
            case STRIKETHROUGH:
                return Formatting.STRIKETHROUGH;
            case UNDERLINE:
                return Formatting.UNDERLINE;
            case OBFUSCATED:
                return Formatting.OBFUSCATED;
            case RESET:
                return Formatting.RESET;
            default:
                throw new IllegalStateException("unknown formatting " + textFormatting);
        }
    }
}
