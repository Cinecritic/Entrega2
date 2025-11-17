-- ACTUALIZAR
UPDATE Libro SET disponible = 'N' WHERE isbn = '978-0-6-112008-4';
COMMIT;

-- ELIMINAR
DELETE FROM Prestamo WHERE id_prestamo = 1;
COMMIT;

-- Eliminar socio (solo si no tiene préstamos o con ON DELETE CASCADE)
-- Si no tienes CASCADE, primero elimina los préstamos:
DELETE FROM Prestamo WHERE id_socio = 2;
DELETE FROM Socio WHERE id_socio = 2;
COMMIT;
--Crear secuencias:
-- Crear secuencia para Socio
CREATE SEQUENCE seq_socio
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

-- Crear secuencia para Prestamo
CREATE SEQUENCE seq_prestamo
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;