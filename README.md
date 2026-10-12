# SimplePaperKits

A small kit plugin for Paper 1.21.11.

## Features
- Every player can use `/kit load <name>`.
- Only operators can use `/kit create <name>`.
- Saves inventory slots, armor, and offhand to `plugins/SimplePaperKits/kits.yml`.
- Command blocks can load a kit for the nearest player by targeting `@p`.

## Build
Requires JDK 21 and Gradle.

```bash
gradle build
```
The plugin JAR is created in `build/libs/`.

## Install
1. Put `SimplePaperKits-1.0.0.jar` in the server's `plugins` folder.
2. Restart the Paper 1.21.11 server.
3. As an OP, equip the inventory you want to save and run `/kit create diamondsmp`.
4. Players can run `/kit load diamondsmp`.

## Button + command block
Set a command block to **Impulse**, **Unconditional**, and **Needs Redstone**. Put a button on it and use this command, without the leading slash:

```text
kit load diamondsmp @p
```

The command block will give the kit to the nearest online player. Make sure command blocks are enabled in `server.properties` (`enable-command-block=true`) and restart the server after changing it.

## Notes
- Kit names can contain lowercase letters, numbers, underscores, and hyphens.
- Loading a kit replaces the target player's current inventory, armor, and offhand.
- Creating a kit overwrites an existing kit with the same name.
- The plugin uses Paper's Bukkit API and targets Java 21.
