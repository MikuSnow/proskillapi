/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdForceReset
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.cmd;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import mc.promcteam.engine.mccore.commands.ConfigurableCommand;
import mc.promcteam.engine.mccore.commands.IFunction;
import mc.promcteam.engine.mccore.config.Filter;
import mc.promcteam.engine.mccore.config.parse.NumberParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command to clear all bound skills
 */
public class CmdForceReset implements IFunction {
    private static final String NOT_PLAYER  = "not-player";
    private static final String NOT_ACCOUNT = "not-account";
    private static final String RESET       = "account-reset";
    private static final String TARGET      = "target-notice";

    /**
     * Executes the command
     *
     * @param command owning command
     * @param plugin  plugin reference
     * @param sender  sender of the command
     * @param args    arguments
     */
    @Override
    public void execute(ConfigurableCommand command, Plugin plugin, CommandSender sender, String[] args) {
        // Needs two arguments
        if (args.length == 0) {
            command.displayHelp(sender);
        }

        // Switch accounts if valid number
        else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

            if (player == null) {
                command.sendMessage(sender, NOT_PLAYER, "&4That is not a valid player name");
                return;
            }

            PlayerAccounts accounts = SkillAPI.getPlayerAccountData(player);
            int            id       = accounts.getActiveId();

            if (args.length > 1)
                id = NumberParser.parseInt(args[1]);

            if (accounts.getAccountLimit() >= id && id > 0) {
                accounts.setAccount(id);
                accounts.getActiveData().resetAll();
                command.sendMessage(sender, RESET, ChatColor.GOLD + "{player}'s" + ChatColor.DARK_GREEN + " active account has been reset", Filter.PLAYER.setReplacement(player.getName()));
                if (player.isOnline()) {
                    command.sendMessage((Player) player, TARGET, ChatColor.DARK_GREEN + "Your class data has been reset");
                }
                return;
            }

            command.sendMessage(sender, NOT_ACCOUNT, ChatColor.RED + "That is not a valid account ID");
        }
    }
}