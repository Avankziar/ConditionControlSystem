package main.java.me.avankziar.ccs.spigot.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import main.java.me.avankziar.ccs.spigot.conditionbonusmalus.Bypass;
import main.java.me.avankziar.ccs.spigot.database.Language.ISO639_2B;

public class YamlManager
{
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configSpigotKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> cbmlanguageKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename muss be predefine. For example in the config.
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> guisKeys = new LinkedHashMap<>();
	
	public YamlManager()
	{
		initConfig();
		initCommands();
		initConditionBonusMalusLanguage();
		initLanguage();
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigSpigotKey()
	{
		return configSpigotKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, Language> getConditionBonusMalusLanguageKey()
	{
		return cbmlanguageKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Language>> getGUIKey()
	{
		return guisKeys;
	}
	
	/*
	 * The main methode to set all paths in the yamls.
	 */
	public void setFileInput(YamlConfiguration yml, LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public void initConfig() //INFO:Config
	{
		configSpigotKeys.put("useIFHAdministration"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("IFHAdministrationPath"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ccs"}));
		
		configSpigotKeys.put("ServerName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"hub"}));
		configSpigotKeys.put("Mysql.Status"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.Host"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"127.0.0.1"}));
		configSpigotKeys.put("Mysql.Port"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				3306}));
		configSpigotKeys.put("Mysql.DatabaseName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"mydatabase"}));
		configSpigotKeys.put("Mysql.SSLEnabled"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.AutoReconnect"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("Mysql.VerifyServerCertificate"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.User"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"admin"}));
		configSpigotKeys.put("Mysql.Password"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"not_0123456789"}));
		
		configSpigotKeys.put("Condition.ConditionOverrulePermission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("DeleteOldDataTask.RunInSeconds"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				60}));;
	}
	
	//INFO:Commands
	public void initCommands()
	{
		comBypass();
		String path = "ccs";
		commandsInput(path, "bmc", "ccs.cmd.ccs", 
				"/ccs [page]", "/ccs ", false,
				"&c/ccs [Seite] &f| Infoseite für alle Befehle.",
				"&c/ccs [page] &f| Info page for all commands.",
				"&bBefehlsrecht für &f/ccs",
				"&bCommandright for &f/ccs",
				"&eInfoseite für alle Befehle.",
				"&eInfo page for all commands.");
		String perm = "ccs.cmd";		
		argumentInput(path+"_add", "add", perm,
				"/ccs add <condition> <player> <global/server:servername/world:servername:worldname> <value> <0/dd-HH:mm> <internreason> <reason...>", "/ccs add ", false,
				"&c/ccs add <Condition> <Spieler> <global/server:servername/world:servername:weltname> <Wert> <0/dd-HH:mm> <interner Grung> <Grund...> &f| Fügt dem angegeben Spieler eine Condition hinzu.",
				"&c/ccs add <condition> <player> <global/server:servername/world:servername:worldname> <value> <0/dd-HH:mm> <internreason> <reason...> &f| Adds a condition to the specified player.",
				"&bBefehlsrecht für &f/ccs add",
				"&bCommandright for &f/ccs add",
				"&eFügt dem angegeben Spieler eine Condition hinzu.",
				"&eAdds a condition to the specified player.");
		argumentInput(path+"_entry", "entry", perm,
				"/ccs entry [page] [playername] [global/server/world]", "/ccs entry ", false,
				"&c/ccs entry [Seite] [Spielername] [global/server/world] &f| Listet alle aktive Condition des Spielers mit Hovererklärung auf.",
				"&c/ccs entry [page] [playername] [global/server/world] &f| Lists all active conditions of the player with hoverexplanation.",
				"&bBefehlsrecht für &f/ccs entry",
				"&bCommandright for &f/ccs entry",
				"&eListet alle aktive Condition des Spielers mit Hovererklärung auf.",
				"&eLists all active condition of the player with hoverexplanation.");
		argumentInput(path+"_registered", "registered", perm,
				"/ccs registered [page]", "/ccs registered ", false,
				"&c/ccs registered [Seite] &f| Listet alle Pluginbasierende registrierte Condition auf.",
				"&c/ccs registered [page] &f| Lists all plugin based registered condition.",
				"&bBefehlsrecht für &f/ccs registered",
				"&bCommandright for &f/ccs registered",
				"&eListet alle Pluginbasierende registrierte Condition auf.",
				"&eLists all plugin based registered condition.");
		argumentInput(path+"_remove", "remove", perm,
				"/ccs remove <condition> <player> <reason...>", "/ccs remove ", false,
				"&c/ccs remove <Condition> <Spieler> <Grund...> &f| Entfernt dem angegeben Spieler eine Condition.",
				"&c/ccs remove <condition> <player> <reason...> &f| Remove a condition to the specified player.",
				"&bBefehlsrecht für &f/ccs remove",
				"&bCommandright for &f/ccs remove",
				"&eEntfernt dem angegeben Spieler eine Condition.",
				"&eRemove a condition to the specified player.");
	}
	
	private void comBypass() //INFO:ComBypass
	{
		List<Bypass.Permission> list = new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class));
		for(Bypass.Permission ept : list)
		{
			commandsKeys.put("Bypass."+ept.toString().replace("_", ".")
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"ccs."+ept.toString().toLowerCase().replace("_", ".")}));
		}
	}
	
	private void commandsInput(String path, String name, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToBonusMalusSystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				name}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".PutUpCommandPermToBonusMalusSystem"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToBonusMalusSystem}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	private void argumentInput(String path, String argument, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToBonusMalusSystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				argument}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission+"."+argument}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".PutUpCommandPermToBonusMalusSystem"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToBonusMalusSystem}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	public void initLanguage() //INFO:Languages
	{
		languageKeys.put("InputIsWrong",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDeine Eingabe ist fehlerhaft! Klicke hier auf den Text, um weitere Infos zu bekommen!",
						"&cYour input is incorrect! Click here on the text to get more information!"}));
		languageKeys.put("NoPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast dafür keine Rechte!",
						"&cYou dont not have the rights!"}));
		languageKeys.put("NoPlayerExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler existiert nicht!",
						"&cThe player does not exist!"}));
		languageKeys.put("NoNumber",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine ganze Zahl sein.",
						"&cThe argument &f%value% &must be an integer."}));
		languageKeys.put("NoDouble",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine Gleitpunktzahl sein!",
						"&cThe argument &f%value% &must be a floating point number!"}));
		languageKeys.put("IsNegativ",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine positive Zahl sein!",
						"&cThe argument &f%value% &must be a positive number!"}));
		languageKeys.put("GeneralHover",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick mich!",
						"&eClick me!"}));
		languageKeys.put("Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6BungeeTeleportManager&7]&e=====",
						"&e=====&7[&6BungeeTeleportManager&7]&e====="}));
		languageKeys.put("Next", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e&nnächste Seite &e==>",
						"&e&nnext page &e==>"}));
		languageKeys.put("Past", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e<== &nvorherige Seite",
						"&e<== &nprevious page"}));
		languageKeys.put("IsTrue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&a✔",
						"&a✔"}));
		languageKeys.put("IsFalse", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&c✖",
						"&c✖"}));
		languageKeys.put("PlayerCmdCooldown", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu bist noch im Befehls-Cooldown! Bitte warte etwas!",
						"&cYou are still in the command cooldown! Please wait a little!"}));
		languageKeys.put("PlayerHasNoCondition", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler &f%player% &chat keine Conditioneintrag!",
						"&cThe player &f%player% &chas no conditionentry!"}));
		languageKeys.put("CmdAdd.IsNotRegistered", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Condition ist nicht registriert!",
						"&cThe condition is not registered!"}));
		languageKeys.put("CmdAdd.AddedPermanent", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat permanent die Condition &f%c% &emit dem Wert &f%value% &eund den folgenden Werten erhalten: &f%type% | %internreason% | %reason%",
						"&eThe player &f%player% &ehas permanently received the bonus/penalty &f%c% &ewith the value &f%value% &eand the following values: &f%type% | %internreason% | %reason%"}));
		languageKeys.put("CmdAdd.AddedTemporary", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat die Condition &f%c% &emit dem Wert &f%value% &eund den folgenden Werten erhalten: &f%type% | %duration% | %internreason% | %reason%",
						"&eThe player &f%player% &ehas received the condituon &f%c% &ewith the value &f%value% &eand the following values: &f%type% | %duration% | %internreason% | %reason%"}));
		languageKeys.put("CmdEntry.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&fConditioneintrag &6von &c%player%&f, Seite %page%&e===",
						"&e===&fConditionentry &6from &c%player%&f, page %page%&e==="}));
		languageKeys.put("CmdEntry.ConditionDescriptionOne", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%displayname%&r: ",
						"%displayname%&r: "}));
		languageKeys.put("CmdEntry.ConditonDescriptionTwo", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%value% Einträge",
						"%value% entrys"}));
		languageKeys.put("CmdEntry.BaseValue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&#fc9303Wert: &r",
						"&#fc9303Value: &r"}));
		languageKeys.put("CmdRegistered.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&fRegistrierte Condition, Seite %page%, GesamtAnzahl: %amount%&e===",
						"&e===&fRegistered Condition, page %page%, totalamount: %amount%&e==="}));
		languageKeys.put("CmdRegistered.ConditionDescriptionOne", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%displayname% ",
						"%displayname% "}));
		languageKeys.put("CmdRegistered.ConditionDescriptionTwo", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bCondition Name: &f%condition%~!~&9Anzahl permanente Condition aller Spieler: &f%permcount%~!~&dAnzahl temporäre Condition Spieler: &f%tempcount%~!~&7Erklärung:~!~&f%explanation%",
						"&bCondition Name: &f%condition%~!~&9Number of permanent condition of all players: &f%permcount%~!~&dNumber of temporary condition players: &f%tempcount%~!~&7Explanation:~!~&f%explanation%"}));
		languageKeys.put("CmdRegistered.Add", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&a☑~click@SUGGEST_COMMAND@%cmd%+%c%+<Spieler>+<global/server:servername/world:servername:worldname>+<Wert>+<0/dd-HH:mm>+<Grund...>~hover@SHOW_TEXT@&eKlicke+hier+zum+hinzufügen+einer+Condition+für+einen+Spieler!",
						"&a☑~click@SUGGEST_COMMAND@%cmd%+%c%+<player>+<global/server:servername/world:servername:worldname>+<value>+<0/dd-HH:mm>+<reason...>~hover@SHOW_TEXT@&eClick+here+to+add+a+condition+for+a+player!"}));
		languageKeys.put("CmdRegistered.Remove", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&c〼~click@SUGGEST_COMMAND@%cmd%+%c%+<Spieler>+<Grund...>~hover@SHOW_TEXT@&eKlicke+hier+zum+entfernen+einer+Condition+für+einen+Spieler!",
						"&c〼~click@SUGGEST_COMMAND@%cmd%+%c%+<player>+<reason...>~hover@SHOW_TEXT@&eClick+here+to+remove+a+condition+for+a+player!"}));
	}
	
	public void initConditionBonusMalusLanguage() //INFO:BonusMalusLanguages
	{
		cbmlanguageKeys.put(Bypass.Permission.OTHERPLAYER.toString()+".Displayname",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eByasspermission für /ccs entry <Zahl> [Spieler]",
						"&eBypasspermission for /ccs entry <number> [player]"}));
		cbmlanguageKeys.put(Bypass.Permission.OTHERPLAYER.toString()+".Explanation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eByasspermission für",
						"&eden Befehl /ccs entry um",
						"&eCondition anderer Spieler zu sehen.",
						"&eBypasspermission for",
						"&ethe /ccs entry to see",
						"&econdition of other players."}));
	}
}