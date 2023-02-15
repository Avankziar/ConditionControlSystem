package main.java.me.avankziar.ccs.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import main.java.me.avankziar.ccs.spigot.database.MysqlHandable;
import main.java.me.avankziar.ccs.spigot.database.MysqlHandler;

public class Condition implements MysqlHandable
{
	private String conditionName;
	private String displayConditionName;
	private ArrayList<String> explanation;	
	
	public Condition(){}
	
	public Condition(String conditionName, String displayConditionName, String[] explanation)
	{
		setConditionName(conditionName);
		setDisplayConditionName(displayConditionName);
		ArrayList<String> ex = new ArrayList<>();
		if(explanation != null)
		{
			for(String s : explanation)
			{
				ex.add(s);
			}
		}		
		setExplanation(ex);
	}

	public String getConditionName()
	{
		return conditionName;
	}

	public void setConditionName(String conditionName)
	{
		this.conditionName = conditionName;
	}

	public String getDisplayConditionName()
	{
		return displayConditionName;
	}

	public void setDisplayConditionName(String displayConditionName)
	{
		this.displayConditionName = displayConditionName;
	}

	public ArrayList<String> getExplanation()
	{
		return explanation;
	}

	public void setExplanation(ArrayList<String> explanation)
	{
		this.explanation = explanation;
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`condition_name`, `display_name`, `explanation`) " 
					+ "VALUES("
					+ "?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, conditionName);
	        ps.setString(2, displayConditionName);
	        ps.setString(3, String.join("~!~", explanation));
	        int i = ps.executeUpdate();
	        MysqlHandler.addRows(MysqlHandler.QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "UPDATE `" + tablename
				+ "` SET `condition_name` = ?, `display_name` = ?, `explanation` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, conditionName);
	        ps.setString(2, displayConditionName);
	        ps.setString(3, String.join("~!~", explanation));
			int i = 4;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + MysqlHandler.Type.CONDITION.getValue() 
				+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			MysqlHandler.addRows(MysqlHandler.QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<Object> al = new ArrayList<>();
			
			while (rs.next()) 
			{
				al.add(
						new Condition(rs.getString("condition_name"),
						rs.getString("display_name"),
						rs.getString("explanation").split("~!~")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<Condition> convert(ArrayList<Object> arrayList)
	{
		ArrayList<Condition> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof Condition)
			{
				l.add((Condition) o);
			}
		}
		return l;
	}
}
