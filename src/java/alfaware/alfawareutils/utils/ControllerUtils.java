package alfaware.alfawareutils.utils;

import alfaware.pedidoplatillotest.utils.Conexion;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joseluischavez
 */
class ControllerUtils {

    private static final String ID = "id";

    //==========================================================================
    protected static Map<String, Object> get(Object mObject) {

        Map<String, Object> map = new HashMap<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = getSQL(mObject);
            ps = Conexion.getConexion().prepareStatement(sql);
            prepareStatementSelect(ps, mObject);
            rs = ps.executeQuery();
            map = fillResultSet(rs, mObject);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Conexion.closeConexion(ps, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    protected static boolean add(Object mObject) {

        boolean result = false;
        PreparedStatement ps = null;

        try {
            String sql = addSQL(mObject);
            ps = Conexion.getConexion().prepareStatement(sql);
            prepareStatementInsert(ps, mObject);
            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Conexion.closeConexion(ps);
        }

        return result;
    }

    protected static boolean modify(Object mObject) {

        boolean result = false;

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = Conexion.getConexion();
            String sql = updateSQL(mObject);
            ps = con.prepareStatement(sql);
            prepareStatementUpdate(ps, mObject);
            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Conexion.closeConexion(ps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    protected static boolean remove(Object mObject) {
        boolean result = false;
        Connection con = null;
        PreparedStatement ps = null;
        String sql = null;

        try {
            con = Conexion.getConexion();
            sql = deleteSQL(mObject);
            ps = con.prepareStatement(sql);
            prepareStatementDelete(ps, mObject);
            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Conexion.closeConexion(ps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;

    }

    //==========================================================================
    protected static String getSQL(Object mObject) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        StringBuilder builder = new StringBuilder();

        Class aClass = mObject.getClass();
        String className = getClassName(aClass.getSimpleName());

        builder.append(" SELECT * FROM ");
        builder.append(className);
        builder.append(" WHERE 1 = 1 ");

        for (Field f : aClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object o = f.get(mObject);

            if (o != null) {
                if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {

                    builder.append(" AND ");
                    builder.append(f.getName());
                    builder.append(" = ?");
                }
            }

        }

        System.out.println("================================================\\\n" + builder.toString());
        return builder.toString().toLowerCase();

    }

    protected static String addSQL(Object mObject) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

        StringBuilder builder = new StringBuilder();
        fillId(mObject);

        Class aClass = mObject.getClass();
        String className = getClassName(aClass.getSimpleName());

        Integer values = 0;
        builder.append(" INSERT INTO ");
        builder.append(className);
        builder.append(" ( ");

        for (Field f : aClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object o = f.get(mObject);

            if (o != null) {
                if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {

                    builder.append(f.getName());
                    builder.append(",");
                    values++;
                }
            }
        }

        String newBuilder = builder.substring(0, builder.length() - 1);
        builder = new StringBuilder(newBuilder);
        builder.append(") VALUES (");

        for (int i = 0; i < values; i++) {
            builder.append("?,");
        }

        newBuilder = builder.substring(0, builder.length() - 1);
        builder = new StringBuilder(newBuilder);
        builder.append(")");

        System.out.println("================================================\\\n" + builder.toString());
        return builder.toString().toLowerCase();
    }

    protected static String updateSQL(Object mObject) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        StringBuilder builder = new StringBuilder();

        Class aClass = mObject.getClass();
        String className = getClassName(aClass.getSimpleName());

        builder.append(" UPDATE ");
        builder.append(className);
        builder.append(" SET ");

        Field field = aClass.getDeclaredField(ID);
        field.setAccessible(true);
        if (field.get(mObject) != null) {
            for (Field f : aClass.getDeclaredFields()) {
                f.setAccessible(true);
                Object o = f.get(mObject);

                if (o != null && !f.getName().equalsIgnoreCase(ID)) {
                    if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                            || f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                            || f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                            || f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                            || f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {
                        builder.append(f.getName());
                        builder.append(" = ? ,");
                    }
                }
            }

            String newBuilder = builder.substring(0, builder.length() - 1);
            builder = new StringBuilder(newBuilder);

            builder.append(" WHERE id = ? ");
        } else {
            throw new NoSuchFieldException();
        }

        System.out.println("================================================\\\n" + builder.toString());
        return builder.toString().toLowerCase();
    }

    protected static String deleteSQL(Object mObject) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        StringBuilder builder = new StringBuilder();
        Class aClass = mObject.getClass();
        String className = getClassName(aClass.getSimpleName());

        builder.append("DELETE FROM ");
        builder.append(className.toLowerCase());
        builder.append(" WHERE ");
        builder.append(ID);
        builder.append(" = ?");

        return builder.toString();
    }

    //==========================================================================
    protected static void prepareStatementSelect(PreparedStatement ps, Object mObject) throws IllegalArgumentException, SQLException, IllegalAccessException {

        Class aClass = mObject.getClass();

        int index = 1;
        for (Field f : aClass.getDeclaredFields()) {
            f.setAccessible(true);

            if (f.get(mObject) != null) {
                Object o = f.get(mObject);
                if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())) {
                    ps.setObject(index++, o);
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {
                    ps.setObject(index++, o);
                }
            }

        }
    }

    protected static void prepareStatementInsert(PreparedStatement ps, Object mObject) throws IllegalArgumentException, IllegalAccessException, SQLException {

        Class aClass = mObject.getClass();

        int index = 1;
        for (Field f : aClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object fieldValue = f.get(mObject);
            if (fieldValue != null) {
                if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {
                    ps.setObject(index++, fieldValue);
                }
            }
        }
    }

    protected static void prepareStatementUpdate(PreparedStatement ps, Object mObject) throws IllegalArgumentException, IllegalAccessException, SQLException, NoSuchFieldException {
        Class aClass = mObject.getClass();

        Field field = aClass.getDeclaredField(ID);
        field.setAccessible(true);
        if (field.get(mObject) != null) {
            int index = 1;
            for (Field f : aClass.getDeclaredFields()) {
                f.setAccessible(true);
                Object fieldValue = f.get(mObject);
                if (fieldValue != null && !f.getName().equalsIgnoreCase(ID)) {
                    if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                            || f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                            || f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                            || f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                            || f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {
                        ps.setObject(index++, fieldValue);
                    }
                }
            }
            ps.setObject(index, field.get(mObject));
        } else {
            throw new NoSuchFieldException();
        }
    }

    protected static void prepareStatementDelete(PreparedStatement ps, Object mObject) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SQLException {
        Class aClass = mObject.getClass();

        Field field = aClass.getDeclaredField(ID);
        field.setAccessible(true);
        if (field.get(mObject) != null) {
            ps.setObject(1, field.get(mObject));
        } else {
            throw new NoSuchFieldException();
        }
    }

    //==========================================================================
    protected static Map<String, Object> fillResultSet(ResultSet resultSet, Object mObject) throws InstantiationException, IllegalArgumentException, IllegalAccessException, SQLException {

        Map<String, Object> map = new HashMap<>();

        Class aClass = mObject.getClass();

        while (resultSet.next()) {
            Object object = aClass.newInstance();
            for (Field f : aClass.getDeclaredFields()) {
                f.setAccessible(true);
                if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {
                    Object o = resultSet.getObject(f.getName());
                    f.set(object, o);
                }
            }
            map.put(object.toString(), object);
        }

        return map;
    }

    private static String getClassName(String simpleName) {
        StringBuilder builder = new StringBuilder();
        if (simpleName != null && !simpleName.isEmpty()) {
            builder.append(simpleName.charAt(0));
            for (int i = 1; i < simpleName.length(); i++) {
                char c = simpleName.charAt(i);
                if (Character.isUpperCase(c)) {
                    builder.append("_");
                }
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private static void fillId(Object object) {
        if (object != null) {
            String id = UUID.randomUUID().toString();
            try {
                for (Field field : object.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getName().equals("id")) {
                        field.set(object, id);
                        break;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ControllerUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
