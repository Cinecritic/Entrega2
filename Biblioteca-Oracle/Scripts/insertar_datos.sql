INSERT INTO Socio (id_socio, nombre, apellido, email, telefono) 
VALUES (1, 'Ana', 'Lopez', 'ana@example.com', '555-1234');
INSERT INTO Socio (id_socio, nombre, apellido, email, telefono) 
VALUES (2, 'Carlos', 'Mendez', 'carlos@example.com', '555-5678');
INSERT INTO Libro (isbn, titulo, autor, anio_publicacion, disponible) 
VALUES ('978-3-16-148410-0', 'Cien anos de soledad', 'Gabriel Garcia Marquez', 1967, 'S');
INSERT INTO Libro (isbn, titulo, autor, anio_publicacion, disponible) 
VALUES ('978-0-06-112008-4', '1984', 'George Orwell', 1949, 'S');
INSERT INTO Prestamo (id_prestamo, id_socio, isbn, fecha_prestamo, fecha_devolucion) 
VALUES (1, 1, '978-0-06-112008-4', SYSDATE, NULL);
UPDATE Libro 
SET disponible = 'N' 
WHERE isbn = '978-0-06-112008-4';
COMMIT;