package main.java.me.avankziar.ccs.spigot.cmd.ccs;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import main.java.me.avankziar.ccs.general.ChatApi;
import main.java.me.avankziar.ccs.spigot.CCS;
import main.java.me.avankziar.ccs.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.ccs.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.ccs.spigot.database.MysqlHandler;

public class ARGRemove extends ArgumentModule
{
	private CCS plugin;
	
	public ARGRemove(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = CCS.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		String condition = args[1];
		String othername = args[2];
		String reason = "/";
		if(args.length >= 4)
		{
			reason = args[3];
		}
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
		int count = 0;
		if(reason.equals("/"))
		{
			count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ?", uuid.toString(), condition);
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ?", uuid.toString(), condition);
		} else
		{
			count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `intern_reason` = ?", uuid.toString(), condition, reason);
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `intern_reason` = ?", uuid.toString(), condition, reason);
		}
		sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdRemove.Remove")
				.replace("%c%", condition)
				.replace("%player%", othername)
				.replace("%reason%", reason)
				.replace("%count%", String.valueOf(count))
				));
		return;
	}
}