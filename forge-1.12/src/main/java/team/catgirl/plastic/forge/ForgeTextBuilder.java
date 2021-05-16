package team.catgirl.plastic.forge;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import team.catgirl.plastic.ui.TextAction;
import team.catgirl.plastic.ui.TextAction.OpenLinkAction;
import team.catgirl.plastic.ui.TextBuilder;
import team.catgirl.plastic.ui.TextColor;
import team.catgirl.plastic.ui.TextStyle;

public final class ForgeTextBuilder extends TextBuilder {

    private final ITextComponent componentString;

    public ForgeTextBuilder(ITextComponent componentString) {
        this.componentString = componentString;
    }

    public ForgeTextBuilder() {
        this(new TextComponentString(""));
    }

    @Override
    public TextBuilder add(String text, TextColor color, TextStyle style, TextAction action) {
        TextComponentString component = new TextComponentString(text);
        net.minecraft.util.text.TextFormatting textFormatting = from(style);
        if (textFormatting != null) {
            component.getStyle().setColor(textFormatting);
        }
        net.minecraft.util.text.TextFormatting textColor = from(color);
        if (textColor != null) {
            component.getStyle().setColor(textColor);
        }
        if (action != null) {
            if (action instanceof OpenLinkAction) {
                OpenLinkAction openLinkAction = (OpenLinkAction) action;
                component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, openLinkAction.url));
            }
        }
        componentString.appendSibling(component);
        return this;
    }

    @Override
    public TextBuilder add(String text) {
        return add(text, null, null, null);
    }

    @Override
    public TextBuilder add(String text, TextStyle textStyle) {
        return add(text, null, textStyle, null);
    }

    @Override
    public TextBuilder add(String text, TextAction action) {
        return add(text, null, null, action);
    }

    @Override
    public TextBuilder add(String text, TextColor color) {
        return add(text, color, null, null);
    }

    @Override
    public TextBuilder add(String text, TextColor color, TextStyle textStyle) {
        return add(text, color, textStyle, null);
    }

    @Override
    public String formattedString() {
        return componentString.getFormattedText();
    }

    @Override
    public String toJSON() {
        return ITextComponent.Serializer.componentToJson(componentString);
    }

    net.minecraft.util.text.TextFormatting from(TextColor color) {
        if (color == null) {
            return null;
        }
        switch (color) {
            case RED:
                return net.minecraft.util.text.TextFormatting.RED;
            case AQUA:
                return net.minecraft.util.text.TextFormatting.AQUA;
            case BLACK:
                return net.minecraft.util.text.TextFormatting.BLACK;
            case BLUE:
                return net.minecraft.util.text.TextFormatting.BLUE;
            case GOLD:
                return net.minecraft.util.text.TextFormatting.GOLD;
            case GRAY:
                return net.minecraft.util.text.TextFormatting.GRAY;
            case GREEN:
                return net.minecraft.util.text.TextFormatting.GREEN;
            case WHITE:
                return net.minecraft.util.text.TextFormatting.WHITE;
            case YELLOW:
                return net.minecraft.util.text.TextFormatting.YELLOW;
            case DARK_AQUA:
                return net.minecraft.util.text.TextFormatting.DARK_AQUA;
            case DARK_BLUE:
                return net.minecraft.util.text.TextFormatting.DARK_BLUE;
            case DARK_GRAY:
                return net.minecraft.util.text.TextFormatting.DARK_GRAY;
            case DARK_GREEN:
                return net.minecraft.util.text.TextFormatting.DARK_GREEN;
            case DARK_PURPLE:
                return net.minecraft.util.text.TextFormatting.DARK_PURPLE;
            case DARK_RED:
                return net.minecraft.util.text.TextFormatting.DARK_RED;
            case LIGHT_PURPLE:
                return net.minecraft.util.text.TextFormatting.LIGHT_PURPLE;
            default:
                throw new IllegalStateException("unknown color " + color);
        }
    }

    net.minecraft.util.text.TextFormatting from(TextStyle style) {
        if (style == null) {
            return null;
        }
        switch (style) {
            case BOLD:
                return net.minecraft.util.text.TextFormatting.BOLD;
            case ITALIC:
                return net.minecraft.util.text.TextFormatting.ITALIC;
            case STRIKETHROUGH:
                return net.minecraft.util.text.TextFormatting.STRIKETHROUGH;
            case UNDERLINE:
                return net.minecraft.util.text.TextFormatting.UNDERLINE;
            case OBFUSCATED:
                return net.minecraft.util.text.TextFormatting.OBFUSCATED;
            case RESET:
                return net.minecraft.util.text.TextFormatting.RESET;
            default:
                throw new IllegalStateException("unknown formatting " + style);
        }
    }
}
