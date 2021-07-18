<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/plastic/fabric/FabricTextBuilder.java
package com.collarmc.plastic.fabric;
=======
package com.collarmc.collar.plastic;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/plastic/GlueTextBuilder.java

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import com.collarmc.plastic.ui.TextAction;
import com.collarmc.plastic.ui.TextAction.OpenLinkAction;
import com.collarmc.plastic.ui.TextBuilder;
import com.collarmc.plastic.ui.TextColor;
import com.collarmc.plastic.ui.TextStyle;

public final class GlueTextBuilder extends TextBuilder {

    MutableText text;

    public GlueTextBuilder(MutableText text) {
        this.text = text;
    }

    public GlueTextBuilder() {
        this.text = new LiteralText("");
    }

    @Override
    public TextBuilder add(String text, TextColor color, TextStyle style, TextAction action) {
        MutableText component = new LiteralText(text);
        if (color != null) {
            Formatting colorFormatting = from(color);
            component = component.formatted(colorFormatting);
        }
        if (style != null) {
            Formatting styleFormatting = from(style);
            component = component.formatted(styleFormatting);
        }
        if (action != null) {
            if (action instanceof OpenLinkAction) {
                OpenLinkAction openLinkAction = (OpenLinkAction)action;
                Style clickEvent = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, openLinkAction.url));
                component.setStyle(clickEvent);
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
    public TextBuilder add(String text, TextStyle textStyle) {
        return add(text, null, textStyle, null);
    }

    @Override
    public TextBuilder add(String text, TextColor color) {
        return add(text, color, null, null);
    }

    @Override
    public TextBuilder add(String text, TextColor color, TextStyle style) {
        return add(text, color, style, null);
    }

    @Override
    public TextBuilder add(String text, TextAction action) {
        return add(text, null, null, action);
    }

    @Override
    public String formattedString() {
        return text.getString();
    }

    @Override
    public String toJSON() {
        return Text.Serializer.toJson(text);
    }

    private static Formatting from(TextColor color) {
        if (color == null) {
            return null;
        }
        switch (color) {
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
            default:
                throw new IllegalStateException("unrecognised color " + color);
        }
    }

    private static Formatting from(TextStyle textStyle) {
        if (textStyle == null) {
            return null;
        }
        switch (textStyle) {
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
                throw new IllegalStateException("unknown formatting " + textStyle);
        }
    }
}
