package link.kmaba.rollCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RollCommandTabCompleter implements TabCompleter {
    private final App plugin;

    public RollCommandTabCompleter(App plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (command.getName().equalsIgnoreCase("rlist") && args.length == 1) {
            completions.addAll(plugin.getCommandListNames());
        }
        
        return completions;
    }
}
