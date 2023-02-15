package main.java.me.avankziar.ccs.spigot.assistance;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.ccs.spigot.CCS;
import main.java.me.avankziar.ccs.spigot.database.MysqlHandler;

public class BackgroundTask
{
	private static CCS plugin;
	
	public BackgroundTask(CCS plugin)
	{
		BackgroundTask.plugin = plugin;
		initUpdateTask();
	}
	
	public void initUpdateTask()
	{
		int mulp = plugin.getYamlHandler().getConfig().getInt("DeleteOldDataTask.RunInSeconds", 60);
		if(mulp <= 0)
		{
			mulp = 60;
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Player player : Bukkit.getOnlinePlayers())
				{
					long now = System.currentTimeMillis();
					UUID uuid = player.getUniqueId();
					int c = plugin.getMysqlHandler().getCount(MysqlHandler.Type.CONDITIONVALUE,
							"`player_uuid` = ? AND `duration` > ? AND duration < ?",
							uuid.toString(), 0, now);
					if(c > 0)
					{
						plugin.getMysqlHandler().deleteData(MysqlHandler.Type.CONDITIONVALUE,
								"`player_uuid` = ? AND `duration` > ? AND duration < ?",
								uuid.toString(), 0, now);
					}					
				}
			}
		}.runTaskTimerAsynchronously(plugin, 20L, 20L*mulp);
	}
}
