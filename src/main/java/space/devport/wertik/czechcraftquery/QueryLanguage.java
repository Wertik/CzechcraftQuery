package space.devport.wertik.czechcraftquery;

import space.devport.utils.DevportPlugin;
import space.devport.utils.text.language.LanguageDefaults;

public class QueryLanguage extends LanguageDefaults {

    public QueryLanguage(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public void setDefaults() {
        addDefault("Commands.Invalid-Type", "&cRequest type &f%param% &cis not valid.");
        addDefault("Commands.Invalid-Context", "&cInvalid context provided for type &f%type%");

        addDefault("Commands.Request.Sending", "&7&oSending a request for %type%...");
        addDefault("Commands.Request.Done", "&7Got a response: &f%response%");

        addDefault("Commands.Get.Done", "&7Fetched response for &f%type%: &f%response%");

        addDefault("Commands.Clear.Done-Single", "&7Cleared &f%count% &7responses for type &f%type%");
        addDefault("Commands.Clear.Done", "&7Cleared &f%count% &7responses in all type handlers.");

        addDefault("Commands.Stop.Not-Running", "&cUpdate task for type &f%type% &cis not running.");
        addDefault("Commands.Stop.Done-Single", "&7Update task for type &f%type% &7stopped.");
        addDefault("Commands.Stop.Done", "&7Stopped all update tasks.");

        addDefault("Commands.Start.Done-Single", "&7Request handler update task started for type &f%type%");
        addDefault("Commands.Start.Done", "&7Request handler tasks started.");

        addDefault("Commands.Test.Invalid-Test", "&cTest &f%param% &cis not loaded.");
        addDefault("Commands.Test.Starting", "&7&oSupplying the test contents...");
        addDefault("Commands.Test.Done", "&7&oParsed and cached the response...", "&7Type: &f%type%", "&7Context: &f%context%", "&7Contents: &f%response%");
    }
}
