package space.devport.wertik.czechcraftquery;

import space.devport.utils.text.language.LanguageDefaults;

public class QueryLanguage extends LanguageDefaults {

    @Override
    public void setDefaults() {
        addDefault("Commands.Invalid-Type", "&cRequest type &f%param% &cis not valid.");
        addDefault("Commands.Invalid-Context", "&cInvalid context provided for type &f%type%");

        addDefault("Commands.Request.Done", "&7Request for &f%type% &7sent, response: &f%response%");
    }
}
