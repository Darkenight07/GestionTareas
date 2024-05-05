import java.io.FileReader;
import java.io.IOException;
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
    public static String SQL_COMPROBAR_CONTRASENA = "SELECT COUNT(*) FROM usuario WHERE usuario = ? AND contrasena = ?";
    public static String SQL_OBTENER_ID_USUARIO = "SELECT id FROM usuario WHERE usuario = ? AND contrasena = ?";
    public static String SQL_DEVOLVER_CONTRASENA = "SELECT contrasena FROM usuario WHERE usuario = ?";

    private static int idUsuario;

    public static void main(String[] args) {

        basedatos = new BaseDatos("config/servidor.conf");
        basedatos.conectar();
        opcionesPosibles();

        while (true) {
            if (idUsuario == 0) {
                menu();
            } else {
                System.out.println("Administracion de tareas");
            }
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
                try {
                    iniciarSesion();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error al iniciar sesion");
                }
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
        String usuario = comprobarUsuario(false);
        String contrasena = comprobarContrasena(false,"");

        if (basedatos.insertar(SQL_CREAR_USUARIO, nombre, usuario, encriptarContrasena(contrasena))) {
            System.out.println("Usuario registrado correctamente");
        } else {
            System.out.println("Error al registrar el usuario");
        }
    }

    public static String comprobarUsuario(boolean debeExistir) {
        Scanner sc = new Scanner(System.in);
        String usuario = "";
        boolean usuarioValido = false;

        while (!usuarioValido) {
            System.out.print("Introduce tu usuario: ");
            usuario = sc.nextLine();
            boolean existeUsuario = basedatos.verificarUsuario(SQL_COMPROBAR_USUARIO, usuario);
            if (debeExistir) {
                if (existeUsuario) {
                    usuarioValido = true;
                } else {
                    System.out.println("El usuario no existe, por favor introduce un usuario que exista");
                }
            } else {
                if (existeUsuario) {
                    System.out.println("El usuario ya existe, por favor introduce otro usuario");
                } else {
                    usuarioValido = true;
                }
            }
        }
        return usuario;
    }

    public static String comprobarContrasena(boolean debeCoincidir,String usuario) {
        Scanner sc = new Scanner(System.in);
        String contrasena = "";
        boolean contrasenaValida = false;

        while (!contrasenaValida) {
            System.out.print("Introduce tu contrasena: ");
            contrasena = sc.nextLine();
            if (debeCoincidir) {
                boolean coincideContrasena = basedatos.verificarContrasena(SQL_COMPROBAR_CONTRASENA, usuario, encriptarContrasena(contrasena));
                if (coincideContrasena) {
                    contrasenaValida = true;
                } else {
                    System.out.println("La contrasena no coincide con el usuario, por favor introduce la contrasena correcta");
                }
            } else {
                if (contrasena.isEmpty() || contrasena.length() < 8){
                    System.out.println("Has introducido una contrasena vacia " +
                            "o menor a 8 caracteres, por favor introduce una contrasena valida");
                } else {
                    contrasenaValida = true;
                }
            }
        }
        return contrasena;
    }


    public static void iniciarSesion() throws Exception {
        String usuario = comprobarUsuario(true);
        String contrasena = comprobarContrasena(true, usuario);

        String contrasenaEncriptada = basedatos.obtenerContrasenaEncriptada(SQL_DEVOLVER_CONTRASENA,usuario);
        String contrsenaDesencriptada = desencriptarContrasena(contrasenaEncriptada);

        if (contrasena.equals(contrsenaDesencriptada)) {
            idUsuario = basedatos.obtenerIdUsuario(SQL_OBTENER_ID_USUARIO, usuario,contrsenaDesencriptada);
        } else {
            idUsuario = -1;
        }

        if (idUsuario != -1) {
            System.out.println("Inicio de sesion correcto, idUsuario: " + idUsuario);
        } else {
            System.out.println("Error al iniciar sesion, ha ocurrido un error inesperado");
        }
    }

    public static String encriptarContrasena(String contrasena) {
        String contrasenaEncriptada = "";
        try {
            String rutaFicheroClaves = "config/claves_vector_encriptador.conf";
            contrasenaEncriptada = Encriptador.encrypt(Encriptador.devolverKeyVector("key", rutaFicheroClaves ),
                    Encriptador.devolverKeyVector("iv", rutaFicheroClaves ), contrasena);
            return contrasenaEncriptada;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al encriptar la contrasena");
        }
        return contrasena;
    }

    public static String desencriptarContrasena(String contrasena) {
        String contrasenaDesencriptada = "";
        try {
            String rutaFicheroClaves = "config/claves_vector_encriptador.conf";
            contrasenaDesencriptada = Encriptador.decrypt(Encriptador.devolverKeyVector("key", rutaFicheroClaves ),
                    Encriptador.devolverKeyVector("iv", rutaFicheroClaves ), contrasena);
            return contrasenaDesencriptada;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al desencriptar la contrasena");
        }
        return contrasena;
    }
}
