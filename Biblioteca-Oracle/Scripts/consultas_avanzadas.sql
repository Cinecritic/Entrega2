-- JOIN múltiple
SELECT s.nombre, s.apellido, l.titulo, p.fecha_prestamo
FROM Socio s
LEFT JOIN Prestamo p ON s.id_socio = p.id_socio
LEFT JOIN Libro l ON p.isbn = l.isbn;

-- Subconsulta: socios que tomaron libros de García Márquez
SELECT DISTINCT s.nombre, s.apellido
FROM Socio s
JOIN Prestamo p ON s.id_socio = p.id_socio
WHERE p.isbn IN (
    SELECT isbn FROM Libro WHERE autor = 'Gabriel García Márquez'
);

-- Funciones agregadas: préstamos por socio
SELECT s.nombre, s.apellido, COUNT(p.id_prestamo) AS total_prestamos
FROM Socio s
JOIN Prestamo p ON s.id_socio = p.id_socio
GROUP BY s.id_socio, s.nombre, s.apellido
ORDER BY total_prestamos DESC;

-- Libros más prestados
SELECT *
FROM (
    SELECT l.titulo, COUNT(*) AS veces_prestado
    FROM Libro l
    JOIN Prestamo p ON l.isbn = p.isbn
    GROUP BY l.titulo
    ORDER BY veces_prestado DESC
)
WHERE ROWNUM <= 3;