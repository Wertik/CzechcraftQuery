package space.devport.wertik.czechcraftquery;

import space.devport.utils.text.language.LanguageDefaults;

public class QueryLanguage extends LanguageDefaults {

    @Override
    public void setDefaults() {
        addDefault("Commands.Invalid-Type", "&cRequest type &f%param% &cis not valid.");
        addDefault("Commands.Invalid-Context", "&cInvalid context provided for type &f%type%");

        addDefault("Commands.Request.Done", "&7Request for &f%type% &7sent, response: &f%response%");

        addDefault("Commands.Clear.Done-Single", "&7Cleared &f%count% &7responses for type &f%type%");
        addDefault("Commands.Clear.Done", "&7Cleared &f%count% &7responses in all type handlers.");

        addDefault("Commands.Stop.Not-Running", "&cUpdate task for type &f%type% &cis not running.");
        addDefault("Commands.Stop.Done-Single", "&7Update task for type &f%type% &7stopped.");
        addDefault("Commands.Stop.Done", "&7Stopped all update tasks.");

        addDefault("Commands.Start.Done-Single", "&7Request handler update task started for type &f%type%");
        addDefault("Commands.Start.Done", "&7Request handler tasks started.");
    }
}
