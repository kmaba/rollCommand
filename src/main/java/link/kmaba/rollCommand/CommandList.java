package link.kmaba.rollCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandList {
    private List<WeightedCommand> commands;
    private int cooldown;
    private final Random random;

    public CommandList(int cooldown) {
        this.commands = new ArrayList<>();
        this.cooldown = cooldown;
        this.random = new Random();
    }

    public void addCommand(String command, int weight) {
        commands.add(new WeightedCommand(command, weight));
    }

    public String getRandomCommand() {
        if (commands.isEmpty()) {
            return null;
        }
        
        int totalWeight = commands.stream().mapToInt(WeightedCommand::getWeight).sum();
        if (totalWeight <= 0) {
            return commands.get(0).getCommand(); // fallback to first command
        }
        
        int randomWeight = random.nextInt(totalWeight);
        
        int currentWeight = 0;
        for (WeightedCommand command : commands) {
            currentWeight += command.getWeight();
            if (randomWeight < currentWeight) {
                return command.getCommand();
            }
        }
        return commands.get(0).getCommand(); // fallback to first command
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }

    public List<String> getCommands() {
        List<String> commandList = new ArrayList<>();
        for (WeightedCommand cmd : commands) {
            commandList.add(cmd.getCommand());
        }
        return commandList;
    }

    public int getWeight(int index) {
        if (index >= 0 && index < commands.size()) {
            return commands.get(index).getWeight();
        }
        return 0;
    }

    private static class WeightedCommand {
        private final String command;
        private final int weight;

        public WeightedCommand(String command, int weight) {
            this.command = command;
            this.weight = weight;
        }

        public String getCommand() {
            return command;
        }

        public int getWeight() {
            return weight;
        }
    }
}
