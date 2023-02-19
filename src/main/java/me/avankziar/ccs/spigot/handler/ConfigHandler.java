package main.java.me.avankziar.ccs.spigot.handler;

import java.util.List;

import main.java.me.avankziar.ccs.spigot.cmdtree.BaseConstructor;

public class ConfigHandler
{	
	public ConfigHandler(){}
	
	public enum CountType
	{
		HIGHEST, ADDUP;
	}
	
	public CountType getCountPermType()
	{
		String s = BaseConstructor.getPlugin().getYamlHandler().getConfig().getString("Mechanic.CountPerm", "HIGHEST");
		CountType ct;
		try
		{
			ct = CountType.valueOf(s);
		} catch (Exception e)
		{
			ct = CountType.HIGHEST;
		}
		return ct;
	}
	
	public boolean isMechanicBonusMalusEnabled()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("EnableMechanic.BonusMalus", false);
	}
	
	public List<String> getBottleTerm()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getStringList("ExpBottle.BottleTerm");
	}
	
	public List<String> getLevelTerm()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getStringList("ExpBottle.LevelTerm");
	}
}