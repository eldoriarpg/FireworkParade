package de.eldoria.fireworkparade;

import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.format.Style;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import org.bukkit.entity.Player;

public final class MessageSender {
    private static final String PREFIX = "§6[FP] ";
    private static final String DEFAULT_MESSAGE_COLOR = "§r§2";
    private static final String DEFAULT_ERROR_COLOR = "§r§c";

    private MessageSender() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Send a message to a player
     *
     * @param player  receiver of the message
     * @param message message with optinal color codes
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + DEFAULT_MESSAGE_COLOR + message.replaceAll("§r", DEFAULT_MESSAGE_COLOR));
    }

    /**
     * Sends a error to a player
     *
     * @param player  receiver of the message
     * @param message message with optinal color codes
     */
    public static void sendError(Player player, String message) {
        player.sendMessage(PREFIX + DEFAULT_MESSAGE_COLOR + message.replaceAll("§r", DEFAULT_ERROR_COLOR));
    }

    public static void sendCommandSuggestion(Player player, String text, String command) {
        TextComponent prefix = TextComponent.builder()
                .color(TextColor.GOLD)
                .content("[FP] ")
                .build();
        TextComponent message = TextComponent.builder()
                .content("[" + text + "]")
                .style(Style.builder().color(TextColor.AQUA).clickEvent(ClickEvent.suggestCommand(command)).decoration(TextDecoration.UNDERLINED, true).build())
                .build();
        TextComponent finalText = TextComponent.builder().append(prefix).append(message).build();
        TextAdapter.sendMessage(player, finalText);
    }

    public static void sendCommandExecution(Player player, String text, String command) {
        TextComponent prefix = TextComponent.builder()
                .color(TextColor.GOLD)
                .content("[FP] ")
                .build();
        TextComponent message = TextComponent.builder()
                .content("[" + text + "]")
                .style(Style.builder().color(TextColor.AQUA).clickEvent(ClickEvent.runCommand(command)).decoration(TextDecoration.UNDERLINED, true).build())
                .build();
        TextComponent finalText = TextComponent.builder().append(prefix).append(message).build();
        TextAdapter.sendMessage(player, finalText);
    }

    public static void sendTextComponents(Player p, TextComponent... components) {
        TextComponent prefix = TextComponent.builder()
                .color(TextColor.GOLD)
                .content("[FP] ")
                .build();
        TextComponent.Builder builder = TextComponent.builder().append(prefix);
        for (TextComponent component : components) {
            builder.append(component);
        }
        TextAdapter.sendMessage(p, builder.build());
    }
}
