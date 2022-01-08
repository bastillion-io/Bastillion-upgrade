/**
 * Copyright 2017 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file stmtcept in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either stmtpress or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.bastillion.manage.util.AppConfig;
import io.bastillion.manage.util.DBUtils;
import io.bastillion.manage.util.H2Upgrade;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

public class Upgrade {

    public static void main(String[] args) {
        if (args.length != 1 || !args[0].contains("BastillionConfig.properties")) {
            System.err.println("Must run command as: java -jar bastillion-upgrade.jar <whatever path>/BastillionConfig.properties");
            System.exit(1);
        }
        DBUtils.DB_PATH = args[0].replaceAll("BastillionConfig.properties", "");
        String user = AppConfig.getProperty("dbUser");
        String password = null;
        if (AppConfig.isPropertyEncrypted("dbPassword")) {
            password = AppConfig.decryptProperty("dbPassword");
        } else {
            password = AppConfig.getProperty("dbPassword");
        }
        String connectionURL = AppConfig.getProperty("dbConnectionURL");
        if (connectionURL != null && connectionURL.contains("CIPHER=")) {
            password = "filepwd " + password;
        }
        assert connectionURL != null;
        connectionURL = connectionURL.replaceAll("keydb/bastillion", DBUtils.DB_PATH + "keydb/bastillion");
        System.out.println("connectionURL : " + connectionURL);

        Properties info = new Properties();
        info.setProperty("user", user);
        info.setProperty("password", password);

        try {
            H2Upgrade.upgrade(connectionURL, info, 200);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        Connection con = DBUtils.getConn();
        System.out.println(con);
        if (con != null) {

            Statement stmt;
            try {
                stmt = con.createStatement();
                stmt.executeUpdate("ALTER TABLE system RENAME COLUMN \"USER\" to USERNAME");
                DBUtils.closeStmt(stmt);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }

            try {
                stmt = con.createStatement();
                stmt.executeUpdate("ALTER TABLE system RENAME COLUMN \"user\" to USERNAME");
                DBUtils.closeStmt(stmt);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }

            try {
                stmt = con.createStatement();
                stmt.executeUpdate("ALTER TABLE terminal_log RENAME COLUMN \"USER\" to USERNAME");
                DBUtils.closeStmt(stmt);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }

            try {
                stmt = con.createStatement();
                stmt.executeUpdate("ALTER TABLE terminal_log RENAME COLUMN \"user\" to USERNAME");
                DBUtils.closeStmt(stmt);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }

            DBUtils.closeConn(con);

            System.out.println("Upgrade successful");
        }
    }
}
