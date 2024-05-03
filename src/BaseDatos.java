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
            // jdbc:mariadb://localhost:3306/test
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

    public boolean crearTabla(String nombreTabla) {
        try {
            conexion.createStatement().execute("CREATE TABLE " + nombreTabla + " (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(50), " +
                    "descripcion VARCHAR(200), " +
                    "cuando_completada DATETIME, " +
                    "completada BOOLEAN DEFAULT FALSE, " +
                    "creada DATETIME DEFAULT CURRENT_TIMESTAMP)");
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

    public void insertar(String nombreTarea,String descripcionTarea) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement("INSERT INTO tarea (nombre, descripcion) VALUES (?, ?)");
            preparedStatement.setString(1, nombreTarea);
            preparedStatement.setString(2, descripcionTarea);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void actualizar() {

    }

    public void eliminar() {

    }

    public void consultar() {

    }

}
