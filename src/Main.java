import java.util.Scanner;

public class Main {
    public static BaseDatos basedatos;

    public static String SQL_CREAR_TABLA_TAREA = "CREATE TABLE " + "tarea" + " (" +
            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
            "id_usuario INT," +
            "nombre VARCHAR(50), " +
            "descripcion VARCHAR(200), " +
            "cuando_completada DATETIME, " +
            "completada BOOLEAN DEFAULT FALSE, " +
            "creada DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (id_usuario) REFERENCES usuario(id))";

    public static String SQL_CREAR_TABLA_USUARIO = "CREATE TABLE " + "usuario" + " (" +
            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
            "nombre VARCHAR(30), " +
            "usuario VARCHAR(25) UNIQUE, " +
            "contrasena VARCHAR(150))";

    public static String SQL_CREAR_USUARIO = "INSERT INTO usuario (nombre, usuario, contrasena) " +
            "VALUES (?, ?, ?)";

    public static String SQL_COMPROBAR_USUARIO = "SELECT COUNT(*) FROM usuario WHERE usuario = ?";


    public static void main(String[] args) {

        basedatos = new BaseDatos("config/servidor.conf");
        basedatos.conectar();
        opcionesPosibles();

        while (true) {
            menu();
        }

    }

    public static void opcionesPosibles() {
        if (!basedatos.comprobarSiExisteBaseDatos("tareas")) {
            System.out.println("La base de datos tareas no existe intentando creacion de la base de datos...");
            if (!basedatos.crearBaseDatos("tareas")) {
                System.out.println("Error al crear la base de datos tareas. No se ha podido crear, verifica los datos de conexion.");
                return;
            }
            System.out.println("Base de datos tareas creada");
        }

        System.out.println("La base de datos test existe");
        basedatos.usarBaseDatos("tareas");

        if (!basedatos.comprobarSiExisteTabla("usuario")) {
            System.out.println("La tabla usuario no existe intentando creacion de la tabla...");
            if (!basedatos.crearTabla(SQL_CREAR_TABLA_USUARIO)) {
                System.out.println("Error al crear la tabla usuario. No se ha podido crear, verifica los datos de conexion.");
                return;
            }
            System.out.println("Tabla usuario creada");
        }

        System.out.println("La tabla usuario existe");

        if (!basedatos.comprobarSiExisteTabla("tarea")) {
            System.out.println("La tabla tarea no existe intentando creacion de la tabla...");
            if (!basedatos.crearTabla(SQL_CREAR_TABLA_TAREA)) {
                System.out.println("Error al crear la tabla tarea. No se ha podido crear, verifica los datos de conexion.");
                return;
            }
            System.out.println("Tabla tarea creada");
        }

        System.out.println("La tabla tarea existe");
    }

    public static void menu() {
        final int INICIAR_SESION = 1;
        final int REGISTRARSE = 2;
        final int SALIR = 3;

        Scanner sc = new Scanner(System.in);
        System.out.println("1. Iniciar sesion");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");

        System.out.print("Introduce una opcion: ");
        int opcion = sc.nextInt();

        switch (opcion) {
            case INICIAR_SESION:
                break;
            case REGISTRARSE:
                registrarse();
                break;
            case SALIR:
                basedatos.cerrarConexion();
                System.exit(0);
                break;
        }
    }

    public static void registrarse() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce tu nombre: ");
        String nombre = sc.nextLine();
        String usuario = "";
        boolean usuarioValido = false;
        while (!usuarioValido) {
            System.out.print("Introduce tu usuario: ");
            usuario = sc.nextLine();
            if (basedatos.verificarUsuario(SQL_COMPROBAR_USUARIO, usuario)) {
                System.out.println("El usuario ya existe, por favor introduce otro usuario");
            } else {
                usuarioValido = true;
            }
        }
        String contrasena = "";
        boolean contrasenaValida = false;

        while (!contrasenaValida) {
            System.out.print("Introduce tu contrasena: ");
            contrasena = sc.nextLine();
            if (contrasena.isEmpty() || contrasena.length() < 8){
                System.out.println("Has introducido una contrasena vacia " +
                        "o menor a 8 caracteres, por favor introduce una contrasena valida");
            } else {
                contrasenaValida = true;
            }
        }
        if (basedatos.insertar(SQL_CREAR_USUARIO, nombre, usuario, contrasena)) {
            System.out.println("Usuario registrado correctamente");
        } else {
            System.out.println("Error al registrar el usuario");
        }
    }
}
