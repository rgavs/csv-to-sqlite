package csvtosqlite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.hsqldb.*;
import org.hsqldb.jdbc.JDBCConnection;

import static java.lang.System.*;

/**
 * Java application to consume a CSV file, parse the data and insert to a SQLite In-Memory database.
 */
public class App {
    public static void main( String[] args ) throws IOException {
        List<String> values;
        File badFile = null;
        try {
            FileReader fr = new FileReader("data.csv");
            BufferedReader br = new BufferedReader(fr);

            Server server = initServer();
            JDBCConnection conn = initConn(server);

            // Read through CSV file
            String line;
            FileWriter fw = null;
            while ((line = br.readLine()) != null) {
                String insert = "";
                // Attempt to deal with field containing string literal "\'"
                if (line.contains("\'")) {
                    StringBuffer sb = new StringBuffer(line);
                    sb.insert(line.indexOf("\'"), "\'");
                    line = sb.toString();
                }
                values = Arrays.asList(line.split(",(?=([^\"]|\"[^\"]*\")*$)"));
                out.println("Item 4 is: " +values.get(4) + '\n'+'\t'+" and len is " + values.get(4).length());
                Statement st = conn.createStatement();
                String str = String.join("\', \'", values);
                if (!values.contains("")) {
                    insert = "INSERT INTO db_tbl VALUES (\'" + str + "\');";
                    out.println(insert);
                    out.println(st.executeUpdate(insert));
                } else {
                    if (badFile == null) {
                        badFile = initBadFile();
                        fw = new FileWriter(badFile);
                    } else {
                        fw.append(line);
                    }
                }
            }

            conn.close();
            server.shutdown();
            fr.close();
            br.close();
            fw.close();
        }  catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println('\n' + "Application complete.");
        System.exit(0);
    }


    private static Server initServer() {
        // create & start in memory database
        Server server = new Server();
        server.setDatabaseName(0, "data");
        server.setDatabasePath(0, "mem:data");
        server.setPort(9001);
        server.start();
        server.checkRunning(true);
        out.println("Running");
        return server;
    }


    private static JDBCConnection initConn(Server server) throws ClassNotFoundException, SQLException {
        JDBCConnection conn = (JDBCConnection) DriverManager.getConnection("jdbc:hsqldb:hsql:mem/data", "SA", "");
        out.println("Testing");
        Class.forName("org.sqlite.JDBC");

        // Create database table
        int result =0;
        Statement stmt = conn.createStatement();
        String create = "CREATE MEMORY TABLE IF NOT EXISTS db_tbl (a VARCHAR(20) NOT NULL," +
                "b VARCHAR(20) NOT NULL, " +
                "c VARCHAR(40) NOT NULL, "+
                "d CHAR(6) NOT NULL, "+
                "e VARCHAR(2000) NOT NULL, "+
                "f VARCHAR(26) NOT NULL, "+
                "g CHAR(5) NOT NULL, "+
                "h CHAR(5) NOT NULL, "+
                "i CHAR(5) NOT NULL, "+
                "j VARCHAR(30) NOT NULL, "+
                "PRIMARY KEY (c) );";
        result = stmt.executeUpdate(create);

        conn.commit();
        out.println("Table created, result: "+ result);
        out.println("Query: "  + stmt.executeQuery("SELECT * FROM db_tbl;"));
        return conn;
    }


    private static File initBadFile() {
        return new File("bad" + (new Timestamp(currentTimeMillis())).toString() + ".csv");
    }
}
