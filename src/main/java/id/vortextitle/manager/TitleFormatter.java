package id.vortextitle.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TitleFormatter {

    private static final MiniMessage MINI =
            MiniMessage.miniMessage();

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacySection();

    private static final Pattern AMP_HEX =
            Pattern.compile("(?i)&#([0-9a-f]{6})");

    private static final Pattern SECTION_HEX =
            Pattern.compile("(?i)§x((?:§[0-9a-f]){6})");

    private static final Pattern AMP_X_HEX =
            Pattern.compile("(?i)&x((&[0-9a-f]){6})");

    private static final Pattern LEGACY_CODES =
            Pattern.compile("(?i)[&§]([0-9a-fk-or])");

    private TitleFormatter() {}

    public static Component parse(String input) {

        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        String converted = convertRGB(input);

        converted = convertLegacy(converted);

        try {

            return MINI.deserialize(converted);

        } catch (Exception exception) {

            try {

                return LegacyComponentSerializer
                        .legacyAmpersand()
                        .deserialize(input);

            } catch (Exception ignored) {

                return Component.text(input);
            }
        }
    }

    public static String toLegacy(String input) {

        return LEGACY.serialize(parse(input));
    }

    public static String plain(String input) {

        return PlainTextComponentSerializer
                .plainText()
                .serialize(parse(input));
    }

    private static String convertRGB(String input) {

        Matcher matcher = AMP_HEX.matcher(input);

        StringBuffer output = new StringBuffer();

        while (matcher.find()) {

            matcher.appendReplacement(
                    output,
                    "<#" + matcher.group(1).toUpperCase(Locale.ROOT) + ">"
            );
        }

        matcher.appendTail(output);

        String result = output.toString();

        matcher = SECTION_HEX.matcher(result);

        output = new StringBuffer();

        while (matcher.find()) {

            String hex = matcher.group(1)
                    .replace("§", "");

            matcher.appendReplacement(
                    output,
                    "<#" + hex.toUpperCase(Locale.ROOT) + ">"
            );
        }

        matcher.appendTail(output);

        result = output.toString();

        matcher = AMP_X_HEX.matcher(result);

        output = new StringBuffer();

        while (matcher.find()) {

            String hex = matcher.group(1)
                    .replace("&", "");

            matcher.appendReplacement(
                    output,
                    "<#" + hex.toUpperCase(Locale.ROOT) + ">"
            );
        }

        matcher.appendTail(output);

        return output.toString();
    }

    private static String convertLegacy(String input) {

        Matcher matcher = LEGACY_CODES.matcher(input);

        StringBuffer output = new StringBuffer();

        while (matcher.find()) {

            String code = matcher.group(1)
                    .toLowerCase(Locale.ROOT);

            String tag = switch (code) {

                case "0" -> "<black>";
                case "1" -> "<dark_blue>";
                case "2" -> "<dark_green>";
                case "3" -> "<dark_aqua>";
                case "4" -> "<dark_red>";
                case "5" -> "<dark_purple>";
                case "6" -> "<gold>";
                case "7" -> "<gray>";
                case "8" -> "<dark_gray>";
                case "9" -> "<blue>";

                case "a" -> "<green>";
                case "b" -> "<aqua>";
                case "c" -> "<red>";
                case "d" -> "<light_purple>";
                case "e" -> "<yellow>";
                case "f" -> "<white>";

                case "k" -> "<obfuscated>";
                case "l" -> "<bold>";
                case "m" -> "<strikethrough>";
                case "n" -> "<underlined>";
                case "o" -> "<italic>";
                case "r" -> "<reset>";

                default -> null;
            };

            if (tag == null) {

                matcher.appendReplacement(
                        output,
                        Matcher.quoteReplacement(matcher.group())
                );

            } else {

                matcher.appendReplacement(
                        output,
                        Matcher.quoteReplacement(tag)
                );
            }
        }

        matcher.appendTail(output);

        return output.toString();
    }
  }
