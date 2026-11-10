
package id.vortextitle.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TitleFormatter {

    private static final MiniMessage MINI =
            MiniMessage.miniMessage();

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacySection();

    private static final LegacyComponentSerializer LEGACY_AMPERSAND =
            LegacyComponentSerializer.legacyAmpersand();

    private static final Pattern AMP_HEX =
            Pattern.compile("(?i)&#([0-9a-f]{6})");

    private static final Pattern SECTION_HEX =
            Pattern.compile("(?i)§x((?:§[0-9a-f]){6})");

    private static final Pattern AMP_X_HEX =
            Pattern.compile("(?i)&x((&[0-9a-f]){6})");

    private static final Pattern LEGACY_CODES =
            Pattern.compile("(?i)[&§]([0-9a-fk-or])");

    private static final String TITLE_PLACEHOLDER = "<vortex_title>";

    private TitleFormatter() {
    }

    /**
     * Parses MiniMessage, HEX, RGB and legacy formatting.
     */
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
                return LEGACY_AMPERSAND.deserialize(input);

            } catch (Exception ignored) {
                return Component.text(input);
            }
        }
    }

    /**
     * Formats a plain title using the title-colors section
     * from config.yml.
     *
     * The stored title is always treated as literal text.
     */
    public static Component formatTitle(
            String rawTitle,
            FileConfiguration config
    ) {

        if (rawTitle == null || rawTitle.isEmpty()) {
            return Component.empty();
        }

        if (config == null) {
            return Component.text(rawTitle);
        }

        ConfigurationSection section =
                config.getConfigurationSection("title-colors");

        if (section == null) {
            return Component.text(rawTitle);
        }

        if (!section.getBoolean("enabled", true)) {
            return Component.text(rawTitle);
        }

        String selectedFormat = null;

        ConfigurationSection formats =
                section.getConfigurationSection("formats");

        /*
         * Find title format without case sensitivity.
         */
        if (formats != null) {

            for (String key : formats.getKeys(false)) {

                if (key.equalsIgnoreCase(rawTitle)) {

                    selectedFormat = formats.getString(key);
                    break;
                }
            }
        }

        /*
         * Use default-format when no custom format exists.
         */
        if (selectedFormat == null || selectedFormat.isEmpty()) {

            selectedFormat = section.getString(
                    "default-format",
                    "{title}"
            );
        }

        if (selectedFormat == null || selectedFormat.isEmpty()) {
            return Component.text(rawTitle);
        }

        /*
         * Replace {title} with a dedicated MiniMessage placeholder.
         * Placeholder.component prevents raw title text from being
         * interpreted as MiniMessage formatting.
         */
        String preparedFormat = selectedFormat.replace(
                "{title}",
                TITLE_PLACEHOLDER
        );

        try {

            String converted = convertRGB(preparedFormat);
            converted = convertLegacy(converted);

            return MINI.deserialize(
                    converted,
                    Placeholder.component(
                            "vortex_title",
                            Component.text(rawTitle)
                    )
            );

        } catch (Exception exception) {

            /*
             * If the configured format is invalid,
             * safely display the original title.
             */
            return Component.text(rawTitle);
        }
    }

    /**
     * Returns the formatted title as legacy text.
     */
    public static String formatTitleLegacy(
            String rawTitle,
            FileConfiguration config
    ) {

        return LEGACY.serialize(
                formatTitle(rawTitle, config)
        );
    }

    /**
     * Converts a formatted string into legacy section text.
     */
    public static String toLegacy(String input) {

        return LEGACY.serialize(parse(input));
    }

    /**
     * Removes formatting and returns plain text.
     */
    public static String plain(String input) {

        return PlainTextComponentSerializer
                .plainText()
                .serialize(parse(input));
    }

    /**
     * Converts the formatted title component into plain text.
     */
    public static String plainTitle(
            String rawTitle,
            FileConfiguration config
    ) {

        return PlainTextComponentSerializer
                .plainText()
                .serialize(formatTitle(rawTitle, config));
    }

    /**
     * Converts supported HEX and RGB formats into MiniMessage.
     *
     * Supported:
     * &#RRGGBB
     * §x§R§R§G§G§B§B
     * &x&R&R&G&G&B&B
     */
    private static String convertRGB(String input) {

        Matcher matcher = AMP_HEX.matcher(input);

        StringBuffer output = new StringBuffer();

        while (matcher.find()) {

            String replacement =
                    "<#" + matcher.group(1).toUpperCase(Locale.ROOT) + ">";

            matcher.appendReplacement(
                    output,
                    Matcher.quoteReplacement(replacement)
            );
        }

        matcher.appendTail(output);

        String result = output.toString();

        matcher = SECTION_HEX.matcher(result);

        output = new StringBuffer();

        while (matcher.find()) {

            String hex = matcher.group(1)
                    .replace("§", "");

            String replacement =
                    "<#" + hex.toUpperCase(Locale.ROOT) + ">";

            matcher.appendReplacement(
                    output,
                    Matcher.quoteReplacement(replacement)
            );
        }

        matcher.appendTail(output);

        result = output.toString();

        matcher = AMP_X_HEX.matcher(result);

        output = new StringBuffer();

        while (matcher.find()) {

            String hex = matcher.group(1)
                    .replace("&", "");

            String replacement =
                    "<#" + hex.toUpperCase(Locale.ROOT) + ">";

            matcher.appendReplacement(
                    output,
                    Matcher.quoteReplacement(replacement)
            );
        }

        matcher.appendTail(output);

        return output.toString();
    }

    /**
     * Converts legacy color codes into MiniMessage tags.
     *
     * Supports:
     * &a, &b, &c, &l, &o, &n, &m, &k, &r
     * §a, §b, §c, §l, §o, §n, §m, §k, §r
     */
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
