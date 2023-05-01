package main.java.me.avankziar.ccs.spigot.ifh;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import main.java.me.avankziar.ccs.spigot.CCS;
import main.java.me.avankziar.ccs.spigot.assistance.Experience;
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
		default:
			break;
		case "==":
			if(MatchApi.isBoolean(a) && MatchApi.isBoolean(b))
			{
				boo = Boolean.parseBoolean(a) == Boolean.parseBoolean(b);
			} else if((MatchApi.isLong(a) && MatchApi.isLong(b))
					|| MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) == Double.parseDouble(b);
			}
			break;
		case "!=":
			if(MatchApi.isBoolean(a) && MatchApi.isBoolean(b))
			{
				boo = Boolean.parseBoolean(a) != Boolean.parseBoolean(b);
			} else if((MatchApi.isLong(a) && MatchApi.isLong(b))
					|| MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) != Double.parseDouble(b);
			}
			break;
		case ">":
			if((MatchApi.isLong(a) && MatchApi.isLong(b))
						|| MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) > Double.parseDouble(b);
			}
			break;
		case "<":
			if((MatchApi.isLong(a) && MatchApi.isLong(b))
					|| MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) < Double.parseDouble(b);
			}
			break;
		case ">=":
			if((MatchApi.isLong(a) && MatchApi.isLong(b))
					|| MatchApi.isDouble(a) && MatchApi.isDouble(b))
			{
				boo = Double.parseDouble(a) >= Double.parseDouble(b);
			}
			break;
		case "<=":
			if((MatchApi.isLong(a) && MatchApi.isLong(b))
					|| MatchApi.isDouble(a) && MatchApi.isDouble(b))
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
			boo = !a.equalsIgnoreCase(b);
			break;
		case "cont":
			boo = a.contains(b);
			break;
		case "ncont":
			boo = !a.contains(b);
			break;
		case "contic":
			boo = a.toLowerCase().contains(b.toLowerCase());
			break;
		case "ncontic":
			boo = !a.toLowerCase().contains(b.toLowerCase());
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
			UUID uuid, UUID uuidTwo,
			ArrayList<String> conditionQuery_Vars_Output_List)
	{
		ArrayList<String> conditionQueryList = new ArrayList<>();
		LinkedHashMap<String, String> variables = new LinkedHashMap<>();
		LinkedHashMap<String, ArrayList<String>> outputOptions = new LinkedHashMap<>();
		boolean asEvent = false;
		String pluginnameForPossibleEvent = "";
		for(String split : conditionQuery_Vars_Output_List)
		{
			if(split.startsWith("if") || split.startsWith("elseif") || split.startsWith("else"))
			{
				//if:(a && b || c):Do.A
				String[] s = split.split(":");
				if(s.length != 3)
				{
					continue;
				}
				conditionQueryList.add(split);
			} else if(split.startsWith("output"))
			{
				//output:abc_def.ddd:cmd:/warp p
				String[] s = split.split(":");
				if(s.length < 3)
				{
					continue;
				}
				ArrayList<String> list = new ArrayList<>();
				if(outputOptions.containsKey(s[1]))
				{
					list = outputOptions.get(s[1]);
				}
				String o = "output:"+s[1]+":";
				list.add(split.substring(o.length()));
				outputOptions.put(s[1], list);
			} else if(split.startsWith("event"))
			{
				String[] s = split.split(":");
				if(s.length != 2)
				{
					continue;
				}
				asEvent = true;
				pluginnameForPossibleEvent = s[1];
			} else
			{
				/* xyz:true            /true;false
				 * xyz:10:>:5          />;<;>=;!=;==    For Numbers
				 * xyz:Evan:eq:Todd    /eq;eqic;neq
				 * Variable over var=
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
		return parseBranchedConditionQuery(uuid, uuidTwo, conditionQueryList, variables, outputOptions, asEvent, pluginnameForPossibleEvent);
	}
	
	public ArrayList<String> parseBranchedConditionQuery(UUID uuid, UUID uuidTwo,
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
				} else if(s[1].startsWith("var="))
				{
					Player other = Bukkit.getPlayer(uuid);
					if(other != null && other.isOnline())
					{
						String[] ara = getVariable(other, s[1]);
						int t = 0;
						int f = 0;
						for(String aa : ara)
						{
							if(MatchApi.isBoolean(aa))
							{
								if(MatchApi.getBoolean(aa))
								{
									t++;
								} else
								{
									f++;
								}
							}
						}
						boo = t > 0 && t > f;
					}
				}
			} else if(s.length == 4)
			{
				String a = s[1];
				String va = s[2];
				String b = s[3];
				if(a.startsWith("var1=") || b.startsWith("var1=")
						|| a.startsWith("var2=") || b.startsWith("var2="))
				{
					a = a.substring(5);
					Player other = Bukkit.getPlayer(uuid);
					Player other2 = Bukkit.getPlayer(uuidTwo);
					if(other != null && other.isOnline())
					{
						boolean check = false;
						if((a.startsWith("var1=") && b.startsWith("var1="))
								|| (a.startsWith("var2=") && b.startsWith("var2="))
								|| (a.startsWith("var1=") && b.startsWith("var2="))
								|| (a.startsWith("var2=") && b.startsWith("var1=")))
						{
							String[] ara = getVariable(a.startsWith("var1=") ? other : other2, a);
							String[] arb = getVariable(a.startsWith("var1=") ? other : other2, b);
							for(String aa : ara)
							{
								for(String bb : arb)
								{
									if(parseBaseConditionQuery(aa+":"+va+":"+bb))
									{
										check = true;
										break;
									}
								}
								if(check)
								{
									break;
								}
							}
							boo = check;
						} else if(a.startsWith("var1=") || a.startsWith("var2="))
						{
							String[] ara = getVariable(a.startsWith("var1=") ? other : other2, a);
							for(String aa : ara)
							{
								if(parseBaseConditionQuery(aa+":"+va+":"+b))
								{
									check = true;
									break;
								}
							}
							boo = check;
						} else if(b.startsWith("var1=") || b.startsWith("var2="))
						{
							String[] arb = getVariable(b.startsWith("var1=") ? other : other2, b);
							for(String bb : arb)
							{
								if(parseBaseConditionQuery(a+":"+va+":"+bb))
								{
									check = true;
									break;
								}
							}
							boo = check;
						}
					}					
				} else
				{
					boo = parseBaseConditionQuery(a+":"+va+":"+b);
				}
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
				Bukkit.getPluginManager().callEvent(new ConditionQueryOutputEvent(false, uuid, uuidTwo, pluginnameForPossibleEvent, op));
				//getProxy().getPluginManager().callEvent(new ConditionQueryOutputEvent(uuid, op)); //TODO Bungeeversion
			}
			return null;
		}
		return output == null ? null : outputOptions.get(output);
	}
	
	@SuppressWarnings("deprecation")
	private String[] getVariable(Player other, String var)
	{
		if(other == null || var.isEmpty())
		{
			return new String[] {var};
		}
		String[] ar = null;
		switch(var)
		{
		default:
			ArrayList<String> al = new ArrayList<>();
			if(var.startsWith("%") && var.endsWith("%") &&
					Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			{
				String s = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(other, var);
				ar = new String[] {s != null ? s : var};
			} else if(var.startsWith("perm="))
			{
				String v = var.substring(5);
				if(v.isBlank() || v.isEmpty())
				{
					ar = new String[] {var}; break;
				} else
				{
					ar = new String[] {String.valueOf(other.hasPermission(v))}; break;
				}				
			} else if(var.startsWith("bm="))
			{
				String[] v = var.substring(3).split("="); //bm=<Zahl>=bonusmalus
				if(v.length != 2)
				{
					ar = new String[] {String.valueOf(var)}; break;
				} else
				{
					double r = CCS.getPlugin().getBonusMalus().getResult(
							other.getUniqueId(), Double.parseDouble(v[0]), v[1], CCS.getPlugin().getServername(), other.getWorld().getName());
					ar = new String[] {String.valueOf(r)}; break;			
				}
			} else
			{
				String[] arr = CCS.getPlugin().getCondition().getConditionEntry(other.getUniqueId(), var);
				if(arr != null && arr.length > 0)
				{
					for(String s : arr)
					{
						if(MatchApi.isBoolean(s))
						{
							al.add(MatchApi.getBoolean(s) ? "true" : "false");
						} else if(CCS.getPlugin().getBonusMalus() != null && other != null)
						{
							if(MatchApi.isLong(s) || MatchApi.isDouble(s))
							{
								double r = CCS.getPlugin().getBonusMalus().getResult(
										other.getUniqueId(), Double.parseDouble(s), var, CCS.getPlugin().getServername(), other.getWorld().getName());
								al.add(String.valueOf(r));
							} else
							{
								al.add(String.valueOf(s));
							}
						} else
						{
							al.add(String.valueOf(s));
						}
					}
				} else
				{
					al.add(var);
				}				
			}
			break;
		case "AbsorptionAmount":
			ar = new String[] {String.valueOf(other.getAbsorptionAmount())}; break;
		case "AllowFlight":
			ar = new String[] {String.valueOf(other.getAllowFlight())}; break;
		case "ArrowCooldown":
			ar = new String[] {String.valueOf(other.getArrowCooldown())}; break;
		case "ArrowsInBody":
			ar = new String[] {String.valueOf(other.getArrowsInBody())}; break;
		case "AttackCooldown":
			ar = new String[] {String.valueOf(other.getAttackCooldown())}; break;
		case "CanPickupItems":
			ar = new String[] {String.valueOf(other.getCanPickupItems())}; break;
		case "ClientViewDistance":
			ar = new String[] {String.valueOf(other.getClientViewDistance())}; break;
		case "Exhaustion":
			ar = new String[] {String.valueOf(other.getExhaustion())}; break;
		case "Exp":
			ar = new String[] {String.valueOf(Experience.getExp(other))}; break;
		case "EyeHeight":
			ar = new String[] {String.valueOf(other.getEyeHeight())}; break;
		case "FallDistance":
			ar = new String[] {String.valueOf(other.getFallDistance())}; break;
		case "FireTicks":
			ar = new String[] {String.valueOf(other.getFireTicks())}; break;
		case "FoodLevel":
			ar = new String[] {String.valueOf(other.getFoodLevel())}; break;
		case "Health":
			ar = new String[] {String.valueOf(other.getHealth())}; break;
		case "Level":
			ar = new String[] {String.valueOf(other.getLevel())}; break;
		case "MaxHealth":
			ar = new String[] {String.valueOf(other.getMaxHealth())}; break;
		case "RemainingAir":
			ar = new String[] {String.valueOf(other.getRemainingAir())}; break;
		case "SaturatedRegenRate":
			ar = new String[] {String.valueOf(other.getSaturatedRegenRate())}; break;
		case "StarvationRate":
			ar = new String[] {String.valueOf(other.getStarvationRate())}; break;
		case "TotalExperience":
			ar = new String[] {String.valueOf(other.getTotalExperience())}; break;
		case "UnsaturatedRegenRate":
			ar = new String[] {String.valueOf(other.getUnsaturatedRegenRate())}; break;
		case "WalkSpeed":
			ar = new String[] {String.valueOf(other.getWalkSpeed())}; break;
		case "CustomName":
			ar = new String[] {String.valueOf(other.getCustomName())}; break;
		case "DisplayName":
			ar = new String[] {String.valueOf(other.getDisplayName())}; break;
		case "PlayerName":
			ar = new String[] {String.valueOf(other.getName())}; break;
		case "Server":
			ar = new String[] {String.valueOf(CCS.getPlugin().getServername())}; break;
		case "PlayerUUID":
			ar = new String[] {String.valueOf(other.getUniqueId().toString())}; break;
		case "World":
			ar = new String[] {String.valueOf(other.getWorld().getName())}; break;
		case "isBanned":
			ar = new String[] {String.valueOf(other.isBanned())}; break;
		case "isBlocking":
			ar = new String[] {String.valueOf(other.isBlocking())}; break;
		case "isClimbing":
			ar = new String[] {String.valueOf(other.isClimbing())}; break;
		case "isCollidable":
			ar = new String[] {String.valueOf(other.isCollidable())}; break;
		case "isCustomNameVisible":
			ar = new String[] {String.valueOf(other.isCustomNameVisible())}; break;
		case "isDead":
			ar = new String[] {String.valueOf(other.isDead())}; break;
		case "isFlying":
			ar = new String[] {String.valueOf(other.isFlying())}; break;
		case "isFrozen":
			ar = new String[] {String.valueOf(other.isFrozen())}; break;
		case "isGliding":
			ar = new String[] {String.valueOf(other.isGliding())}; break;
		case "isGlowing":
			ar = new String[] {String.valueOf(other.isGlowing())}; break;
		case "isHandRaised":
			ar = new String[] {String.valueOf(other.isHandRaised())}; break;
		case "isInsideVehicle":
			ar = new String[] {String.valueOf(other.isInsideVehicle())}; break;
		case "isInvisible":
			ar = new String[] {String.valueOf(other.isInvisible())}; break;
		case "isInvulnerable":
			ar = new String[] {String.valueOf(other.isInvulnerable())}; break;
		case "isInWater":
			ar = new String[] {String.valueOf(other.isInWater())}; break;
		case "isOnline":
			ar = new String[] {String.valueOf(other.isOnline())}; break;
		case "isOp":
			ar = new String[] {String.valueOf(other.isOp())}; break;
		case "isRiptiding":
			ar = new String[] {String.valueOf(other.isRiptiding())}; break;
		case "isSilent":
			ar = new String[] {String.valueOf(other.isSilent())}; break;
		case "isSleeping":
			ar = new String[] {String.valueOf(other.isSleeping())}; break;
		case "isSleepingIgnored":
			ar = new String[] {String.valueOf(other.isSleepingIgnored())}; break;
		case "isSneaking":
			ar = new String[] {String.valueOf(other.isSneaking())}; break;
		case "isSprinting":
			ar = new String[] {String.valueOf(other.isSprinting())}; break;
		case "isSwimming":
			ar = new String[] {String.valueOf(other.isSwimming())}; break;
		case "isVisualFire":
			ar = new String[] {String.valueOf(other.isVisualFire())}; break;
		case "isWhitelisted":
			ar = new String[] {String.valueOf(other.isWhitelisted())}; break;
		case "hasGravity":
			ar = new String[] {String.valueOf(other.hasGravity())}; break;
		}
		return ar;
	}
}