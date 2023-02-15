package main.java.me.avankziar.ccs.spigot.cmd.ccs;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.ccs.general.ChatApi;
import main.java.me.avankziar.ccs.spigot.CCS;
import main.java.me.avankziar.ccs.spigot.assistance.MatchApi;
import main.java.me.avankziar.ccs.spigot.assistance.TimeHandler;
import main.java.me.avankziar.ccs.spigot.cmd.CCSCommandExecutor;
import main.java.me.avankziar.ccs.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.ccs.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.ccs.spigot.conditionbonusmalus.Bypass;
import main.java.me.avankziar.ccs.spigot.conditionbonusmalus.Bypass.Permission;
import main.java.me.avankziar.ccs.spigot.database.MysqlHandler;
import main.java.me.avankziar.ccs.spigot.objects.Condition;
import main.java.me.avankziar.ccs.spigot.objects.ConditionValue;
import main.java.me.avankziar.ifh.general.math.MatchPrimitiveDataTypes;
import main.java.me.avankziar.ifh.general.math.Mathematic;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGEntry extends ArgumentModule
{
	private CCS plugin;
	private HashMap<String, Long> cooldown = new HashMap<>();
	private ArgumentConstructor ac = null;
	
	public ARGEntry(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = CCS.getPlugin();
		this.ac = argumentConstructor;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		if(cooldown.containsKey(player.getName()))
		{
			if(cooldown.get(player.getName()) > System.currentTimeMillis())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerCmdCooldown")));
				return;
			}
		}
		cooldown.put(player.getName(), System.currentTimeMillis()+1000L*10);
		int page = 0;
		if(args.length >= 2 && MatchApi.isInteger(args[1]))
		{
			page = Integer.parseInt(args[1]);
		}
		String othername = player.getName();
		if(args.length >= 3)
		{
			if(!player.hasPermission(Bypass.get(Permission.OTHERPLAYER)))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return;
			}
			othername = args[2];
		}
		Player other = Bukkit.getPlayer(othername);
		if(other == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		final UUID uuid = other.getUniqueId();
		String type = "global";
		String server = null;
		String world = null;
		if(args.length >= 3)
		{
			type = args[2];
			switch(type)
			{
			default:
			case "global":
				break;
			case "server":
				server = plugin.getAdministration() != null ? plugin.getAdministration().getSpigotServerName() 
						: plugin.getYamlHandler().getConfig().getString("ServerName");
				break;
			case "world":
				server = plugin.getAdministration() != null ? plugin.getAdministration().getSpigotServerName() 
						: plugin.getYamlHandler().getConfig().getString("ServerName");
				world = other.getWorld().getName();
				break;
			}
		}
		ArrayList<Condition> rg = plugin.getCondition().getRegisteredC();
		LinkedHashMap<Condition, String[]> map = new LinkedHashMap<>();
		int end = page * 10 + 9;
		for(int i = page * 10; i < rg.size(); i++)
		{
			Condition c = rg.get(i);
			if(!plugin.getCondition().hasConditionEntry(uuid, c.getConditionName(), server, world))
			{
				continue;
			}
			map.put(c, plugin.getCondition().getConditionEntry(uuid, c.getConditionName(), server, world));
			if(i >= end)
			{
				break;
			}
		}
		boolean lastpage = rg.size()-9 < page * 10;
		if(map.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerHasNoCondition")
					.replace("%player%", othername)));
			return;
		}
		ArrayList<ArrayList<BaseComponent>> bc = new ArrayList<>();
		ArrayList<BaseComponent> bc1 = new ArrayList<>();
		bc1.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdEntry.Headline")
				.replace("%player%", othername)
				.replace("%page%", String.valueOf(page))));
		bc.add(bc1);
		for(Entry<Condition, String[]> cme : map.entrySet())
		{
			Condition c = cme.getKey();
			ArrayList<BaseComponent> bc3 = new ArrayList<>();
			bc3.add(ChatApi.hoverEvent(plugin.getYamlHandler().getLang().getString("CmdEntry.ConditionDescriptionOne")
					.replace("%displayname%", c.getDisplayConditionName()),
					HoverEvent.Action.SHOW_TEXT, String.join("~!~", c.getExplanation())));
			ArrayList<ConditionValue> cvv = ConditionValue.convert(
					plugin.getMysqlHandler().getFullList(MysqlHandler.Type.CONDITIONVALUE, "`id` ASC",
					"`player_uuid` = ? AND `condition_name` = ?", uuid.toString(), c.getConditionName()));
			ArrayList<String> vlist = new ArrayList<>();
			vlist.add(plugin.getYamlHandler().getLang().getString("CmdEntry.BaseValue"));
			for(ConditionValue cv : cvv)
			{
				String v = cv.getValue();
				StringBuilder sb = new StringBuilder();
				if(MatchPrimitiveDataTypes.isBoolean(v))
				{
					boolean boo = MatchPrimitiveDataTypes.getBoolean(v).booleanValue();
					sb.append(boo
							? "'"+plugin.getYamlHandler().getLang().getString("IsTrue")+"'"
							: "'"+plugin.getYamlHandler().getLang().getString("IsFalse")+"'");
				} else if(MatchPrimitiveDataTypes.isLong(v))
				{
					sb.append("'"+Long.parseLong(v)+"'");
				} else if(MatchPrimitiveDataTypes.isDouble(v))
				{
					sb.append("'"+Mathematic.round(Double.parseDouble(v), 2, RoundingMode.DOWN)+"'");
				}
				sb.append(" >> '"+cv.getDisplayReason()+"'");
				if(cv.getDuration() > 0)
				{
					long dur = cv.getDuration()-System.currentTimeMillis();
					sb.append("&r >> " + TimeHandler.getRepeatingTime(dur, "dd-HH:mm"));
				}
				vlist.add(sb.toString());
			}
			bc3.add(ChatApi.hoverEvent(plugin.getYamlHandler().getLang().getString("CmdEntry.ConditionDescriptionTwo")
					.replace("%value%", String.valueOf(cme.getValue().length)),
					HoverEvent.Action.SHOW_TEXT, String.join("~!~", vlist)));
			bc.add(bc3);
		}
		for(ArrayList<BaseComponent> b : bc)
		{
			TextComponent tc = ChatApi.tc("");
			tc.setExtra(b);
			player.spigot().sendMessage(tc);
		}
		CCSCommandExecutor.pastNextPage(player, page, lastpage, ac.getCommandString(), othername, type);
	}
}