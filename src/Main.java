import java.sql.SQLException;

public class Main {
    public static BaseDatos basedatos;

    public static void main(String[] args) throws SQLException {

        basedatos = new BaseDatos("config/servidor.conf");
        basedatos.conectar();
        opcionesPosibles();

    }

    public static void opcionesPosibles() {
        if (basedatos.comprobarSiExisteBaseDatos("tareas")) {
            System.out.println("La base de datos test existe");
            basedatos.usarBaseDatos("tareas");
            if (basedatos.comprobarSiExisteTabla("tarea")) {
                System.out.println("La tabla tarea existe");
            } else {
                System.out.println("La tabla tarea no existe intentando creacion de la tabla...");
                if (basedatos.crearTabla("tarea")) {
                    System.out.println("Tabla tarea creada");
                } else {
                    System.out.println("Error al crear la tabla tarea. No se ha podido crear, verifica los datos de conexion.");
                }
            }
        } else {
            System.out.println("La base de datos test no existe intentando creacion de la base de datos...");
            if (basedatos.crearBaseDatos("tareas")) {
                System.out.println("Base de datos tareas creada");
                basedatos.usarBaseDatos("tareas");
                if (basedatos.comprobarSiExisteTabla("tarea")) {
                    System.out.println("La tabla tarea existe");
                } else {
                    System.out.println("La tabla tarea no existe intentando creacion de la tabla...");
                    if (basedatos.crearTabla("tarea")) {
                        System.out.println("Tabla tarea creada");
                    } else {
                        System.out.println("Error al crear la tabla tarea. No se ha podido crear, verifica los datos de conexion.");
                    }
                }
            } else {
                System.out.println("Error al crear la base de datos tareas. No se ha podido crear, verifica los datos de conexion.");
            }
        }
    }
}
