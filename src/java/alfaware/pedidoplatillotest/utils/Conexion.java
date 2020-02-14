//Clase estatica para el manejo de conexiones a base de datos
package alfaware.pedidoplatillotest.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author Gustavo Alonso González Benavente
 */
public class Conexion {

    private static Connection conn = null;

    static public Connection getConexion() throws Exception {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:/CAMBIAR_NOMBRE_JNDI");
        conn = ds.getConnection();
        return conn;
    }

    static public void closeConexion() {
        closeConexion(null, null, null);
    }

    static public void closeConexion(PreparedStatement ps) {
        closeConexion(ps, null, null);
    }

    static public void closeConexion(CallableStatement cs) {
        closeConexion(null, null, cs);
    }

    static public void closeConexion(PreparedStatement ps, ResultSet rs) {
        closeConexion(ps, rs, null);
    }

    static public void closeConexion(PreparedStatement ps, ResultSet rs, CallableStatement cs) {
        try {
            rs.close();
        } catch (Exception e) {
        }
        try {
            ps.close();
        } catch (Exception e) {
        }
        try {
            cs.close();
        } catch (Exception e) {
        }
        try {
            conn.close();
            conn = null;
        } catch (Exception e) {
        }
    }
}
