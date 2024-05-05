import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class BaseDatos {

    private String rutaFicheroConfiguracion;
    private Connection conexion;

    public BaseDatos(String rutaFicheroConfiguracion) {
        this.rutaFicheroConfiguracion = rutaFicheroConfiguracion;
    }

    public boolean conectar() {

        try {
            Properties p =  new Properties();
            p.load(new FileReader(rutaFicheroConfiguracion));
            conexion = DriverManager.getConnection("jdbc:" +
                    p.getProperty("tiposervidor") + "://"+ p.getProperty("servidor")+":" + p.getProperty("puerto") +
                    "/"+p.getProperty("basedatos"), p.getProperty("usuario"), p.getProperty("contrasena"));
            return true;
        }catch (SQLException | FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean crearBaseDatos(String nombreBaseDatos) {
        try {
            conexion.createStatement().execute("CREATE DATABASE " + nombreBaseDatos);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean usarBaseDatos(String nombreBaseDatos) {
        try {
            conexion.createStatement().execute("USE " + nombreBaseDatos);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean comprobarSiExisteBaseDatos(String nombreBaseDatos) {
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW DATABASES LIKE '" + nombreBaseDatos + "'");
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean crearTabla(String sql) {
        try {
            conexion.createStatement().execute(sql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean comprobarSiExisteTabla(String nombreTabla) {
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW TABLES LIKE '" + nombreTabla + "'");
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public void cerrarConexion() {
        try {
            conexion.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insertar(String sql,String nombre,String usuario,String contrasena) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, usuario);
            preparedStatement.setString(3, contrasena);
            if (!preparedStatement.execute()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean verificarUsuario(String sql, String usuario) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getInt(1) > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }


    public void actualizar() {

    }

    public void eliminar() {

    }

    public void consultar() {

    }

}
