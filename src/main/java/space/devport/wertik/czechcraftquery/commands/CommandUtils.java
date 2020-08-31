package space.devport.wertik.czechcraftquery.commands;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.devport.utils.text.language.LanguageManager;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;

import java.text.ParseException;

@UtilityClass
public class CommandUtils {

    public RequestContext parseContext(CommandSender sender, String[] args) {
        String serverSlug = args[1];

        RequestContext context = new RequestContext(serverSlug);

        if (args.length > 2) {
            String month = CommandUtils.attemptParseMonth(args[2]);
            String username = null;

            if (month == null) {
                username = CommandUtils.attemptParseUsername(sender, args[2]);
                if (args.length > 3)
                    month = CommandUtils.attemptParseMonth(args[3]);
            } else {
                if (args.length > 3)
                    username = CommandUtils.attemptParseUsername(sender, args[3]);
            }

            context.month(month);
            context.user(username);
        }
        return context;
    }

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