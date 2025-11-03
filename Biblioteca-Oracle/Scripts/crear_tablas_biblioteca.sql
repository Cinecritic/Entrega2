CREATE TABLE Socio (
    id_socio NUMBER PRIMARY KEY,
    nombre VARCHAR2(50) NOT NULL,
    apellido VARCHAR2(50) NOT NULL,
    email VARCHAR2(100),
    telefono VARCHAR2(20)
);

CREATE TABLE Libro (
    isbn VARCHAR2(20) PRIMARY KEY,
    titulo VARCHAR2(200) NOT NULL,
    autor VARCHAR2(100) NOT NULL,
    anio_publicacion NUMBER(4),
    disponible CHAR(1) DEFAULT 'S' CHECK (disponible IN ('S', 'N'))
);

CREATE TABLE Prestamo (
    id_prestamo NUMBER PRIMARY KEY,
    id_socio NUMBER NOT NULL,
    isbn VARCHAR2(20) NOT NULL,
    fecha_prestamo DATE DEFAULT SYSDATE,
    fecha_devolucion DATE,
    CONSTRAINT fk_socio FOREIGN KEY (id_socio) REFERENCES Socio(id_socio),
    CONSTRAINT fk_libro FOREIGN KEY (isbn) REFERENCES Libro(isbn)
);