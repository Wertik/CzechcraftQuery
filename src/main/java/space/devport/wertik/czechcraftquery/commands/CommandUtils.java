package space.devport.wertik.czechcraftquery.commands;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import space.devport.utils.text.language.LanguageManager;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

@UtilityClass
public class CommandUtils {

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