package com.sumeru.clans.command;

import com.sumeru.clans.QDClans;
import com.sumeru.clans.economy.Vault;
import com.sumeru.clans.gui.ClanMenu;
import com.sumeru.clans.utils.CooldownManager;
import com.sumeru.clans.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ClanCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "create":
                        if (args.length == 2) {
                            if (args[1].length() >= 4 && args[1].length() <= 16) {
                                if (args[1].matches("^[a-zA-Z0-9]+$")) {
                                    if (Vault.getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) >= QDClans.instance.getConfig().getInt("clan-creation-cost")) {
                                        if (Utils.getPlayerClan(player.getName())==null) {
                                            if (!Utils.clanAlreadyExist(args[1])) {
                                                List<ChatColor> colors = List.of(ChatColor.BLACK,
                                                        ChatColor.DARK_BLUE,
                                                        ChatColor.DARK_GREEN,
                                                        ChatColor.DARK_AQUA,
                                                        ChatColor.DARK_RED,
                                                        ChatColor.DARK_PURPLE,
                                                        ChatColor.GOLD,
                                                        ChatColor.GRAY,
                                                        ChatColor.DARK_GRAY,
                                                        ChatColor.BLUE,
                                                        ChatColor.GREEN,
                                                        ChatColor.AQUA,
                                                        ChatColor.RED,
                                                        ChatColor.LIGHT_PURPLE,
                                                        ChatColor.YELLOW);
                                                String glowColor = colors.get(ThreadLocalRandom.current().nextInt(colors.size())).name();
                                                ConfigurationSection clansSection = QDClans.instance.getConfig().createSection("clans." + args[1]);
                                                clansSection.set("players", List.of(player.getName()));
                                                clansSection.set("glowing-color", glowColor);
                                                clansSection.set("glowing-enabled", true);
                                                clansSection.set("max-players", 10);
                                                clansSection.set("money", 0);
                                                clansSection.set("home", null);
                                                clansSection.set("level", 1);
                                                clansSection.set("leader", player.getName());
                                                clansSection.set("pvp-enabled", true);
                                                QDClans.instance.saveConfig();
                                                String message = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_created_successfully"));
                                                message = message.replace("%clan_name%", args[1]);
                                                player.sendMessage(message);
                                                QDClans.glower.glowPlayer(player, glowColor);
                                                return true;
                                            } else {
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_creation_error_clan_exists")));
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_creation_error_already_member")));
                                        }
                                    } else {
                                        String error = QDClans.instance.getConfig().getString("messages.clan_creation_error_insufficient_balance");
                                        error = error.replace("%clan_creation_cost%", String.valueOf(QDClans.instance.getConfig().getInt("clan-creation-cost")));
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', error));
                                    }
                                }
                                else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_creation_error_invalid_characters")));
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_creation_error_name_length")));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED+"Укажите название клана!(убедитесь что также нет лишних аргументов)");
                        }
                        return false;
                    case "invite":
                        if (args.length == 2) {
                            String clan = Utils.getPlayerClan(player.getName());
                            if (clan != null) {
                                if (Bukkit.getPlayerExact(args[1])!=null) {
                                    if (Utils.getPlayerClan(args[1])==null) {
                                        ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans").getConfigurationSection(clan);

                                        if (clanSection != null) {
                                            if (clanSection.getStringList("players").size() < clanSection.getInt("max-players")) {
                                                if ((CooldownManager.cooldownGet(player.getUniqueId()))) {
                                                    player.sendMessage(ChatColor.RED + "Вы не можете выполнять команду так часто!");
                                                    return false;
                                                }
                                                Bukkit.getPlayerExact(args[1]).spigot().sendMessage(Utils.getInviteNotification(player.getName(), clan));
                                                CooldownManager.cooldownSet(player.getUniqueId());
                                                return true;
                                            } else {
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.invite_error_max_members_reached")));
                                            }
                                        }
                                    } else {
                                        String message = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.invite_error_member_exists"));
                                        message = message.replace("%player%", args[1]);
                                        player.sendMessage(message);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.invite_error_player_not_online")));
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.invite_error_no_clan")));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED+"Укажите игрока, которого желаете пригласить!(убедитесь что также нет лишних аргументов)");
                        }
                        return false;
                    case "menu":
                        if (args.length == 1) {
                            if (Utils.getPlayerClan(player.getName()) != null) {
                                ClanMenu menu = new ClanMenu(player);
                                player.openInventory(menu.getInventory());
                                return true;
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        }
                        return false;
                    case "sethome":
                        if (args.length == 1) {
                            String clanName = Utils.getPlayerClan(player.getName());

                            if (clanName != null) {
                                ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans").getConfigurationSection(clanName);
                                if (clanSection != null && clanSection.getInt("level")>=3) {
                                    if (Objects.equals(clanSection.getString("leader"), player.getName())) {
                                        clanSection.set("home", player.getLocation().getWorld().getName() + " " + player.getLocation().getBlockX()+" "+player.getLocation().getBlockY()+" "+player.getLocation().getBlockZ());
                                        QDClans.instance.saveConfig();
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.home_set_message")));
                                        return true;
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.not_clan_leader")));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED+"Ваш клан не достиг нужного уровня(3) для открытия этой функции(Текущий уровень: "+clanSection.getInt("level")+")");
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        }
                        return false;
                    case "home":
                        if (args.length == 1) {
                            String clanName = Utils.getPlayerClan(player.getName());

                            if (clanName != null) {
                                ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans").getConfigurationSection(clanName);
                                if (clanSection != null && clanSection.get("home")!=null) {
                                    String[] home = clanSection.getString("home").split(" ");
                                    String world = home[0];
                                    String x = home[1];
                                    String y = home[2];
                                    String z = home[3];
                                    player.teleport(new Location(Bukkit.getWorld(world), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)));
                                    player.sendMessage(ChatColor.GREEN+"Вы были успешно телепортированы!");
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.RED+"Точка дома не установлена!");
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        }
                        return false;
                    case "invest":
                        if (args.length == 2) {
                            double amount;
                            try {
                                amount = Double.parseDouble(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Недопустимая сумма.");
                                return false;
                            }
                            String clanName = Utils.getPlayerClan(player.getName());
                            if (clanName != null) {
                                if (amount > 0 && amount <= Vault.getEconomy().getBalance(player)) {
                                    ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans").getConfigurationSection(clanName);
                                    if (clanSection != null) {
                                        double currentMoney = clanSection.getDouble("money");
                                        clanSection.set("money", currentMoney + amount);
                                        QDClans.instance.saveConfig();
                                        Utils.updateClan(clanName);
                                        Vault.getEconomy().withdrawPlayer(player, amount);
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_investment").replace("%player%", player.getName()).replace("%amount%", String.valueOf(amount))));
                                        return true;
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Клан не найден.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Недостаточно средств или неверная сумма.");
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Укажите сумму для инвестирования!");
                        }
                        return false;
                    case "delhome":
                        if (args.length == 1) {
                            String clanName = Utils.getPlayerClan(player.getName());

                            if (clanName != null) {
                                ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans").getConfigurationSection(clanName);
                                if (clanSection != null && clanSection.get("home") != null) {
                                    if (Objects.equals(clanSection.getString("leader"), player.getName())) {
                                        clanSection.set("home", null);
                                        QDClans.instance.saveConfig();
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.home_deleted")));
                                        return true;
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.not_clan_leader")));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED+ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.home_delete_error")));
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        }
                        return false;
                    case "money":
                        if (args.length == 1) {
                            String clanName = Utils.getPlayerClan(player.getName());
                            ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);

                            if (clanName != null && clanSection != null) {
                                double clanBalance = clanSection.getDouble("money");
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_balance_display").replace("%clan_balance%", String.valueOf(clanBalance))));
                                return true;
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                            return false;
                        }
                    case "pvp":
                        if (args.length == 1) {
                            String clanName = Utils.getPlayerClan(player.getName());
                            ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);

                            if (clanName != null && clanSection != null) {
                                if (Objects.equals(clanSection.getString("leader"), player.getName())) {
                                    boolean pvpEnabled = clanSection.getBoolean("pvp-enabled");
                                    clanSection.set("pvp-enabled", !pvpEnabled);
                                    QDClans.instance.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString(pvpEnabled ? "messages.clan_pvp_disabled" : "messages.clan_pvp_enabled")));
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.not_clan_leader")));
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        }
                        return false;
                    case "leave":
                        if (args.length == 1) {
                            String clanName = Utils.getPlayerClan(player.getName());
                            ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);

                            if (clanName != null && clanSection != null) {
                                List<String> players = clanSection.getStringList("players");
                                players.remove(player.getName());
                                clanSection.set("players", players);
                                QDClans.instance.saveConfig();
                                List<Player> onlinePlayers = new ArrayList<>();
                                for (String p : players) {
                                    if (Bukkit.getPlayerExact(p)!=null) {
                                        onlinePlayers.add(Bukkit.getPlayerExact(p));
                                    }
                                }
                                if (!onlinePlayers.isEmpty()) {
                                    String message = Utils.getExitNotification(player.getName());
                                    onlinePlayers.forEach(plr -> plr.sendMessage(message));
                                }
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_exit_success").replace("%clan_name%", clanName)));
                                QDClans.glower.stopGlowing(player);
                                return true;
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Убедитесь, что у вас нет лишних аргументов.");
                        }
                        return false;

                    case "kick":
                        if (args.length == 2) {
                            String clanName = Utils.getPlayerClan(player.getName());
                            String playerToKick = args[1];
                            ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);

                            if (clanName != null && clanSection != null) {
                                if (Objects.equals(clanSection.getString("leader"), player.getName())) {
                                    List<String> players = clanSection.getStringList("players");
                                    if (players.contains(playerToKick)) {
                                        if (!playerToKick.equals(player.getName())) {
                                            Player p = Bukkit.getPlayerExact(playerToKick);
                                            if (p != null) {
                                                QDClans.glower.stopGlowing(p);
                                            }
                                            players.remove(playerToKick);
                                            clanSection.set("players", players);
                                            QDClans.instance.saveConfig();

                                            Player kickedPlayer = Bukkit.getPlayerExact(playerToKick);
                                            if (kickedPlayer != null) {
                                                kickedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.player_kicked").replace("%player%", playerToKick).replace("%clan_name%", clanName)));
                                            }
                                            List<Player> onlinePlayers = new ArrayList<>();
                                            for (String p_ : players) {
                                                if (Bukkit.getPlayerExact(p_) != null) {
                                                    onlinePlayers.add(Bukkit.getPlayerExact(p_));
                                                }
                                            }
                                            if (!onlinePlayers.isEmpty()) {
                                                String message = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.player_kicked").replace("%player%", playerToKick).replace("%clan_name%", clanName));
                                                onlinePlayers.forEach(plr -> plr.sendMessage(message));
                                            }
                                            return true;
                                        } else {
                                            player.sendMessage(ChatColor.RED+"Вы не можете кикнуть сами себя!");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.invite_error_member_exists").replace("%player%", playerToKick)));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.not_clan_leader")));
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Укажите игрока, которого хотите исключить из клана.");
                        }
                        return false;

                    case "confirmdisband":
                        if (args.length == 2) {
                            String clanName = args[1];
                            String playerClan = Utils.getPlayerClan(player.getName());

                            if (playerClan != null && playerClan.equals(clanName)) {
                                ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);
                                if (clanSection != null) {
                                    List<String> players = clanSection.getStringList("players");
                                    QDClans.instance.getConfig().set("clans." + clanName, null);
                                    QDClans.instance.getConfig().set("storageData." + clanName, null);
                                    QDClans.instance.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_dissolved_successfully")));
                                    List<Player> onlinePlayers = new ArrayList<>();
                                    for (String p : players) {
                                        if (Bukkit.getPlayerExact(p)!=null) {
                                            onlinePlayers.add(Bukkit.getPlayerExact(p));
                                        }
                                    }
                                    if (!onlinePlayers.isEmpty()) {
                                        String message = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_dissolved_by_leader"));
                                        onlinePlayers.forEach(plr -> plr.sendMessage(message));
                                        onlinePlayers.forEach(plr -> QDClans.glower.stopGlowing(plr));
                                    }
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        }
                        return false;
                    case "disband":
                        if (args.length == 1) {
                            String clanName = Utils.getPlayerClan(player.getName());
                            ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);

                            if (clanName != null && clanSection != null) {
                                if (Objects.equals(clanSection.getString("leader"), player.getName())) {
                                    TextComponent disbandMessage = new TextComponent(ChatColor.GOLD+"Вы действительно хотите удалить этот клан?");
                                    TextComponent confirmLink = new TextComponent("[Подтвердить удаление]");
                                    confirmLink.setColor(net.md_5.bungee.api.ChatColor.RED);
                                    confirmLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan confirmdisband " + clanName));

                                    disbandMessage.addExtra(confirmLink);

                                    player.spigot().sendMessage(disbandMessage);
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.not_clan_leader")));
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        }
                        return false;
                    case "join":
                        if (args.length == 2) {
                            String targetClan = args[1];
                            if (Utils.getPlayerClan(player.getName()) == null) {
                                if (Utils.clanAlreadyExist(targetClan)) {
                                    ConfigurationSection targetClanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + targetClan);
                                    if (targetClanSection != null) {
                                        List<String> players = targetClanSection.getStringList("players");
                                        if (players.size() < targetClanSection.getInt("max-players")) {
                                            players.add(player.getName());
                                            targetClanSection.set("players", players);
                                            QDClans.instance.saveConfig();
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.invite_accepted")));
                                            List<Player> onlinePlayers = new ArrayList<>();
                                            for (String p : players) {
                                                if (Bukkit.getPlayerExact(p)!=null) {
                                                    onlinePlayers.add(Bukkit.getPlayerExact(p));
                                                }
                                            }
                                            if (!onlinePlayers.isEmpty()) {
                                                String message = ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.player_joined_clan").replace("%player%", player.getName()));
                                                onlinePlayers.forEach(plr -> plr.sendMessage(message));
                                            }
                                            Utils.updateClan(targetClan);
                                            QDClans.glower.glowPlayer(player, targetClanSection.getString("glowing-color"));
                                            return true;
                                        } else {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.invite_error_max_members_reached").replace("%clan_name%", targetClan)));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Клан не найден.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Такого клана не существует.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Вы уже состоите в клане.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Укажите название клана для вступления.");
                        }
                        return false;
                    case "withdraw":
                        if (args.length == 2) {
                            double amount;
                            try {
                                amount = Double.parseDouble(args[1]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Недопустимая сумма.");
                                return false;
                            }
                            String clanName = Utils.getPlayerClan(player.getName());
                            if (clanName != null) {
                                ConfigurationSection clanSection = QDClans.instance.getConfig().getConfigurationSection("clans." + clanName);
                                if (clanSection != null) {
                                    double clanMoney = clanSection.getDouble("money");
                                    if (amount > 0 && amount <= clanMoney) {
                                        if (Objects.equals(clanSection.getString("leader"), player.getName())) {
                                            clanSection.set("money", clanMoney - amount);
                                            QDClans.instance.saveConfig();
                                            Vault.getEconomy().depositPlayer(player, amount);
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.clan_withdrawal")
                                                    .replace("%player%", player.getName()).replace("%amount%", String.valueOf(amount))));
                                            return true;
                                        } else {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.not_clan_leader")));
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Недостаточно средств в казне клана.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Клан не найден.");
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', QDClans.instance.getConfig().getString("messages.no_clan_membership")));
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Укажите сумму для вывода!");
                        }
                        return false;
                    default:
                        player.sendMessage(ChatColor.RED + "Unknown command.");
                        break;
                }
            }
        } else {
            commandSender.sendMessage("Вы не являетесь игроком для выполнения данной команды!");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("clan") && strings.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("create");
            completions.add("money");
            completions.add("menu");
            completions.add("invest");
            completions.add("disband");
            completions.add("invite");
            completions.add("kick");
            completions.add("leave");
            completions.add("pvp");
            completions.add("join");
            completions.add("withdraw");
            completions.add("delhome");
            completions.add("home");
            completions.add("sethome");
            return completions;
        } else {
            return null;
        }
    }
}
