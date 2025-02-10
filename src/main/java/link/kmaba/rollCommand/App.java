package link.kmaba.rollCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class App extends JavaPlugin {
    private Map<String, CommandList> commandLists;
    private Map<String, Long> cooldowns;
    private String chosenList;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        loadConfiguration();
        cooldowns = new HashMap<>();
        getLogger().info("RollCommand enabled - Loaded " + commandLists.size() + " command lists");
        getCommand("rlist").setTabCompleter(new RollCommandTabCompleter(this));
    }

    private void loadConfiguration() {
        commandLists = new HashMap<>();
        reloadConfig(); // Ensure we have fresh config data
        
        ConfigurationSection lists = getConfig().getConfigurationSection("commandLists");
        chosenList = getConfig().getString("chosenList", "exampleList");

        if (lists == null) {
            getLogger().warning("No commandLists section found in config!");
            return;
        }

        for (String listName : lists.getKeys(false)) {
            ConfigurationSection listSection = lists.getConfigurationSection(listName);
            if (listSection == null) {
                getLogger().warning("Invalid section for list: " + listName);
                continue;
            }

            int cooldown = listSection.getInt("cooldown", 60);
            CommandList cmdList = new CommandList(cooldown);

            List<Map<?, ?>> commands = listSection.getMapList("commands");
            if (commands != null && !commands.isEmpty()) {
                for (Map<?, ?> cmdMap : commands) {
                    String cmd = String.valueOf(cmdMap.get("command"));
                    int weight = cmdMap.containsKey("weight") ? 
                        Integer.parseInt(String.valueOf(cmdMap.get("weight"))) : 1;
                    
                    cmdList.addCommand(cmd, weight);
                    getLogger().info("Added command: " + cmd + " with weight: " + weight + " to list: " + listName);
                }
            } else {
                getLogger().warning("No commands found for list: " + listName);
            }

            commandLists.put(listName, cmdList);
            getLogger().info("Loaded command list: " + listName);
        }
        
        getLogger().info("Loaded " + commandLists.size() + " command lists. Available lists: " + 
                        String.join(", ", commandLists.keySet()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("roll")) {
            return handleRollCommand(sender);
        } else if (command.getName().equalsIgnoreCase("rlist")) {
            return handleRListCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("rtest")) {
            return handleTestRollCommand(sender);
        }
        return false;
    }

    private boolean handleRollCommand(CommandSender sender) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }

            Player player = (Player) sender;
            CommandList list = commandLists.get(chosenList);
            
            if (list == null) {
                player.sendMessage("No valid command list selected!");
                return true;
            }

            if (list.isEmpty()) {
                player.sendMessage("The selected command list is empty!");
                getLogger().warning("Command list '" + chosenList + "' is empty!");
                return true;
            }

            long currentTime = System.currentTimeMillis();
            long lastUse = cooldowns.getOrDefault(player.getName(), 0L);
            
            if (currentTime - lastUse < list.getCooldown() * 1000) {
                player.sendMessage("You must wait before using this command again!");
                return true;
            }

            String randomCommand = list.getRandomCommand();
            if (randomCommand != null) {
                randomCommand = randomCommand.replace("%p", player.getName());
                getServer().dispatchCommand(getServer().getConsoleSender(), randomCommand.substring(1));
                cooldowns.put(player.getName(), currentTime);
                return true;
            } else {
                player.sendMessage("Failed to get a random command!");
                getLogger().warning("Failed to get random command from list '" + chosenList + "'");
                return true;
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error executing roll command", e);
            sender.sendMessage("An error occurred while executing the command!");
            return true;
        }
    }

    private boolean handleRListCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String listName = args[0];
            if (commandLists.containsKey(listName)) {
                chosenList = listName;
                getConfig().set("chosenList", listName);
                saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Selected command list: " + ChatColor.YELLOW + listName);
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid list name! Available lists: " + 
                                 ChatColor.YELLOW + String.join(", ", commandLists.keySet()));
            }
        } else {
            // Display all available lists
            sender.sendMessage(ChatColor.GREEN + "=== Available Command Lists ===");
            sender.sendMessage(ChatColor.YELLOW + "Current list: " + ChatColor.WHITE + chosenList);
            sender.sendMessage("");

            for (Map.Entry<String, CommandList> entry : commandLists.entrySet()) {
                String listName = entry.getKey();
                CommandList list = entry.getValue();
                
                sender.sendMessage(ChatColor.GOLD + listName + ":" + 
                                 (listName.equals(chosenList) ? ChatColor.GREEN + " (SELECTED)" : ""));
                sender.sendMessage(ChatColor.GRAY + "  Cooldown: " + ChatColor.WHITE + list.getCooldown() + "s");
                
                List<String> commands = list.getCommands();
                for (int i = 0; i < commands.size(); i++) {
                    String cmd = commands.get(i);
                    int weight = list.getWeight(i);
                    sender.sendMessage(ChatColor.DARK_GRAY + "  - " + 
                                     ChatColor.WHITE + cmd + 
                                     ChatColor.GRAY + " (weight: " + weight + ")");
                }
                sender.sendMessage("");
            }
        }
        return true;
    }

    private boolean handleTestRollCommand(CommandSender sender) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                return true;
            }

            Player player = (Player) sender;
            CommandList list = commandLists.get(chosenList);
            
            if (list == null) {
                player.sendMessage(ChatColor.RED + "No valid command list selected!");
                return true;
            }

            if (list.isEmpty()) {
                player.sendMessage(ChatColor.RED + "The selected command list is empty!");
                return true;
            }

            String randomCommand = list.getRandomCommand();
            if (randomCommand != null) {
                randomCommand = randomCommand.replace("%p", player.getName());
                getServer().dispatchCommand(getServer().getConsoleSender(), randomCommand.substring(1));
                player.sendMessage(ChatColor.GREEN + "Test command executed!");
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "Failed to get a random command!");
                return true;
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error executing test roll command", e);
            sender.sendMessage(ChatColor.RED + "An error occurred while executing the command!");
            return true;
        }
    }

    public List<String> getCommandListNames() {
        return new ArrayList<>(commandLists.keySet());
    }
}