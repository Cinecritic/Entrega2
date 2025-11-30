## 🔐 Seguridad y buenas prácticas
- Se creó un **usuario dedicado `biblioteca`** con privilegios limitados.
- **No se usó** `SYSTEM`, `SYS` ni el modo `SYSDBA` para operaciones de la aplicación.
- `SYSDBA` es un rol exclusivo para administradores de base de datos (inicio/detención de Oracle), **no para desarrollo de apps**.

## 📌 Credenciales de conexión
- **Usuario**: `biblioteca`
- **Contraseña**: `oracle`
- **URL JDBC**: `jdbc:oracle:thin:@localhost:1521:XE`

## ▶️ Ejecución
1. Ejecutar `crear_usuario.sql` como `SYSTEM`.
2. Ejecutar scripts de tablas y datos como `biblioteca`.
3. Compilar y ejecutar la app Java con el driver JDBC.

## 🚀 Novedades en esta entrega

- 🔍 **Índices**  
  Se crearon índices en `Prestamo.id_socio` y `Prestamo.fecha_prestamo` para optimizar el rendimiento de consultas frecuentes.

- 🔄 **Transacciones**  
  Se implementó manejo correcto de transacciones en operaciones como `INSERT` y `UPDATE` para garantizar la integridad de los datos.

- 💻 **CRUD desde Java**  
  Se añadieron operaciones completas de creación, lectura, actualización y eliminación directamente desde la aplicación.

- 🎯 **Interfaz mejorada**  
  Ahora es más fácil seleccionar libros por número en lugar de ingresar ISBN manualmente, haciendo la experiencia más intuitiva y segura.

# 📚 BibliotecaApp – Entrega Final

## Objetivo
Ampliar la aplicación de biblioteca para usar funciones avanzadas de Oracle: **procedimientos almacenados**, **triggers** y **vistas**, llamándolas desde Java. Esto hace el sistema más seguro, rápido y fácil de mantener.

## Lo que se agregó

### En la base de datos
- **Una vista** llamada `vista_prestamos_activos` que muestra los préstamos actuales de forma sencilla.
- **Un procedimiento almacenado** llamado `registrar_prestamo` que guarda un nuevo préstamo y marca el libro como no disponible en un solo paso seguro.
- **Un trigger** llamado `devolver_libro` que se activa al eliminar un préstamo y automáticamente devuelve el libro a disponibilidad.

### En la aplicación Java
- La opción **1** ahora muestra los préstamos usando la vista (más eficiente).
- Se añadió la opción **6**, que permite registrar un préstamo usando el procedimiento almacenado.
  - Al igual que en la opción 5, **no pide el ISBN completo**: muestra una lista numerada de libros disponibles y el usuario elige por número.
  - Esto evita errores y hace la app más amigable.

## ¿Por qué es mejor?
- La lógica importante (como registrar o devolver libros) vive en la base de datos, no en la app.
- Si algo falla, Oracle deshace los cambios automáticamente.
- La app se vuelve más simple y confiable.
- Los datos siempre están consistentes, gracias al trigger.
