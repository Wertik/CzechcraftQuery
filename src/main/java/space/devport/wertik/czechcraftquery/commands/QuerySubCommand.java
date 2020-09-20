package space.devport.wertik.czechcraftquery.commands;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.wertik.czechcraftquery.QueryPlugin;

public abstract class QuerySubCommand extends SubCommand {

    @Getter
    protected final QueryPlugin plugin;

    public QuerySubCommand(QueryPlugin plugin, String name) {
        super(name);
        setPermissions();
        this.plugin = plugin;
    }

    @Override
    public @Nullable
    abstract String getDefaultUsage();

    @Override
    public @Nullable
    abstract String getDefaultDescription();

    @Override
    public @Nullable
    abstract ArgumentRange getRange();
}
