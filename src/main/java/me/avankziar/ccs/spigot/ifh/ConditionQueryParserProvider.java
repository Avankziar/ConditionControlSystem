package main.java.me.avankziar.ccs.spigot.ifh;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;

import main.java.me.avankziar.ccs.spigot.assistance.MatchApi;
import main.java.me.avankziar.ifh.general.condition.ConditionQueryParser;
import main.java.me.avankziar.ifh.general.math.MathFormulaParser;
import main.java.me.avankziar.ifh.spigot.event.misc.ConditionQueryOutputEvent;

public class ConditionQueryParserProvider implements ConditionQueryParser
{	
	public boolean parseBaseConditionQuery(String conditionQuery)
	{
		String[] s = conditionQuery.split(":");
		String a = s[0];
		String b = s[2];
		boolean boo = false;
		switch(s[1])
		{
		case "==":
			if(MatchApi.isBoolean(a) && MatchApi.isBoolean(b))
			{
				boo = Boolean.parseBoolean(a) == Boolean.parseBoolean(b);
			} else if(MatchApi.isLong(a) && MatchApi.isLong(b))
			{
				boo = Double.parseDouble(a) == Double.parseDouble(b);
			} else if(MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) == Double.parseDouble(b);
			}
			break;
		case "!=":
			if(MatchApi.isBoolean(a) && MatchApi.isBoolean(b))
			{
				boo = Boolean.parseBoolean(a) != Boolean.parseBoolean(b);
			} else if(MatchApi.isLong(a) && MatchApi.isLong(b))
			{
				boo = Double.parseDouble(a) != Double.parseDouble(b);
			} else if(MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) != Double.parseDouble(b);
			}
			break;
		case ">":
			if(MatchApi.isLong(a) && MatchApi.isLong(b))
			{
				boo = Double.parseDouble(a) > Double.parseDouble(b);
			} else if(MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) > Double.parseDouble(b);
			}
			break;
		case "<":
			if(MatchApi.isLong(a) && MatchApi.isLong(b))
			{
				boo = Double.parseDouble(a) < Double.parseDouble(b);
			} else if(MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) < Double.parseDouble(b);
			}
			break;
		case ">=":
			if(MatchApi.isLong(a) && MatchApi.isLong(b))
			{
				boo = Double.parseDouble(a) >= Double.parseDouble(b);
			} else if(MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) >= Double.parseDouble(b);
			}
			break;
		case "<=":
			if(MatchApi.isLong(a) && MatchApi.isLong(b))
			{
				boo = Double.parseDouble(a) <= Double.parseDouble(b);
			} else if(MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) <= Double.parseDouble(b);
			}
			break;
		case "eq":
			boo = a.equals(b);
			break;
		case "neq":
			boo = !a.equals(b);
			break;
		case "eqic":
			boo = a.equalsIgnoreCase(b);
			break;
		case "neqic":
			boo = !	a.equalsIgnoreCase(b);
			break;
		}
		return boo;
	}
	
	public Boolean parseSimpleConditionQuery(String conditionQuery, LinkedHashMap<String, Boolean> variables)
	{
		String cq = conditionQuery.strip().replace(" ", "");
		for(Entry<String, Boolean> e : variables.entrySet())
		{
			cq = cq.replace(e.getKey(), String.valueOf(e.getValue() ? 1 : 0));
		}
		cq = cq.replace("!0", "1").replace("!1", "0").replace("&&", "*").replace("||", "+");
		double d = 0.0;
		try
		{
			d = new MathFormulaParser().parse(cq);
		} catch(Exception e) 
		{
			return null;
		}
		return d >= 1.0 ? true : false;
	}
	
	public ArrayList<String> parseBranchedConditionQuery(
			UUID uuid,
			ArrayList<String> conditionQuery_Vars_Output_List,
			boolean asEvent, String pluginnameForPossibleEvent)
	{
		ArrayList<String> conditionQueryList = new ArrayList<>();
		LinkedHashMap<String, String> variables = new LinkedHashMap<>();
		LinkedHashMap<String, ArrayList<String>> outputOptions = new LinkedHashMap<>();
		for(String split : conditionQuery_Vars_Output_List)
		{
			if(split.startsWith("if") || split.startsWith("elseif") || split.startsWith("else"))
			{ //TODO Änderung aller bm & condition in SalE und BUP ändern.
				//if:(a && b || c):Do.A
				String[] s = split.split(":");
				if(s.length != 3)
				{
					continue;
				}
				conditionQueryList.add(split);
			} else if(split.startsWith("output"))
			{
				//output:cmd:/warp p
				//output:event:pluginname_....
				String[] s = split.split(":");
				if(s.length != 3)
				{
					continue;
				}
				ArrayList<String> list = new ArrayList<>();
				if(outputOptions.containsKey(s[1]))
				{
					list = outputOptions.get(s[1]);
				}
				list.add(s[2]);
				outputOptions.put(s[1], list);
			} else
			{
				/* xyz:true            /true;false
				 * xyz:10:>:5          />;<;>=;!=;==    For Numbers
				 * xyz:Evan:eq:Todd    /eq;eqic;neq
				 * Variable over [var]
				 */
				String[] s = split.split(":");
				String key = s[0];
				if(s.length != 2 && s.length == 4)
				{
					continue;
				}
				variables.put(key, split);
			}
		}
		return parseBranchedConditionQuery(uuid, conditionQueryList, variables, outputOptions, asEvent, pluginnameForPossibleEvent);
	}
	
	public ArrayList<String> parseBranchedConditionQuery(UUID uuid, 
			ArrayList<String> conditionQueryList,
			LinkedHashMap<String, String> variables,
			LinkedHashMap<String, ArrayList<String>> outputOptions,
			boolean asEvent, String pluginnameForPossibleEvent)
	{
		LinkedHashMap<String, Boolean> vars = new LinkedHashMap<>();
		for(Entry<String, String> v : variables.entrySet())
		{
			String[] s = v.getValue().split(":");
			boolean boo = false;
			if(s.length == 2)
			{
				if(MatchApi.isBoolean(s[1]))
				{
					boo = Boolean.parseBoolean(s[1]);
				}
			} else if(s.length == 4)
			{
				String a = s[1]; //TODO ConditionEntry hier holen
				String va = s[2];
				String b = s[3]; //TODO ConditionEntry hier holen
				boo = parseBaseConditionQuery(a+":"+va+":"+b);
			} else
			{
				continue;
			}
			vars.put(v.getKey(), boo);
		}
		String output = null;
		for(String condition : conditionQueryList)
		{
			String[] sp = condition.split(":");
			if(sp.length != 3)
			{
				continue;
			}
			String c = sp[1];
			if(sp[0].equalsIgnoreCase("if") || sp[0].equalsIgnoreCase("elseif"))
			{
				Boolean boo = parseSimpleConditionQuery(c, vars);
				if(boo == null)
				{
					continue;
				}
				if(!boo.booleanValue())
				{
					continue;
				}
				output = sp[2];
				break;
			} else if(sp[0].equalsIgnoreCase("else"))
			{
				Boolean boo = parseSimpleConditionQuery(c, vars);
				if(boo == null)
				{
					break;
				}
				if(!boo.booleanValue())
				{
					break;
				}
				output = sp[2];
				break;
			} else
			{
				break;
			}
		}
		if(asEvent)
		{
			ArrayList<String> op = outputOptions.get(output);
			if(op != null)
			{
				Bukkit.getPluginManager().callEvent(new ConditionQueryOutputEvent(false, uuid, pluginnameForPossibleEvent, op));
				//getProxy().getPluginManager().callEvent(new ConditionQueryOutputEvent(uuid, op)); //TODO Bungeeversion
			}
			return null;
		}
		return output == null ? null : outputOptions.get(output);
	}
}