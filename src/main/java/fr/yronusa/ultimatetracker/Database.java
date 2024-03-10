package fr.yronusa.ultimatetracker;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import fr.yronusa.ultimatetracker.Config.Config;
import fr.yronusa.ultimatetracker.Event.ItemUpdateDateEvent;
import org.bukkit.Bukkit;
import java.sql.*;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;


public class Database {

    public static MysqlDataSource getDataSource() throws SQLException {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName(Config.databaseHost);
        dataSource.setPort(Config.databasePort);
        dataSource.setDatabaseName(Config.databaseName);
        dataSource.setUser(Config.databaseUser);
        dataSource.setPassword(Config.databasePassword);
        dataSource.setServerTimezone(TimeZone.getDefault().getID());

        return dataSource;
    }

    public static void add(TrackedItem trackedItem) {
        String itemBase64 = trackedItem.getBase64();
        MysqlDataSource dataSource;

        try {
            dataSource = getDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
/**
        String base = "INSERT INTO TRACKED_ITEMS (UUID, ITEMBASE64, LAST_UPDATE, LAST_INVENTORIES, IS_BLACKLISTED)\n" +
                "VALUES (" + trackedItem.getOriginalID() + "," + itemBase64 + "," + trackedItem.getLastUpdate() +"," + "trackedItem.getLastInventories().toString()" +", 0);\n";
        **/

        String statement = "INSERT INTO TRACKED_ITEMS (UUID, ITEMBASE64, LAST_UPDATE, LAST_INVENTORIES, IS_BLACKLISTED) VALUES (?, ?, ?, ?, ?)";
        MysqlDataSource finalDataSource = dataSource;
        Bukkit.getScheduler().runTaskAsynchronously(UltimateTracker.getInstance(), new Runnable() {
            @Override
            public void run() {

                Connection conn = null;
                try {
                    conn = finalDataSource.getConnection();
                    PreparedStatement preparedStatement = conn.prepareStatement(statement);
                    preparedStatement.setString(1, trackedItem.getOriginalID().toString());
                    preparedStatement.setString(2, itemBase64);
                    preparedStatement.setTimestamp(3, trackedItem.getLastUpdateItem());
                    preparedStatement.setString(4, "TODO");
                    preparedStatement.setInt(5, 0);

                    int i = preparedStatement.executeUpdate();
                    if (i > 0) {
                        System.out.println("ROW INSERTED");
                    } else {
                        System.out.println("ROW NOT INSERTED");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }


            }
        });


    }

    public static Timestamp getLastUpdate(UUID uuid){

        MysqlDataSource dataSource;
        try {
            dataSource = getDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String statement = "SELECT LAST_UPDATE FROM TRACKED_ITEMS WHERE UUID = ?";

        MysqlDataSource finalDataSource = dataSource;
        Timestamp lastUpdateTimestamp = null;
        Connection conn;
        try {
            conn = finalDataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(1, uuid.toString());
            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if the result set has data
            if (resultSet.next()) {
                // Retrieve the last update timestamp from the result set
                lastUpdateTimestamp = resultSet.getTimestamp("LAST_UPDATE");
                // Print or use the timestamp as needed
                System.out.println("Last Update Timestamp for UUID " + uuid.toString() + ": " + lastUpdateTimestamp);
            } else {
                System.out.println("No data found for UUID: " + uuid.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        return lastUpdateTimestamp;
    }
    public static List<InventoryLocation> getLastInventories(UUID uuid){
        MysqlDataSource dataSource = null;
        try {
            dataSource = getDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sqlSelectTrackedItem= "SELECT * FROM SAVED_ITEMS WHERE UUID = " + uuid.toString();
        MysqlDataSource finalDataSource = dataSource;

        final String[] res = new String[0];
        Bukkit.getScheduler().runTaskAsynchronously(UltimateTracker.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = finalDataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sqlSelectTrackedItem);
                    ResultSet rs = ps.executeQuery(); {
                        while (rs.next()) {
                            res[0] = rs.getString("LAST_UPDATE");
                        }


                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // handle the exception
                }
            }


        });
        return null;
    }
    public static void update(UUID uuid, Timestamp newDate) {

        MysqlDataSource dataSource;
        try {
            dataSource = getDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String statement = "UPDATE TRACKED_ITEMS SET LAST_UPDATE = ? WHERE UUID = ?";

        MysqlDataSource finalDataSource = dataSource;
        Bukkit.getScheduler().runTaskAsynchronously(UltimateTracker.getInstance(), new Runnable() {
            @Override
            public void run() {

                Connection conn;
                try {
                    conn = finalDataSource.getConnection();
                    PreparedStatement preparedStatement = conn.prepareStatement(statement);
                    preparedStatement.setTimestamp(1, newDate);
                    preparedStatement.setString(2, uuid.toString());

                    int i = preparedStatement.executeUpdate();
                    if (i > 0) {
                        System.out.println("ROW UPDATED");
                    } else {
                        System.out.println("ROW NOT UPDATED");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }


            }
        });


    }

    public static boolean isDuplicated(TrackedItem item){
        Timestamp databaseTimestamp = Database.getLastUpdate(item.getOriginalID());
        Timestamp itemTimestamp = item.getLastUpdateItem();
        System.out.println("dtb ts : " + databaseTimestamp);
        System.out.println("item ts : " + itemTimestamp);
        return itemTimestamp.before(databaseTimestamp);
    }
}

