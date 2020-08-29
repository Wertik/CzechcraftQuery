package space.devport.wertik.czechcraftquery.commands;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.devport.utils.text.language.LanguageManager;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

import java.text.ParseException;

@UtilityClass
public class CommandUtils {

    public String attemptParseMonth(String input) {
        try {
            QueryPlugin.MONTH_FORMAT.parse(input);
            return input;
        } catch (ParseException e) {
            return null;
        }
    }

    public String attemptParseUsername(CommandSender sender, String input) {
        String username;
        if (input.equalsIgnoreCase("me")) {
            if (!(sender instanceof Player)) return null;

            username = sender.getName();
        } else username = input;
        return username;
    }

    public RequestType parseRequestType(CommandSender sender, LanguageManager language, String arg) {
        RequestType type = RequestType.fromString(arg);

        if (type == null) {
            language.getPrefixed("Commands.Invalid-Type")
                    .replace("%param%", arg)
                    .send(sender);
            return null;
        }
        return type;
    }
}