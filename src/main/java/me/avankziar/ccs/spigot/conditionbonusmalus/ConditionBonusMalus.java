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
			String t = BaseConstructor.getPlugin().getCondition().getConditionEntry(
					player.getUniqueId(),
					bc.getConditionPath(),
					BaseConstructor.getPlugin().getServername(),
					player.getWorld().getName());
			if(MatchApi.isBoolean(t)
					&& MatchApi.getBoolean(t).booleanValue() && player.hasPermission(bc.getPermission()))
			{
				return true;
			}
			return false;
		}
		return player.hasPermission(bc.getPermission());
	}
	
	public static boolean hasPermission(Player player, Bypass.Permission bypassPermission)
	{
		if(BaseConstructor.getPlugin().getCondition() != null)
		{
			String t = BaseConstructor.getPlugin().getCondition().getConditionEntry(
					player.getUniqueId(),
					bypassPermission.getCondition(),
					BaseConstructor.getPlugin().getServername(),
					player.getWorld().getName());
			if(MatchApi.isBoolean(t)
					&& MatchApi.getBoolean(t).booleanValue() && player.hasPermission(Bypass.get(bypassPermission)))
			{
				return true;
			}
			return false;
		}
		return player.hasPermission(Bypass.get(bypassPermission));
	}
}	