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
    public static String SQL_CREAR_TAREA = "INSERT INTO tarea (id_usuario, nombre, descripcion) " +
            "VALUES (?, ?, ?)";

    public static String SQL_COMPROBAR_TAREA = "SELECT COUNT(*) FROM tarea WHERE id = ? AND id_usuario = ?";
    public static String SQL_MODIFICAR_TAREA = "UPDATE tarea SET nombre = ?, descripcion = ? WHERE id = ? AND id_usuario = ?";
    public static String SQL_MODIFICAR_TAREA_COMPLETADA = "UPDATE tarea SET completada = ?, cuando_completada = ? WHERE id = ? AND id_usuario = ?";
    public static String SQL_MODFICAR_TAREA_Y_COMPLETA = "UPDATE tarea SET nombre = ?, descripcion = ?, completada = ?, cuando_completada = ? WHERE id = ? AND id_usuario = ?";
    public static String SQL_ELIMINAR_TAREA = "DELETE FROM tarea WHERE id = ? AND id_usuario = ?";


    private static int idUsuario;

    public static void main(String[] args) {

        basedatos = new BaseDatos("config/servidor.conf");
        basedatos.conectar();
        opcionesPosibles();

        while (true) {
            if (idUsuario == 0) {
                menuPrincipal();
            } else {
                menuTareas();
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

    public static void menuPrincipal() {
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
            default:
                System.out.println("Opcion no valida");
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


    public static void iniciarSesion() {
        String usuario = comprobarUsuario(true);
        String contrasena = comprobarContrasena(true, usuario);

        String contrasenaEncriptada = basedatos.obtenerContrasenaEncriptada(SQL_DEVOLVER_CONTRASENA,usuario);
        String contrsenaDesencriptada = desencriptarContrasena(contrasenaEncriptada);

        if (contrasena.equals(contrsenaDesencriptada)) {
            idUsuario = basedatos.obtenerIdUsuario(SQL_OBTENER_ID_USUARIO, usuario,contrasenaEncriptada);
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
            System.out.println("Error al desencriptar la contrasena");
        }
        return contrasena;
    }

    public static void menuTareas() {
        Scanner sc = new Scanner(System.in);

        final int CREAR_TAREA = 1;
        final int VER_TAREAS = 2;
        final int MODIFICAR_TAREA = 3;
        final int ELIMINAR_TAREA = 4;
        final int CERRA_SESION = 5;
        final int SALIR = 6;

        System.out.println("1. Crear tarea");
        System.out.println("2. Ver tareas");
        System.out.println("3. Modificar tarea");
        System.out.println("4. Eliminar tarea");
        System.out.println("5. Cerrar sesion");
        System.out.println("6. Salir");

        System.out.print("Introduce una opcion: ");
        int opcion = sc.nextInt();

        switch (opcion) {
            case CREAR_TAREA:
                crearTarea();
                break;
            case VER_TAREAS:
                verTareas();
                break;
            case MODIFICAR_TAREA:
                modificarTarea();
                break;
            case ELIMINAR_TAREA:
                eliminarTarea();
                break;
            case CERRA_SESION:
                idUsuario = 0;
                break;
            case SALIR:
                basedatos.cerrarConexion();
                System.exit(0);
                break;
            default:
                System.out.println("Opcion no valida");
                break;
        }
    }


    public static void crearTarea() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce el nombre de la tarea: ");
        String nombre = sc.nextLine();
        System.out.print("Introduce la descripcion de la tarea: ");
        String descripcion = sc.nextLine();

        if (basedatos.insertarTarea(SQL_CREAR_TAREA, idUsuario, nombre, descripcion)) {
            System.out.println("Tarea creada correctamente");
        } else {
            System.out.println("Error al crear la tarea");
        }

    }

    private static void verTareas() {
        System.out.println("Tareas que tiene el usuario: \n");
        String tareas = basedatos.verTareas(idUsuario);
        System.out.println(tareas);
    }

    private static void modificarTarea() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Tareas disponibles: \n");
        String tareas = basedatos.verTareas(idUsuario);
        System.out.println(tareas);
        int idTarea = 0;
        boolean existeTarea = false;

        while (!existeTarea) {
            System.out.print("Introduce el id de la tarea que quieres modificar: ");
            idTarea = sc.nextInt();
            existeTarea = basedatos.comprobarIDTarea(SQL_COMPROBAR_TAREA, idTarea, idUsuario);
            if (!existeTarea) {
                System.out.print("El id de la tarea no existe, por favor introduce un id valido\n");
            }
        }


        System.out.println("1. Modificar nombre y descripcion");
        System.out.println("2. Marcar como completada");
        System.out.println("3. Modificar nombre y descripcion y marcar como completada");
        System.out.print("Que quieres hacer?: ");
        int opcion = sc.nextInt();
        sc.nextLine();

        switch (opcion) {
            case 1:
                System.out.print("Introduce el nuevo nombre de la tarea: ");
                String nombre = sc.nextLine();
                System.out.print("Introduce la nueva descripcion de la tarea: ");
                String descripcion = sc.nextLine();
                if (basedatos.actualizarTarea(SQL_MODIFICAR_TAREA, nombre, descripcion, idTarea, idUsuario)) {
                    System.out.println("Tarea modificada correctamente");
                } else {
                    System.out.println("Error al modificar la tarea");
                }
                break;
            case 2:
                System.out.print("Introduce el nuevo estado de la tarea (true/false): ");
                boolean completada = sc.nextBoolean();
                if (basedatos.actualizarTarea(SQL_MODIFICAR_TAREA_COMPLETADA, completada,idTarea, idUsuario)) {
                    System.out.println("Tarea modificada correctamente");
                } else {
                    System.out.println("Error al modificar la tarea");
                }
                break;
            case 3:
                System.out.print("Introduce el nuevo nombre de la tarea: ");
                String nombre2 = sc.nextLine();
                System.out.print("Introduce la nueva descripcion de la tarea: ");
                String descripcion2 = sc.nextLine();
                System.out.print("Introduce el nuevo estado de la tarea (true/false): ");
                boolean completada2 = sc.nextBoolean();
                if (basedatos.actualizarTarea(SQL_MODFICAR_TAREA_Y_COMPLETA, nombre2, descripcion2, completada2, idTarea, idUsuario)) {
                    System.out.println("Tarea modificada correctamente");
                } else {
                    System.out.println("Error al modificar la tarea");
                }
                break;
        }

    }

    private static void eliminarTarea() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Tareas disponibles: \n");
        String tareas = basedatos.verTareas(idUsuario);
        System.out.println(tareas);
        int idTarea = 0;
        boolean existeTarea = false;

        while (!existeTarea) {
            System.out.print("Introduce el id de la tarea que quieres eliminar: ");
            idTarea = sc.nextInt();
            existeTarea = basedatos.comprobarIDTarea(SQL_COMPROBAR_TAREA, idTarea, idUsuario);
            if (!existeTarea) {
                System.out.println("El id de la tarea no existe, por favor introduce un id valido");
            }
        }
        if (basedatos.eliminarTarea(SQL_ELIMINAR_TAREA, idTarea, idUsuario)) {
            System.out.println("Tarea eliminada correctamente");
        } else {
            System.out.println("Error al eliminar la tarea");
        }
    }


}
