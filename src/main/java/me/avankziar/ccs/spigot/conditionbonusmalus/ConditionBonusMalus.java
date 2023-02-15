package main.java.me.avankziar.ccs.spigot.conditionbonusmalus;

import org.bukkit.entity.Player;

import main.java.me.avankziar.ccs.spigot.assistance.MatchApi;
import main.java.me.avankziar.ccs.spigot.cmdtree.BaseConstructor;

public class ConditionBonusMalus
{
	public static boolean hasPermission(Player player, BaseConstructor bc)
	{
		if(BaseConstructor.getPlugin().getCondition() != null)
		{
			String[] ss = BaseConstructor.getPlugin().getCondition().getConditionEntry(
					player.getUniqueId(),
					bc.getConditionPath(),
					BaseConstructor.getPlugin().getServername(),
					player.getWorld().getName());
			if(ss == null)
			{
				if(BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("Condition.ConditionOverrulePermission", false))
				{
					return false;
				} else
				{
					return player.hasPermission(bc.getPermission());
				}
			}
			int t = 0;
			int f = 0;
			for(String s : ss)
			{
				if(MatchApi.isBoolean(s))
				{
					if(MatchApi.getBoolean(s).booleanValue())
					{
						t++;
					} else
					{
						f++;
					}
				}
			}
			if(BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("Condition.ConditionOverrulePermission", false))
			{
				if((t > 0 && t > f))
				{
					return true;
				}
			} else
			{
				if((t > 0 && t > f) || player.hasPermission(bc.getPermission()))
				{
					return true;
				}
			}
			return false;
		}
		return player.hasPermission(bc.getPermission());
	}
	
	public static boolean hasPermission(Player player, Bypass.Permission bypassPermission)
	{
		if(BaseConstructor.getPlugin().getCondition() != null)
		{
			String[] ss = BaseConstructor.getPlugin().getCondition().getConditionEntry(
					player.getUniqueId(),
					bypassPermission.getCondition(),
					BaseConstructor.getPlugin().getServername(),
					player.getWorld().getName());
			if(ss == null)
			{
				if(BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("Condition.ConditionOverrulePermission", false))
				{
					return false;
				} else
				{
					return player.hasPermission(Bypass.get(bypassPermission));
				}
			}
			int t = 0;
			int f = 0;
			for(String s : ss)
			{
				if(MatchApi.isBoolean(s))
				{
					if(MatchApi.getBoolean(s).booleanValue())
					{
						t++;
					} else
					{
						f++;
					}
				}
			}
			if(BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("Condition.ConditionOverrulePermission", false))
			{
				if((t > 0 && t > f))
				{
					return true;
				}
			} else
			{
				if((t > 0 && t > f) || player.hasPermission(Bypass.get(bypassPermission)))
				{
					return true;
				}
			}
			return false;
		}
		return player.hasPermission(Bypass.get(bypassPermission));
	}
}	