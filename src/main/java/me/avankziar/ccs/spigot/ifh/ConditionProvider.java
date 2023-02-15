package main.java.me.avankziar.ccs.spigot.ifh;

import java.util.ArrayList;
import java.util.UUID;

import main.java.me.avankziar.ccs.spigot.CCS;
import main.java.me.avankziar.ccs.spigot.database.MysqlHandler;
import main.java.me.avankziar.ccs.spigot.objects.Condition;
import main.java.me.avankziar.ccs.spigot.objects.ConditionValue;

public class ConditionProvider implements main.java.me.avankziar.ifh.general.condition.Condition
{
	private CCS plugin;
	private static ArrayList<Condition> registeredC = new ArrayList<>();
	
	public ConditionProvider()
	{
		this.plugin = CCS.getPlugin();
		if(registeredC.isEmpty())
		{
			init();
		}
	}
	
	public void init()
	{
		ArrayList<Condition> clist = Condition.convert(plugin.getMysqlHandler()
				.getFullList(MysqlHandler.Type.CONDITION, "`id`", "1"));
		registeredC.addAll(clist);
		CCS.log.info(clist.size()+" Bonus/Malus are registered!");
	}
	
	public boolean isRegistered(String conditionName)
	{
		for(Condition c : registeredC)
		{
			if(c.getConditionName().equals(conditionName))
			{
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Condition> getRegisteredC()
	{
		return registeredC;
	}
	
	/**
	 * Register a condition.
	 * @param conditionName
	 * @param displayconditionName
	 * @param conditionExplanation
	 * @return
	 */
	public boolean register(String conditionName, String displayConditionName,
			String...conditionExplanation)
	{
		if(isRegistered(conditionName))
		{
			return false;
		}
		if(conditionName == null || displayConditionName == null)
		{
			return false;
		}
		Condition c = new Condition(conditionName, displayConditionName,
				conditionExplanation);
		plugin.getMysqlHandler().create(MysqlHandler.Type.CONDITION, c);
		registeredC.add(c);
		return true;
	}
	
	/**
	 * Return a list of all registered condition.
	 * @return
	 */
	public ArrayList<String> getRegistered()
	{
		ArrayList<String> list = new ArrayList<>();
		registeredC.stream().forEach(x -> list.add(x.getConditionName()));
		return list;
	}
	
	/**
	 * Return the displayname of the condition.
	 * @param conditionName
	 * @return
	 */
	public String getRegisteredDisplayName(String conditionName)
	{
		String display = null;
		for(Condition c : registeredC)
		{
			if(c.getConditionName().equals(conditionName))
			{
				display = c.getDisplayConditionName();
				break;
			}
		}
		return display;
	}
	
	/**
	 * Return the explanation of the condition if it exist.
	 * @param conditionName
	 * @return
	 */
	public String[] getRegisteredExplanation(String conditionName)
	{
		String[] ar = null;
		for(Condition c : registeredC)
		{
			if(c.getConditionName().equals(conditionName))
			{
				ar = c.getExplanation().toArray(new String[c.getExplanation().size()]);
				break;
			}
		}
		return ar;
	}
	
	public void remove(UUID uuid)
	{
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.CONDITIONVALUE,
				"`player_uuid` = ?", uuid.toString());
	}
	
	public void remove(UUID uuid, String reason)
	{
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.CONDITIONVALUE,
				"`player_uuid` = ? AND `intern_reason` = ?", uuid.toString(), reason);
	}
	
	public void remove(UUID uuid, String conditionName, String internReason)
	{
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.CONDITIONVALUE,
				"`player_uuid` = ? AND `condition_name` = ? AND `intern_reason` = ?", uuid.toString(), conditionName, internReason);
	}
	
	public void remove(String internReason)
	{
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.CONDITIONVALUE,
				"`intern_reason` = ?", internReason);
	}
	
	public void remove(String conditionName, String internReason)
	{
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.CONDITIONVALUE,
				"`condition_name` = ? AND `intern_reason` = ?", conditionName, internReason);
	}
	
	public boolean hasConditionEntry(UUID uuid, String conditionName)
	{
		return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
				"`player_uuid` = ? AND `condition_name` = ?", uuid.toString(), conditionName);
	}
	
	public boolean hasConditionEntry(UUID uuid, String conditionName, String internreason)
	{
		if(internreason != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `intern_reason` = ?",
					uuid.toString(), conditionName, internreason);
		}
		return hasConditionEntry(uuid, conditionName);
	}
	
	public boolean hasConditionEntry(UUID uuid, String conditionName, String server, String world)
	{
		if(server != null && world == null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `server` = ?"
					, uuid.toString(), conditionName, server);
		} else if(server == null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `world` = ?"
					, uuid.toString(), conditionName, world);
		} else if(server != null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), conditionName, server, world);
		}
		return hasConditionEntry(uuid, conditionName);
	}
	
	public boolean hasConditionEntry(UUID uuid, String conditionName, String internReason, String server, String world)
	{
		if(internReason != null && server != null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `intern_reason` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), conditionName, internReason, server, world);
		} else if(internReason != null && server == null && world == null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `intern_reason` = ?"
					, uuid.toString(), conditionName, internReason);
		} else if(internReason == null && server != null && world == null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `server` = ?"
					, uuid.toString(), conditionName, server);
		} else if(internReason == null && server == null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `world` = ?"
					, uuid.toString(), conditionName, world);
		} else if(internReason != null && server != null && world == null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `intern_reason` = ? AND `server` = ?"
					, uuid.toString(), conditionName, internReason, server);
		} else if(internReason != null && server == null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `intern_reason` = ? AND `world` = ?"
					, uuid.toString(), conditionName, internReason, world);
		} else if(internReason == null && server != null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), conditionName, server, world);
		}
	return hasConditionEntry(uuid, conditionName);
	}
	
	public String[] getConditionEntry(UUID uuid, String conditionName)
	{
		return getConditionEntry(uuid, conditionName, null, null);
	}
	
	public String[] getConditionEntry(UUID uuid, String conditionName, String server, String world)
	{
		if(!isRegistered(conditionName)  || !hasConditionEntry(uuid, conditionName, server, world))
		{
			return null;
		}
		ArrayList<String> list = new ArrayList<>();
		ArrayList<ConditionValue> cv = null;
		if(server != null && world == null)
		{
			cv = ConditionValue.convert(plugin.getMysqlHandler().getFullList(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `server` = ?"
					, uuid.toString(), conditionName, server));
		} else if(server == null && world != null)
		{
			cv = ConditionValue.convert(plugin.getMysqlHandler().getFullList(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `world` = ?"
					, uuid.toString(), conditionName, world));
		} else if(server != null && world != null)
		{
			cv = ConditionValue.convert(plugin.getMysqlHandler().getFullList(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), conditionName, server, world));
		} else
		{
			cv = ConditionValue.convert(plugin.getMysqlHandler().getFullList(MysqlHandler.Type.CONDITIONVALUE,
					"`player_uuid` = ? AND `condition_name` = ?"
					, uuid.toString(), conditionName));
		}
		for(ConditionValue c : cv)
		{
			list.add(c.getValue());
		}
		return list.toArray(new String[list.size()]);
	}
	
	public void addConditionEntry(UUID uuid, String conditionName,
			String value,
			String internReason, String displayReason,
			String server, String world,
			Long duration)
	{
		ConditionValue cv = new ConditionValue(0, uuid, conditionName, value,
				internReason, displayReason,
				server, world,
				duration != null ? (duration.longValue() > 0 ? duration.longValue()+System.currentTimeMillis() : -1) : -1);
		plugin.getMysqlHandler().create(MysqlHandler.Type.CONDITIONVALUE, cv);
	}
}