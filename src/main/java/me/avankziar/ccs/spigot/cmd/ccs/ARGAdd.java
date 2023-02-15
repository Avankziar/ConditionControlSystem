package main.java.me.avankziar.ccs.spigot.cmd.ccs;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import main.java.me.avankziar.ccs.general.ChatApi;
import main.java.me.avankziar.ccs.spigot.CCS;
import main.java.me.avankziar.ccs.spigot.assistance.MatchApi;
import main.java.me.avankziar.ccs.spigot.assistance.TimeHandler;
import main.java.me.avankziar.ccs.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.ccs.spigot.cmdtree.ArgumentModule;
import net.md_5.bungee.api.chat.ClickEvent;

public class ARGAdd extends ArgumentModule
{
	private CCS plugin;
	
	public ARGAdd(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = CCS.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		String condition = args[1];
		String othername = args[2];
		String type = args[3];
		String value = args[4];
		String dur = args[5];
		String internReason = args[6];
		long duration = -1;
		String reason = "";
		if(!plugin.getCondition().isRegistered(condition))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAdd.IsNotRegistered")));
			return;
		}
		OfflinePlayer other = Bukkit.getPlayer(othername);
		if(other == null || !other.hasPlayedBefore())
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		final UUID uuid = other.getUniqueId();
		String server = null;
		String world = null;
		if(type.startsWith("server"))
		{
			String[] sp = type.split(":");
			if(sp.length != 2)
			{
				sender.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
						ClickEvent.Action.RUN_COMMAND, CCS.infoCommand));
				return;
			}
			server = sp[1];
		} else if(type.startsWith("world"))
		{
			String[] sp = type.split(":");
			if(sp.length != 3)
			{
				sender.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
						ClickEvent.Action.RUN_COMMAND, CCS.infoCommand));
				return;
			}
			server = sp[1];
			world = sp[2];
		} else
		{
			type = "global";
		}
		if(MatchApi.isLong(dur))
		{
			duration = Long.parseLong(dur);
		} else
		{
			duration = TimeHandler.getRepeatingTimeShort(dur);
		}
		if(duration == 0)
		{
			duration = -1;
		}
		for (int i = 7; i < args.length; i++) 
        {
			reason += args[i];
			if(i < (args.length-1))
			{
				reason += " ";
			}
        }
		if(reason.isBlank())
		{
			reason = "/";
		}
		plugin.getCondition().addConditionEntry(uuid, condition, value, internReason, reason, server, world, duration);
		if(duration < 0)
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAdd.AddedPermanent")
					.replace("%c%", condition)
					.replace("%player%", othername)
					.replace("%type%", type)
					.replace("%value%", value)
					.replace("%internreason%", internReason)
					.replace("%reason%", reason)
					));
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAdd.AddedTemporary")
					.replace("%c%", condition)
					.replace("%player%", othername)
					.replace("%type%", type)
					.replace("%value%", value)
					.replace("%duration%", TimeHandler.getRepeatingTime(duration, "dd-HH:mm"))
					.replace("%internreason%", internReason)
					.replace("%reason%", reason)
					));
		}
		return;
	}
}
