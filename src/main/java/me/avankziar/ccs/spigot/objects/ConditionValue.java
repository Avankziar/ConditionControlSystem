package main.java.me.avankziar.ccs.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import main.java.me.avankziar.ccs.spigot.database.MysqlHandable;
import main.java.me.avankziar.ccs.spigot.database.MysqlHandler;

public class ConditionValue implements MysqlHandable
{
	private int id;
	private UUID uuid;
	private String conditionName;
	private String value;
	private String internReason;
	private String displayReason;
	private String server;
	private String world;
	private long duration;

	public ConditionValue() {}
	
	public ConditionValue(int id, UUID uuid, String conditionName,
			String value, String internReason, String displayReason, String server, String world, long duration)
	{
		setId(id);
		setUuid(uuid);
		setConditionName(conditionName);
		setValue(value);
		setInternReason(internReason);
		setDisplayReason(displayReason);
		setServer(server);
		setWorld(world);
		setDuration(duration);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getConditionName()
	{
		return conditionName;
	}

	public void setConditionName(String conditionName)
	{
		this.conditionName = conditionName;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getInternReason()
	{
		return internReason;
	}

	public void setInternReason(String internReason)
	{
		this.internReason = internReason;
	}

	public String getDisplayReason()
	{
		return displayReason;
	}

	public void setDisplayReason(String displayReason)
	{
		this.displayReason = displayReason;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public String getWorld()
	{
		return world;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public long getDuration()
	{
		return duration;
	}

	public void setDuration(long duration)
	{
		this.duration = duration;
	}

	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`, `condition_name`, `condition_value`,"
					+ " `intern_reason`, `display_reason`,"
					+ " `server`, `world`, `duration`) " 
					+ "VALUES("
					+ "?, ?, ?, "
					+ "?, ?, "
					+ "?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, uuid.toString());
	        ps.setString(2, conditionName);
	        ps.setString(3, value);
	        ps.setString(4, internReason);
	        ps.setString(5, displayReason);
	        ps.setString(6, server);
	        ps.setString(7, world);
	        ps.setLong(8, duration);
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
				+ "` SET `player_uuid` = ?, `condition_name` = ?, `condition_value` = ?,"
				+ " `intern_reason` = ?, `display_reason` = ?,"
				+ " `server` = ?, `world` = ?, `duration` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, uuid.toString());
	        ps.setString(2, conditionName);
	        ps.setString(3, value);
	        ps.setString(4, internReason);
	        ps.setString(5, displayReason);
	        ps.setString(6, server);
	        ps.setString(7, world);
	        ps.setLong(8, duration);
			int i = 9;
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
			String sql = "SELECT * FROM `" + MysqlHandler.Type.CONDITIONVALUE.getValue() 
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
						new ConditionValue(
								rs.getInt("id"),
								UUID.fromString(rs.getString("player_uuid")),
								rs.getString("condition_name"),
								rs.getString("condition_value"),
								rs.getString("intern_reason"),
								rs.getString("display_reason"),
								rs.getString("server"),
								rs.getString("world"),
								rs.getLong("duration")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<ConditionValue> convert(ArrayList<Object> arrayList)
	{
		ArrayList<ConditionValue> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof ConditionValue)
			{
				l.add((ConditionValue) o);
			}
		}
		return l;
	}
}
