/**
 * Copyright 2017 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.keybox.manage.util.DBUtils;
import java.sql.Connection;
import java.sql.Statement;

public class Upgrade {
    public Upgrade() {
    }

    public static void main(String[] args) {
        if (args.length != 1 || !args[0].contains("KeyBoxConfig.properties")) {
            System.err.println("Must run command as: java -jar keybox-upgrade-2_90.jar <whatever path>/KeyBoxConfig.properties");
            System.exit(1);
        }

        DBUtils.DB_PATH = args[0].replaceAll("KeyBoxConfig.properties", "");
        Connection con = DBUtils.getConn();
        if (con != null) {
            try {
                Statement stmt = con.createStatement();
                stmt.executeUpdate("alter table session_log add column ip_address varchar");
                DBUtils.closeStmt(stmt);
            } catch (Exception ex) {
                System.err.println("Upgrade failed");
                ex.printStackTrace();
                DBUtils.closeConn(con);
                System.exit(1);
            } finally {
                DBUtils.closeConn(con);
            }

            System.out.println("Upgrade successful");
        }

    }
}
