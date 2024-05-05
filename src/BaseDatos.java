import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

public class BaseDatos {

    private final String rutaFicheroConfiguracion;
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
            return resultSet.next();
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
            return resultSet.next();
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
            return !preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean verificarUsuario(String sql, String usuario) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean verificarContrasena(String sql, String usuario, String contrasena) {
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, contrasena);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            } else {
                return false;
            }
        } catch (SQLException var6) {
            return false;
        }
    }

    public int obtenerIdUsuario(String sql, String usuario, String contrasena) {
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, contrasena);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0 ? resultSet.getInt(1) : -1;
        } catch (SQLException e) {
            return -1;
        }
    }

    public String obtenerContrasenaEncriptada(String sql, String usuario) {
        try {
            PreparedStatement preparedStatement = this.conexion.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? resultSet.getString(1) : "";
        } catch (SQLException var5) {
            return "";
        }
    }

    public boolean insertarTarea(String sql,int idUsuario,String nombreTarea,String descripcion) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setInt(1, idUsuario);
            preparedStatement.setString(2, nombreTarea);
            preparedStatement.setString(3, descripcion);
            return !preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    public String verTareas(int idUsuario) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement("SELECT * FROM tarea WHERE id_usuario = ?");
            preparedStatement.setInt(1, idUsuario);
            ResultSet resultSet = preparedStatement.executeQuery();
            String tareas = "";
            while (resultSet.next()) {
                tareas += "----------------------\n";
                tareas += "ID Tarea: " + resultSet.getInt(1) + "\n";
                tareas += "Nombre: " + resultSet.getString(3) + "\n";
                tareas += "Descripcion: " + resultSet.getString(4) + "\n";
                tareas += "Completada: " + ((resultSet.getInt(6)==0) ? "No":"Si" ) + "\n";
                tareas+= "Creada: " + resultSet.getDate(7) + " " + resultSet.getTime(7) + "\n";
                tareas += "----------------------\n";
            }
            return tareas;
        } catch (SQLException e) {
            return "";
        }

    }

    public boolean comprobarIDTarea(String sql, int idTarea,int idUsuario) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setInt(1, idTarea);
            preparedStatement.setInt(2,idUsuario);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return resultSet.getInt(1) >= 1;
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }
    // Actualizar tarea para cambiar el nombre y la descripcion
    public boolean actualizarTarea(String sql, String nombre,String descripcion, int idTarea,int idUsuario) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setInt(3, idTarea);
            preparedStatement.setInt(4, idUsuario);
            return !preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }

    }
    // Actualizar tarea para cambiar el estado de completada y la fecha de completada
    public boolean actualizarTarea(String sql, boolean completada,int idTarea,int idUsuario) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setBoolean(1, completada);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(3, idTarea);
            preparedStatement.setInt(4, idUsuario);
            return !preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }

    }
    // Actualizar tarea para cambiar el nombre, la descripcion, el estado de completada y la fecha de completada
    public boolean actualizarTarea(String sql, String nombre,String descripcion, boolean completada,int idTarea,int idUsuario) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, descripcion);
            preparedStatement.setBoolean(3, completada);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(5, idTarea);
            preparedStatement.setInt(6, idUsuario);
            return !preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }

    }

    public boolean eliminarTarea(String sql, int idTarea,int idUsuario) {
        try {
            PreparedStatement preparedStatement = conexion.prepareStatement(sql);
            preparedStatement.setInt(1, idTarea);
            preparedStatement.setInt(2, idUsuario);
            return !preparedStatement.execute();
        } catch (SQLException e) {
            return false;
        }

    }

}
