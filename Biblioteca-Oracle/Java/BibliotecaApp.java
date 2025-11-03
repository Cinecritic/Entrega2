import java.sql.*;

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

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC no encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error de base de datos:");
            e.printStackTrace();
        }
    }
}