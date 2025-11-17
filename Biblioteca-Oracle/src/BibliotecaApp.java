import java.sql.*;
import java.util.Scanner;

public class BibliotecaApp {
    // Conexión al esquema 'biblioteca'
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "biblioteca";
    private static final String PASSWORD = "oracle";

    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                System.out.println("✅ Conectado a Oracle como 'biblioteca'.");

                // Mostrar menú de opciones
                mostrarMenu(conn);

            }

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC no encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error de base de datos:");
            e.printStackTrace();
        }
    }

    // Método para mostrar un menú de operaciones CRUD y transacciones
    private static void mostrarMenu(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- Menú Biblioteca ---");
            System.out.println("1. Consultar préstamos activos (JOIN)");
            System.out.println("2. Insertar nuevo socio (CREATE)");
            System.out.println("3. Actualizar disponibilidad de un libro (UPDATE)");
            System.out.println("4. Eliminar un préstamo (DELETE)");
            System.out.println("5. Procesar nuevo préstamo (Transacción)");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");
            opcion = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    consultarPrestamosActivos(conn);
                    break;
                case 2:
                    insertarSocio(conn, sc);
                    break;
                case 3:
                    actualizarDisponibilidadLibro(conn, sc);
                    break;
                case 4:
                    eliminarPrestamo(conn, sc);
                    break;
                case 5:
                    procesarNuevoPrestamoSimplificado(conn, sc);
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 0);

        sc.close();
    }

    // 1. Consulta avanzada: JOIN múltiple
    private static void consultarPrestamosActivos(Connection conn) throws SQLException {
        String sql = """
            SELECT s.nombre, s.apellido, l.titulo, p.fecha_prestamo
            FROM Prestamo p
            JOIN Socio s ON p.id_socio = s.id_socio
            JOIN Libro l ON p.isbn = l.isbn
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Préstamos activos ---");
            while (rs.next()) {
                System.out.printf("📚 %s %s | '%s' | Fecha: %s%n",
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("titulo"),
                        rs.getDate("fecha_prestamo")
                );
            }
        }
    }

    // 2. CRUD: CREATE - Insertar nuevo socio
    private static void insertarSocio(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Nombre del socio: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido del socio: ");
        String apellido = sc.nextLine();
        System.out.print("Email del socio: ");
        String email = sc.nextLine();
        System.out.print("Teléfono del socio: ");
        String telefono = sc.nextLine();

        conn.setAutoCommit(false); // Desactivar auto-commit

        try {
            String sql = "INSERT INTO Socio (id_socio, nombre, apellido, email, telefono) VALUES (seq_socio.NEXTVAL, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.setString(2, apellido);
                stmt.setString(3, email);
                stmt.setString(4, telefono);
                stmt.executeUpdate();
            }
            conn.commit();
            System.out.println("✅ Socio insertado correctamente.");
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("❌ Error al insertar socio: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true); // Restaurar auto-commit
        }
    }

    // 3. CRUD: UPDATE - Actualizar disponibilidad de un libro (con búsqueda y selección por título)
    private static void actualizarDisponibilidadLibro(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Título del libro a actualizar (o parte del título): ");
        String busqueda = sc.nextLine();

        conn.setAutoCommit(false); // Desactivar auto-commit

        try {
            // Buscar libros que coincidan
            String sqlBuscar = "SELECT ROWNUM AS num, isbn, titulo, disponible FROM Libro WHERE UPPER(titulo) LIKE ?";
            String isbnSeleccionado = null;
            String tituloSeleccionado = null;

            try (PreparedStatement stmt = conn.prepareStatement(sqlBuscar)) {
                stmt.setString(1, "%" + busqueda.toUpperCase() + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("\n--- Resultados de búsqueda ---");
                    boolean encontrado = false;
                    while (rs.next()) {
                        encontrado = true;
                        System.out.printf("%d. %s | ISBN: %s | Disponible: %s%n",
                                rs.getInt("num"),
                                rs.getString("titulo"),
                                rs.getString("isbn"),
                                rs.getString("disponible"));
                    }
                    if (!encontrado) {
                        System.out.println("⚠️ No se encontraron libros que coincidan.");
                        return;
                    }
                }
            }

            System.out.print("Selecciona el número del libro que deseas actualizar: ");
            int seleccion = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            // Volver a ejecutar la consulta para obtener el ISBN del número seleccionado
            try (PreparedStatement stmt = conn.prepareStatement(sqlBuscar)) {
                stmt.setString(1, "%" + busqueda.toUpperCase() + "%");
                try (ResultSet rs2 = stmt.executeQuery()) {
                    while (rs2.next()) {
                        if (rs2.getInt("num") == seleccion) {
                            isbnSeleccionado = rs2.getString("isbn");
                            tituloSeleccionado = rs2.getString("titulo");
                            break;
                        }
                    }
                }
            }

            if (isbnSeleccionado == null) {
                System.out.println("⚠️ Selección inválida.");
                return;
            }

            System.out.printf("Seleccionaste: %s (ISBN: %s)%n", tituloSeleccionado, isbnSeleccionado);
            System.out.print("¿Nuevo estado de disponibilidad? (S/N): ");
            String disponible = sc.nextLine().toUpperCase();

            String sqlActualizar = "UPDATE Libro SET disponible = ? WHERE isbn = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sqlActualizar)) {
                stmt.setString(1, disponible);
                stmt.setString(2, isbnSeleccionado);
                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    conn.commit();
                    System.out.println("✅ Libro actualizado correctamente.");
                } else {
                    System.out.println("⚠️ No se encontró el libro con ISBN: " + isbnSeleccionado);
                    conn.rollback(); // Revertir si no se encontró
                }
            }

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("❌ Error al actualizar libro: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true); // Restaurar auto-commit
        }
    }

    // 4. CRUD: DELETE - Eliminar un préstamo
    private static void eliminarPrestamo(Connection conn, Scanner sc) throws SQLException {
        System.out.print("ID del préstamo a eliminar: ");
        int idPrestamo = sc.nextInt();

        conn.setAutoCommit(false); // Desactivar auto-commit

        try {
            String sql = "DELETE FROM Prestamo WHERE id_prestamo = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idPrestamo);
                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    conn.commit();
                    System.out.println("✅ Préstamo eliminado correctamente.");
                } else {
                    System.out.println("⚠️ No se encontró el préstamo con ID: " + idPrestamo);
                    conn.rollback(); // Revertir si no se encontró
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("❌ Error al eliminar préstamo: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true); // Restaurar auto-commit
        }
    }

    // 5. Transacción: Procesar nuevo préstamo (simplificado con selección por número)
    private static void procesarNuevoPrestamoSimplificado(Connection conn, Scanner sc) throws SQLException {
        System.out.print("ID del socio: ");
        int idSocio = sc.nextInt();

        conn.setAutoCommit(false); // Desactivar auto-commit

        try {
            // Mostrar libros disponibles con un número
            String sqlLibros = "SELECT ROWNUM AS num, isbn, titulo, autor FROM Libro WHERE disponible = 'S'";
            String isbnSeleccionado = null;

            try (PreparedStatement stmt = conn.prepareStatement(sqlLibros);
                 ResultSet rs = stmt.executeQuery()) {

                System.out.println("\n--- Libros disponibles ---");
                while (rs.next()) {
                    System.out.printf("%d. %s - %s (ISBN: %s)%n",
                            rs.getInt("num"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("isbn"));
                }

                System.out.print("Selecciona el número del libro que desea prestar: ");
                int seleccion = sc.nextInt();

                // Volver a recorrer para encontrar el ISBN correspondiente al número
                stmt.clearParameters();
                try (ResultSet rs2 = stmt.executeQuery()) {
                    while (rs2.next()) {
                        if (rs2.getInt("num") == seleccion) {
                            isbnSeleccionado = rs2.getString("isbn");
                            break;
                        }
                    }
                }
            }

            if (isbnSeleccionado == null) {
                System.out.println("⚠️ Selección inválida o libro no disponible.");
                conn.rollback(); // Revertir si no se pudo seleccionar
                return;
            }

            // 1. Insertar nuevo préstamo
            String insertPrestamo = "INSERT INTO Prestamo (id_prestamo, id_socio, isbn, fecha_prestamo) VALUES (seq_prestamo.NEXTVAL, ?, ?, SYSDATE)";
            try (PreparedStatement stmt = conn.prepareStatement(insertPrestamo)) {
                stmt.setInt(1, idSocio);
                stmt.setString(2, isbnSeleccionado);
                stmt.executeUpdate();
            }

            // 2. Actualizar estado del libro como no disponible
            String updateLibro = "UPDATE Libro SET disponible = 'N' WHERE isbn = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateLibro)) {
                stmt.setString(1, isbnSeleccionado);
                stmt.executeUpdate();
            }

            // Confirmar la transacción
            conn.commit();
            System.out.printf("✅ Préstamo procesado: Libro '%s' marcado como no disponible.%n", isbnSeleccionado);

        } catch (SQLException e) {
            // Revertir la transacción en caso de error
            conn.rollback();
            System.err.println("❌ Error al procesar préstamo. Transacción revertida. " + e.getMessage());
        } finally {
            conn.setAutoCommit(true); // Restaurar auto-commit
        }
    }
}