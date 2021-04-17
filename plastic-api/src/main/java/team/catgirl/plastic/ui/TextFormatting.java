package team.catgirl.plastic.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TextFormatting {
    BLACK(Type.COLOR),
    DARK_BLUE(Type.COLOR),
    DARK_GREEN(Type.COLOR),
    DARK_AQUA(Type.COLOR),
    DARK_RED(Type.COLOR),
    DARK_PURPLE(Type.COLOR),
    GOLD(Type.COLOR),
    GRAY(Type.COLOR),
    DARK_GRAY(Type.COLOR),
    BLUE(Type.COLOR),
    GREEN(Type.COLOR),
    AQUA(Type.COLOR),
    RED(Type.COLOR),
    LIGHT_PURPLE(Type.COLOR),
    YELLOW(Type.COLOR),
    WHITE(Type.COLOR),
    OBFUSCATED(Type.FORMAT),
    BOLD(Type.FORMAT),
    STRIKETHROUGH(Type.FORMAT),
    UNDERLINE(Type.FORMAT),
    ITALIC(Type.FORMAT),
    RESET(Type.FORMAT);

    public final Type type;

    TextFormatting(Type type) {
        this.type = type;
    }

    public static List<TextFormatting> colors() {
        return Arrays.stream(TextFormatting.values()).filter(textFormatting -> textFormatting.type == Type.COLOR).collect(Collectors.toList());
    }

    public enum Type {
        COLOR,
        FORMAT;
    }
}
