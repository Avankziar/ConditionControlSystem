package main.java.me.avankziar.ccs.spigot;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.ccs.spigot.assistance.BackgroundTask;
import main.java.me.avankziar.ccs.spigot.assistance.Utility;
import main.java.me.avankziar.ccs.spigot.cmd.CCSCommandExecutor;
import main.java.me.avankziar.ccs.spigot.cmd.TabCompletion;
import main.java.me.avankziar.ccs.spigot.cmd.ccs.ARGAdd;
import main.java.me.avankziar.ccs.spigot.cmd.ccs.ARGEntry;
import main.java.me.avankziar.ccs.spigot.cmd.ccs.ARGRegistered;
import main.java.me.avankziar.ccs.spigot.cmd.ccs.ARGRemove;
import main.java.me.avankziar.ccs.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.ccs.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.ccs.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.ccs.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.ccs.spigot.cmdtree.CommandExecuteType;
import main.java.me.avankziar.ccs.spigot.conditionbonusmalus.Bypass;
import main.java.me.avankziar.ccs.spigot.database.MysqlHandler;
import main.java.me.avankziar.ccs.spigot.database.MysqlSetup;
import main.java.me.avankziar.ccs.spigot.database.YamlHandler;
import main.java.me.avankziar.ccs.spigot.database.YamlManager;
import main.java.me.avankziar.ccs.spigot.handler.ConfigHandler;
import main.java.me.avankziar.ccs.spigot.ifh.ConditionProvider;
import main.java.me.avankziar.ccs.spigot.ifh.ConditionQueryParserProvider;
import main.java.me.avankziar.ccs.spigot.metrics.Metrics;
import main.java.me.avankziar.ifh.general.bonusmalus.BonusMalus;
import main.java.me.avankziar.ifh.spigot.administration.Administration;

public class CCS extends JavaPlugin
{
	public static Logger log;
	private static CCS plugin;
	public String pluginName = "ConditionControlSystem";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private Utility utility;
	private BackgroundTask backgroundTask;
	
	private ArrayList<BaseConstructor> helpList = new ArrayList<>();
	private ArrayList<CommandConstructor> commandTree = new ArrayList<>();
	private LinkedHashMap<String, ArgumentModule> argumentMap = new LinkedHashMap<>();
	private ArrayList<String> players = new ArrayList<>();
	
	public static String infoCommand = "/";
	
	public Administration administrationConsumer;
	private ConditionProvider conditionProvider;
	private ConditionQueryParserProvider conditonQueryParserProvider;
	private BonusMalus bonusMalusConsumer;
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=CCS
		log.info("  ██████╗ ██████╗███████╗ | API-Version: "+plugin.getDescription().getAPIVersion());
		log.info(" ██╔════╝██╔════╝██╔════╝ | Author: "+plugin.getDescription().getAuthors().toString());
		log.info(" ██║     ██║     ███████╗ | Plugin Website: "+plugin.getDescription().getWebsite());
		log.info(" ██║     ██║     ╚════██║ | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		log.info(" ╚██████╗╚██████╗███████║ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		log.info("  ╚═════╝ ╚═════╝╚══════╝ | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		setupIFHAdministration();
		
		yamlHandler = new YamlHandler(this);
		
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getYamlHandler().getConfig().getBoolean("useIFHAdministration")
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm || yamlHandler.getConfig().getBoolean("Mysql.Status", false) == true)
		{
			mysqlHandler = new MysqlHandler(plugin);
			mysqlSetup = new MysqlSetup(plugin, adm, path);
		} else
		{
			log.severe("MySQL is not set in the Plugin " + pluginName + "!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
			return;
		}
		
		utility = new Utility();
		backgroundTask = new BackgroundTask(this);
		
		setupBypassPerm();
		setupCommandTree();
		setupListeners();
		setupIFHProvider();
		setupBstats();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		log.info(pluginName + " is disabled!");
	}

	public static CCS getPlugin()
	{
		return plugin;
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}

	public void setYamlManager(YamlManager yamlManager)
	{
		this.yamlManager = yamlManager;
	}
	
	public MysqlSetup getMysqlSetup() 
	{
		return mysqlSetup;
	}
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	public BackgroundTask getBackgroundTask()
	{
		return backgroundTask;
	}
	
	public String getServername()
	{
		return getPlugin().getAdministration() != null ? getPlugin().getAdministration().getSpigotServerName() 
				: getPlugin().getYamlHandler().getConfig().getString("ServerName");
	}
	
	private void setupCommandTree()
	{		
		infoCommand += plugin.getYamlHandler().getCommands().getString("ccs.Name");
		
		TabCompletion tab = new TabCompletion(plugin);
		
		ArgumentConstructor add = new ArgumentConstructor(CommandExecuteType.CCS_ADD, "ccs_add", 0, 6, 999, true, null);
		new ARGAdd(add);
		ArgumentConstructor entry = new ArgumentConstructor(CommandExecuteType.CCS_ENTRY, "ccs_entry", 0, 0, 3, true, null);
		new ARGEntry(entry);
		ArgumentConstructor registered = new ArgumentConstructor(CommandExecuteType.CCS_REGISTERED, "ccs_registered", 0, 0, 1, false, null);
		new ARGRegistered(registered);
		ArgumentConstructor remove = new ArgumentConstructor(CommandExecuteType.CCS_REMOVE, "ccs_remove", 0, 2, 3, true, null);
		new ARGRemove(remove);
		
		CommandConstructor ccs = new CommandConstructor(CommandExecuteType.CCS, "ccs", false,
				add, entry, registered, remove);
		registerCommand(ccs.getPath(), ccs.getName());
		getCommand(ccs.getName()).setExecutor(new CCSCommandExecutor(plugin, ccs));
		getCommand(ccs.getName()).setTabCompleter(tab);
	}
	
	public void setupBypassPerm()
	{
		String path = "Count.";
		for(Bypass.Counter bypass : new ArrayList<Bypass.Counter>(EnumSet.allOf(Bypass.Counter.class)))
		{
			if(!bypass.forPermission())
			{
				continue;
			}
			Bypass.set(bypass, yamlHandler.getCommands().getString(path+bypass.toString()));
		}
		path = "Bypass.";
		for(Bypass.Permission bypass : new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class)))
		{
			Bypass.set(bypass, yamlHandler.getCommands().getString(path+bypass.toString()));
		}
	}
	
	public ArrayList<BaseConstructor> getCommandHelpList()
	{
		return helpList;
	}
	
	public void addingCommandHelps(BaseConstructor... objects)
	{
		for(BaseConstructor bc : objects)
		{
			helpList.add(bc);
		}
	}
	
	public ArrayList<CommandConstructor> getCommandTree()
	{
		return commandTree;
	}
	
	public CommandConstructor getCommandFromPath(String commandpath)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getPath().equalsIgnoreCase(commandpath))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public CommandConstructor getCommandFromCommandString(String command)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getName().equalsIgnoreCase(command))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public void registerCommand(String... aliases) 
	{
		PluginCommand command = getCommand(aliases[0], plugin);
	 
		command.setAliases(Arrays.asList(aliases));
		getCommandMap().register(plugin.getDescription().getName(), command);
	}
	 
	private static PluginCommand getCommand(String name, CCS plugin) 
	{
		PluginCommand command = null;
	 
		try 
		{
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
	 
			command = c.newInstance(name, plugin);
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} catch (InstantiationException e) 
		{
			e.printStackTrace();
		} catch (InvocationTargetException e) 
		
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		}
	 
		return command;
	}
	 
	private static CommandMap getCommandMap() 
	{
		CommandMap commandMap = null;
	 
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) 
			{
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);
	 
				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException e) 
		{
			e.printStackTrace();
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
	 
		return commandMap;
	}
	
	public LinkedHashMap<String, ArgumentModule> getArgumentMap()
	{
		return argumentMap;
	}
	
	public ArrayList<String> getMysqlPlayers()
	{
		return players;
	}

	public void setMysqlPlayers(ArrayList<String> players)
	{
		this.players = players;
	}
	
	public void setupListeners()
	{
		//PluginManager pm = getServer().getPluginManager();
		//pm.registerEvents(new BackListener(plugin), plugin);
	}
	
	public boolean reload() throws IOException
	{
		if(!yamlHandler.loadYamlHandler())
		{
			return false;
		}
		if(yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			if(!mysqlSetup.loadMysqlSetup())
			{
				return false;
			}
		} else
		{
			return false;
		}
		return true;
	}
	
	public boolean existHook(String externPluginName)
	{
		if(plugin.getServer().getPluginManager().getPlugin(externPluginName) == null)
		{
			return false;
		}
		log.info(pluginName+" hook with "+externPluginName);
		return true;
	}
	
	private void setupIFHAdministration()
	{ 
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		RegisteredServiceProvider<main.java.me.avankziar.ifh.spigot.administration.Administration> rsp = 
                getServer().getServicesManager().getRegistration(Administration.class);
		if (rsp == null) 
		{
		   return;
		}
		administrationConsumer = rsp.getProvider();
		log.info(pluginName + " detected InterfaceHub >>> Administration.class is consumed!");
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
	}
	
	private boolean setupIFHProvider()
	{
		if(!setupIFHCondition())
		{
			return false;
		}
		if(!setupIFHConditionQueryParser())
		{
			return false;
		}
		return true;
	}
	
	private boolean setupIFHCondition()
	{
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
			log.severe("IFH is not set in the Plugin " + pluginName + "! Disable plugin!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
	    	return false;
	    }
		conditionProvider = new ConditionProvider();
    	plugin.getServer().getServicesManager().register(
        main.java.me.avankziar.ifh.general.condition.Condition.class,
        conditionProvider,
        this,
        ServicePriority.Normal);
    	log.info(pluginName + " detected InterfaceHub >>> Condition.class is provided!");
    	for(BaseConstructor bc : getCommandHelpList())
		{
			if(!bc.isPutUpCmdPermToConditionSystem())
			{
				continue;
			}
			if(conditionProvider.isRegistered(bc.getConditionPath()))
			{
				continue;
			}
			String[] ex = {plugin.getYamlHandler().getCommands().getString(bc.getPath()+".Explanation")};
			conditionProvider.register(
					bc.getConditionPath(),
					plugin.getYamlHandler().getCommands().getString(bc.getPath()+".Displayname", "Command "+bc.getName()),
					ex);
		}
		return false;
	}
	
	public ConditionProvider getCondition()
	{
		return conditionProvider;
	}
	
	private boolean setupIFHConditionQueryParser()
	{
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
			log.severe("IFH is not set in the Plugin " + pluginName + "! Disable plugin!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
	    	return false;
	    }
		conditonQueryParserProvider = new ConditionQueryParserProvider();
    	plugin.getServer().getServicesManager().register(
        main.java.me.avankziar.ifh.general.condition.ConditionQueryParser.class,
        conditonQueryParserProvider,
        this,
        ServicePriority.Normal);
    	log.info(pluginName + " detected InterfaceHub >>> ConditionQueryParser.class is provided!");
		return false;
	}
	
	public ConditionQueryParserProvider getConditionQueryParserProvider()
	{
		return conditonQueryParserProvider;
	}
	
	public void setupIFHConsumer()
	{
		setupIFHBonusMalus();
	}
	
	private void setupIFHBonusMalus() 
	{
		if(!new ConfigHandler().isMechanicBonusMalusEnabled())
		{
			return;
		}
        if(Bukkit.getPluginManager().getPlugin("InterfaceHub") == null) 
        {
            return;
        }
        new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
				try
				{
					if(i == 20)
				    {
						cancel();
						return;
				    }
				    RegisteredServiceProvider<main.java.me.avankziar.ifh.general.bonusmalus.BonusMalus> rsp = 
		                             getServer().getServicesManager().getRegistration(
		                            		 main.java.me.avankziar.ifh.general.bonusmalus.BonusMalus.class);
				    if(rsp == null) 
				    {
				    	//Check up to 20 seconds after the start, to connect with the provider
				    	i++;
				        return;
				    }
				    bonusMalusConsumer = rsp.getProvider();
				    log.info(pluginName + " detected InterfaceHub >>> BonusMalus.class is consumed!");
				    cancel();
				} catch(NoClassDefFoundError e)
				{
					cancel();
				}
			}
        }.runTaskTimer(plugin, 20L, 20*2);
	}
	
	public BonusMalus getBonusMalus()
	{
		return bonusMalusConsumer;
	}
	
	public void setupBstats()
	{
		int pluginId = 17753; //Bungee 17754
        new Metrics(this, pluginId);
	}
}