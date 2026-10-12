package net.ludovicoflaviano.simplepaperkits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public final class SimplePaperKits extends JavaPlugin {
    private File kitsFile;
    private YamlConfiguration kits;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) getLogger().severe("Could not create plugin data folder.");
        kitsFile = new File(getDataFolder(), "kits.yml");
        kits = YamlConfiguration.loadConfiguration(kitsFile);
        getLogger().info("SimplePaperKits enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("kit")) return false;
        if (args.length < 2) {
            sender.sendMessage(color("&eUsage: /kit load <name> [player] &7| &e/kit create <name>"));
            return true;
        }
        String action = args[0].toLowerCase(Locale.ROOT);
        String name = args[1].toLowerCase(Locale.ROOT);
        if (!name.matches("[a-z0-9_-]{1,32}")) {
            sender.sendMessage(color("&cKit names may only use letters, numbers, _ and -, up to 32 characters."));
            return true;
        }
        if (action.equals("create")) {
            if (!sender.hasPermission("simplepaperkits.create")) {
                sender.sendMessage(color("&cOnly operators can create kits."));
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage(color("&cRun this command in-game while holding the inventory you want to save."));
                return true;
            }
            saveKit(name, player.getInventory());
            sender.sendMessage(color("&aSaved kit &f" + name + "&a."));
            return true;
        }
        if (action.equals("load")) {
            if (!sender.hasPermission("simplepaperkits.load")) {
                sender.sendMessage(color("&cYou do not have permission to load kits."));
                return true;
            }
            Player target;
            if (args.length >= 3) {
                if (sender instanceof Player && !sender.hasPermission("simplepaperkits.create")) {
                    sender.sendMessage(color("&cYou can only load kits for yourself."));
                    return true;
                }
                List<Entity> selected;
                try {
                    selected = Bukkit.selectEntities(sender, args[2]);
                } catch (IllegalArgumentException exception) {
                    sender.sendMessage(color("&cInvalid player target."));
                    return true;
                }
                target = selected.stream().filter(Player.class::isInstance).map(Player.class::cast).findFirst().orElse(null);
                if (target == null) {
                    sender.sendMessage(color("&cNo matching online player found."));
                    return true;
                }
            } else if (sender instanceof Player player) {
                target = player;
            } else {
                sender.sendMessage(color("&cFrom console or a command block, specify a player: /kit load <name> <player>."));
                return true;
            }
            ConfigurationSection section = kits.getConfigurationSection("kits." + name);
            if (section == null) {
                sender.sendMessage(color("&cKit &f" + name + " &cdoes not exist."));
                return true;
            }
            loadKit(section, target.getInventory());
            target.updateInventory();
            target.sendMessage(color("&aLoaded kit &f" + name + "&a."));
            if (sender != target) sender.sendMessage(color("&aLoaded kit &f" + name + " &afor &f" + target.getName() + "&a."));
            return true;
        }
        sender.sendMessage(color("&eUsage: /kit load <name> [player] &7| &e/kit create <name>"));
        return true;
    }

    private void saveKit(String name, PlayerInventory inventory) {
        String root = "kits." + name;
        kits.set(root + ".contents", inventory.getContents());
        kits.set(root + ".armor", inventory.getArmorContents());
        kits.set(root + ".offhand", inventory.getItemInOffHand());
        try {
            kits.save(kitsFile);
        } catch (IOException exception) {
            getLogger().severe("Could not save kit " + name + ": " + exception.getMessage());
        }
    }

    private void loadKit(ConfigurationSection section, PlayerInventory inventory) {
        inventory.clear();
        Object contentsValue = section.get("contents");
        if (contentsValue instanceof ItemStack[] contents) inventory.setContents(contents);
        Object armorValue = section.get("armor");
        if (armorValue instanceof ItemStack[] armor) inventory.setArmorContents(armor);
        ItemStack offhand = section.getItemStack("offhand");
        inventory.setItemInOffHand(offhand == null ? new ItemStack(Material.AIR) : offhand);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
