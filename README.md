# RollCommand Plugin

RollCommand is a Minecraft plugin that allows server administrators to create and manage lists of commands to be executed at random. With this plugin, you can configure multiple command lists—each with its own cooldown and weighted command selection—to add fun and variety to your server.

## Features

- **Random Command Execution:**  
  Execute a random command from the currently selected list using the `/roll` command.

- **Multiple Command Lists:**  
  Define several command lists with unique cooldown periods and command weights.

- **Weighted Commands:**  
  Assign weights to commands to influence the likelihood of selection.

- **In-Game Management:**  
  Use the `/rlist` command to display available command lists and select an active one.

- **Admin Testing:**  
  Execute a test command (`/rtest`) to run a random command without affecting cooldowns (Admin only).

## Commands

- **/roll** (alias: `/r`)  
  - Executes a random command from the active command list.
  - **Permission:** `rollCommand.use`

- **/rlist <list>**  
  - When provided with a list name, sets it as the active command list.
  - Without arguments, displays available command lists and the currently active one.
  - **Permission:** `rollCommand.admin`

- **/rtest**  
  - Executes a random command without enforcing the cooldown period.
  - **Permission:** `rollCommand.admin`

## Configuration

The plugin configuration is defined in [src/main/resources/config.yml](src/main/resources/config.yml). Below is an example configuration:

```yaml
chosenList: exampleList

commandLists:
  diamondList:
    cooldown: 120
    commands:
    - command: "/give %p diamond 64"
      weight: 1
    - command: "/give %p diamond_block 32"
      weight: 2
    - command: "/effect give %p haste 300 2"
      weight: 3
  exampleList:
    cooldown: 60
    commands:
    - command: "/give %p diamond 1"
      weight: 1
    - command: "/say Hello, %p!"
      weight: 2
  funList:
    cooldown: 30
    commands:
    - command: "/effect give %p jump_boost 30 3"
      weight: 3
    - command: "/effect give %p speed 30 2"
      weight: 2
    - command: "/give %p cookie 1"
      weight: 1
```

**Configuration Details:**

- **chosenList:**  
  Specifies the active command list to be used by the `/roll` and `/rtest` commands.

- **commandLists:**  
  Under this section, each command list (e.g., `exampleList`, `diamondList`, `funList`) can have its own cooldown (in seconds) and a list of commands.  
  Each command is defined along with a weight that influences its random selection probability.  
  `%p` in a command will be replaced by the player's name.

## Plugin Manifest

The plugin details are stored in plugin.yml. This file defines:

- The plugin **name** and **description**.
- The main class, which is `link.kmaba.rollCommand.App`.
- Commands (`/roll`, `/rlist`, and `/rtest`) along with their usage instructions.
- Required permissions:
  - `rollCommand.use` (default: true)
  - `rollCommand.admin` (default: op)

## Quick Start

1. **Deploy the Plugin:**  
   Place the compiled jar (`rollCommand-0.0.1.jar`) into your server's plugins folder.

2. **Configure:**  
   Adjust the settings in config.yml as needed.

3. **Start the Server:**  
   Launch or reload your server.

4. **Use the Commands:**  
   - Execute `/roll` / `/r` to perform a random command from the active list.
   - Use `/rlist` to view available command lists or change the active list.
   - Test commands with `/rtest` (requires admin permission).