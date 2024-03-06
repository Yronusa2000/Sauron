package fr.yronusa.ultimatetracker;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import java.sql.*;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class Database {

    public static FileConfiguration config = UltimateTracker.getInstance().getConfig();
    public static String host = config.getString("database.host");
    public static int port = config.getInt("database.port");
    public static String databaseName = config.getString("database.database");
    public static String username = config.getString("database.user");
    public static String password = config.getString("database.password");
    public static String jdbcURL = "jdbc:mysql://" + host + ":" + port + "/" + databaseName +
            "?useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Paris";

    public static MysqlDataSource getDataSource() throws SQLException {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
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
                    preparedStatement.setTimestamp(3, trackedItem.getLastUpdate());
                    preparedStatement.setString(4, "TODO");
                    preparedStatement.setInt(5, 0);

                    preparedStatement.executeUpdate();
                    System.out.println(preparedStatement);

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
        MysqlDataSource dataSource = null;
        try {
            dataSource = getDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sqlSelectTrackedItem= "SELECT * FROM SAVED_ITEMS WHERE UUID = " + uuid.toString();
        MysqlDataSource finalDataSource = dataSource;

        final Timestamp[] res = new Timestamp[0];
        Bukkit.getScheduler().runTaskAsynchronously(UltimateTracker.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = finalDataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sqlSelectTrackedItem);
                    ResultSet rs = ps.executeQuery(); {
                        while (rs.next()) {
                            res[0] = rs.getTimestamp("LAST_UPDATE");
                        }


                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // handle the exception
                }
            }


        });



        return res[0];
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


    public static void update(UUID id, Timestamp date){

    }

/**

    public static List<TrackedItem> getTrackedItems(int a, int b) throws SQLException {
        // Gets item from A to B in the dtb

        MysqlDataSource dataSource = getDataSource();
        String sqlSelectTrackedItemsFromAtoB= "SELECT * FROM TRACKED_ITEMS WHERE id >= " + a + " AND id <= " + b;
        List<TrackedItem> trackedItems = new ArrayList<>();
        Bukkit.getScheduler().runTaskAsynchronously(UltimateTracker.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (

                        Connection conn = dataSource.getConnection();
                        PreparedStatement ps = conn.prepareStatement(sqlSelectTrackedItemsFromAtoB);


                        ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ItemStack item = ItemSaver.itemStackFromBase64(rs.getString("itemBase64"));
                        Inventory inventory = ItemSaver.inventoryFromString(rs.getString("inventory"));
                        Inventory lastInventory = ItemSaver.inventoryFromString(rs.getString("lastInventory"));
                        int inventoryPlace = rs.getInt("inventoryPlace");
                        UUID originalID = UUID.fromString(rs.getString("originalID"));
                        UUID trackingID = UUID.fromString(rs.getString("trackingID"));


                        TrackedItem t = new TrackedItem(item, inventory, lastInventory, inventoryPlace, originalID, trackingID);
                        trackedItems.add(t);
                    }


                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    // handle the exception
                }

                return null;
            }
        });

        return trackedItems;
    }

    public static List<ItemStack> getSavedItems(int a, int b) throws SQLException {
        MysqlDataSource dataSource = getDataSource();
        String sqlSelectTrackedItemsFromAtoB= "SELECT * FROM SAVED_ITEMS WHERE id >= " + a + " AND id <= " + b;
        List<ItemStack> savedItems = new ArrayList<>();

        Bukkit.getScheduler().runTaskAsynchronously(UltimateTracker.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = dataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sqlSelectTrackedItemsFromAtoB);
                    ResultSet rs = ps.executeQuery(); {
                        while (rs.next()) {
                            System.out.println("0. " + rs.getString("itemBase64"));
                            ItemStack item = ItemSaver.itemStackFromBase64(rs.getString("itemBase64"));
                            System.out.println("2. " + item);
                            savedItems.add(item);
                        }


                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    // handle the exception
                }

            }

        });

        CompletableFuture<List<TrackedItem>> future = CompletableFuture.supplyAsync(() -> {
            TrackedItem data = database.fetchData(playerId); // blocking method
            data.setSomeValue(true);
            return data;
        });

        future.exceptionally(error -> {
            error.printStackTrace();
            return new ErrorData();
        });

        future.thenAccept((data) -> {
            player.sendMessage("The value is " + data.getSomeValue());
        });

// This code still executes first, as the database code is being ran on another thread and won't be available this soon
        player.sendMessage("Fetching data..");

        return savedItems;
    }



    public static void createDatabase() throws SQLException {
        try {
            MysqlDataSource dataSource = getDataSource();
            Connection connection = dataSource.getConnection();

            // Create a statement
            Statement statement = connection.createStatement();

            // Check if the database exists
            String checkExistenceQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + databaseName + "'";
            boolean databaseExists = statement.executeQuery(checkExistenceQuery).next();

            // If the database doesn't exist, create it
            if (!databaseExists) {
                String createDatabaseQuery = "CREATE DATABASE " + databaseName;
                statement.executeUpdate(createDatabaseQuery);
                System.out.println("Database created successfully.");
            } else {
                System.out.println("Database already exists.");
            }

            // Close the statement and connection
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void createTables() throws SQLException {

        // TRY TO CREATE THE "SAVED ITEM" TABLE IN "ULTIMATE TRACKER" DATABASE
        try {
            MysqlDataSource dataSource = getDataSource();
            Connection connection = dataSource.getConnection();

            // Specify the name of the table you want to create
            String tableName = "SAVED_ITEMS";

            // Create a statement
            Statement statement = connection.createStatement();

            // Check if the table exists
            String checkExistenceQuery = "SELECT 1 FROM " + tableName + " LIMIT 1";
            boolean tableExists;
            try{
                tableExists = statement.execute(checkExistenceQuery);
            }
            catch(Exception e){
                tableExists = false;
            }

            // If the table doesn't exist, create it
            if (!tableExists) {
                String createTableQuery = "CREATE TABLE " + tableName + " ("
                        +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +

                        "itemBase64 VARCHAR(5000) NOT NULL, " + // Change this to the appropriate data type for item names
                        ")";
                statement.executeUpdate(createTableQuery);
                System.out.println("Table created successfully.");
            } else {
                System.out.println("Table already exists.");
            }

            // Close the statement and connection
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // TRY TO CREATE THE "TRACKED ITEMS" TABLE IN "ULTIMATE TRACKER" DATABASE
        try {
            MysqlDataSource dataSource = getDataSource();
            Connection connection = dataSource.getConnection();

            // Specify the name of the table you want to create
            String tableName = "TRACKED_ITEMS";

            // Create a statement
            Statement statement = connection.createStatement();

            // Check if the table exists
            String checkExistenceQuery = "SELECT 1 FROM " + tableName + " LIMIT 1";
            boolean tableExists = statement.execute(checkExistenceQuery);

            // If the table doesn't exist, create it
            if (!tableExists) {
                String createTableQuery = "CREATE TABLE " + tableName + " ("
                        +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "item CHAR(500), " +
                        "inventory CHAR(500), " +
                        "lastInventory CHAR(500), " +
                        "inventoryPlace INT," +
                        "originalID CHAR(36) NOT NULL, " +
                        "trackingID CHAR(36) NOT NULL" +
                        ")";
                statement.executeUpdate(createTableQuery);
                System.out.println("Table created successfully.");
            } else {
                System.out.println("Table already exists.");
            }

            // Close the statement and connection
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static MysqlDataSource getDataSource() throws SQLException {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setServerTimezone(TimeZone.getDefault().getID());

        return dataSource;
    }
**/
}

