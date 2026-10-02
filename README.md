# Sistema de Gestión de Gimnasio

Aplicación de escritorio en Java Swing con arquitectura MVC y gestión de dependencias con Maven, desarrollada por incrementos funcionales colaborativos.

**Estado actual:** Incremento 3 — integración de objeto de configuración global (`ConfiguracionGimnasio`) y habilitación del caso de uso de actualización de estado de membresía mediante interacción visual en interfaz gráfica. Este incremento expande el flujo operativo previo (Registrar y Consultar afiliados) incorporando la modificación de estado (`ACTIVO` / `INACTIVO`) a través de un botón interactivo en `FrmRegistro`, coordinado mediante desacoplamiento por interfaces (`IUsuarioRepository` y `UsuarioController`). Adicionalmente, se centralizaron los parámetros operativos del sistema (nombre de sede, versión del aplicativo y tarifa base) eliminando valores cableados (*hardcoded*) en la vista, y se implementó un control de riesgos operativo y de integración para el trabajo concurrente sobre la rama `master`.

---

## Descripción

El sistema gestiona el control de acceso, suscripciones y membresías de un centro de acondicionamiento físico. Modela los afiliados del gimnasio con sus datos personales, plan de entrenamiento (`PlanMembresia`), rol de acceso (`Rol`) y estado de vigencia (`EstadoUsuario`).

La persistencia de este incremento se administra a través de una colección dinámica en memoria (`ArrayList`), encapsulada tras el contrato de repositorio para permitir su posterior migración hacia persistencia relacional (JDBC / SQL) sin requerir modificaciones sobre el controlador ni la interfaz gráfica.

---

## Requisitos del Entorno

- **JDK:** Java Development Kit 17 o superior (compatible con JDK 17 y JDK 23)
- **Gestor de construcción:** Apache Maven 3.8+
- **IDE recomendado:** Apache NetBeans 19+
- **Look and Feel:** Soporte para FlatLaf Dark integrado vía Maven

---

## Estructura del Proyecto

```
SistemaGimnasio/
├── pom.xml                                  ← descriptor de proyecto Maven (plugins y dependencias)
├── README.md                                ← este documento de arquitectura y operación
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── gimnasio/
        │           ├── SistemaGimnasio.java ← clase principal de arranque (ensamblador del aplicativo)
        │           ├── controlador/
        │           │   └── UsuarioController.java       ← intermediario MVC y orquestador del caso de uso
        │           ├── modelo/
        │           │   ├── ConfiguracionGimnasio.java   ← objeto global de configuración y parámetros de sede
        │           │   ├── EstadoUsuario.java           ← enum de estados de membresía (ACTIVO, INACTIVO)
        │           │   ├── PlanMembresia.java           ← modalidades de suscripción al gimnasio
        │           │   ├── Rol.java                     ← catálogo de perfiles de usuario
        │           │   └── Usuario.java                 ← entidad base del afiliado (POJO con encapsulamiento)
        │           ├── repositorio/
        │           │   ├── IUsuarioRepository.java      ← contrato abstracto de persistencia
        │           │   └── UsuarioRepositoryImpl.java   ← implementación en memoria basada en colecciones
        │           └── vista/
        │               └── FrmRegistro.java             ← formulario Swing con controles de búsqueda y botón de estado
        └── resources/                       ← recursos estáticos y assets de la aplicación
```

---

## Instalación y Ejecución

### Opción 1: Desde NetBeans IDE
1. Abrir NetBeans y seleccionar **File → Open Project...**.
2. Navegar hasta la carpeta `SistemaGimnasio` (el IDE detectará automáticamente el proyecto Maven).
3. Hacer clic derecho sobre el proyecto raíz y seleccionar **Clean and Build**.
4. Presionar `Shift + F6` sobre `SistemaGimnasio.java` (o presionar `F6` tras definirla como clase principal).

### Opción 2: Desde la Terminal (CLI Maven)
```bash
# 1. Clonar el repositorio
git clone [https://github.com/JuanUrzola10/sistema-gimnasio.git](https://github.com/JuanUrzola10/sistema-gimnasio.git)

# 2. Posicionarse en el directorio raíz
cd sistema-gimnasio

# 3. Compilar el ciclo de vida y empaquetar
mvn clean compile

# 4. Ejecutar la clase principal
mvn exec:java -Dexec.mainClass="com.gimnasio.SistemaGimnasio"
```

---

## Arquitectura y Flujo de Datos

```
                                  +-----------------------------+
                                  |    ConfiguracionGimnasio    |
                                  | (Constantes de sede/tarifa) |
                                  +--------------+--------------+
                                                 | alimenta título/etiquetas
                                                 v
+--------------------------------------------------------------------------+
|                            FrmRegistro (JFrame)                          |
|  - Encabezado dinámico con NOMBRE_SEDE y VERSION_SISTEMA                 |
|  - Formulario de captura y consulta por documento                        |
|  - Botón interactivo: "Cambiar Estado" (Activar / Inactivar)             |
+------------------------------------+-------------------------------------+
                                     |
                          invoca     | eventos de usuario
                                     v
+--------------------------------------------------------------------------+
|                         UsuarioController                                |
|  - registrarAfiliado(...)                                                |
|  - buscarAfiliado(documento)                                             |
|  - actualizarEstadoAfiliado(documento, nuevoEstado)                      |
+------------------------------------+-------------------------------------+
                                     |
                          depende de | abstracción (DIP)
                                     v
+--------------------------------------------------------------------------+
|                       <<interface>> IUsuarioRepository                   |
|  + boolean agregar(Usuario usuario);                                     |
|  + Usuario buscarPorDocumento(String documento);                         |
|  + boolean cambiarEstado(String documento, EstadoUsuario nuevoEstado);    |
+------------------------------------+-------------------------------------+
                                     |
                        implementa   | contrato
                                     v
+--------------------------------------------------------------------------+
|                       UsuarioRepositoryImpl                              |
|  - List<Usuario> usuarios (Colección en memoria)                         |
|  - Lógica de búsqueda secuencial y mutación de estado del objeto         |
+------------------------------------+-------------------------------------+
                                     |
                         opera sobre | entidades
                                     v
+--------------------------------------------------------------------------+
|                          Dominio / Modelo                                |
|     Usuario  <--->  EstadoUsuario [ACTIVO | INACTIVO]                    |
|              <--->  PlanMembresia                                        |
+--------------------------------------------------------------------------+
```

### Reglas de Dependencia y Separación de Capas:
- **Desacoplamiento de la Vista:** `FrmRegistro` no interactúa directamente con la lista de usuarios ni modifica atributos en memoria. Toda interacción pasa por `UsuarioController`.
- **Inversión de Dependencias (DIP):** El controlador depende de la interfaz `IUsuarioRepository`, permitiendo sustituir el almacenamiento en memoria por una base de datos sin alterar la lógica de negocio.
- **Modelo Agnóstico de Frameworks:** Las clases dentro de `com.gimnasio.modelo` no contienen importaciones de `javax.swing` ni componentes visuales.
- **Centralización de Configuración:** Las constantes institucionales no están distribuidas de forma arbitraria en los formularios; se leen desde el objeto estático `ConfiguracionGimnasio`.

---

## Novedades del Incremento 3

Este incremento incorpora mejoras operativas y funcionales sobre el sistema base:

### 1. Objeto de Configuración Global (`ConfiguracionGimnasio`)
- **Problema previo:** El nombre del gimnasio, la tarifa mensual sugerida y la versión del sistema se encontraban escritos de forma estática dentro de las propiedades del JFrame.
- **Solución arquitectónica:** Se implementó una clase dedicada con atributos constantes (`public static final`):
  - `NOMBRE_SEDE`: Centraliza la denominación comercial desplegada en el título del software.
  - `VERSION_SISTEMA`: Controla el identificador de versión (`v2.2.0`) visible para el usuario.
  - `TARIFA_MENSUAL`: Establece la base de liquidación económica para los planes.
- **Consumo en la Vista:** La ventana principal invoca directamente la configuración en su inicialización:
  ```java
  setTitle(ConfiguracionGimnasio.NOMBRE_SEDE + " | Versión " + ConfiguracionGimnasio.VERSION_SISTEMA);
  ```

### 2. Control Interactivo de Estado de Membresía (Botón de Estado)
- Se habilitó la transición de estado del afiliado sin necesidad de recrear el registro:
  - **Firma del contrato:** Se añadió `cambiarEstado(String documento, EstadoUsuario nuevoEstado)` en `IUsuarioRepository`.
  - **Implementación segura:** `UsuarioRepositoryImpl` localiza al afiliado por su documento; si existe, muta su propiedad `setEstado(...)` y retorna confirmación lógica.
  - **Puente en controlador:** `UsuarioController` expone `actualizarEstadoAfiliado(...)` para abstraer la persistencia frente a la interfaz.
  - **Experiencia de usuario (UI):** Tras buscar un afiliado, un botón de acción permite conmutar su estado entre `ACTIVO` e `INACTIVO`, notificando mediante un diálogo `JOptionPane` y refrescando la vista de inmediato.

---

## Matriz de Gestión de Riesgos del Proyecto

Durante el desarrollo de este incremento, el equipo identificó, priorizó y trató formalmente los riesgos operacionales y técnicos que podían comprometer la entrega:

### 1. Matriz de Priorización y Tratamiento de Riesgos

| ID | Riesgo Identificado | Activo Afectado | Probabilidad | Impacto | Prioridad | Respuesta | Acción de Tratamiento Concreta |
|:--:|:---|:---|:---:|:---:|:---:|:---:|:---|
| **R1** | **Falta de continuidad en datos de prueba:** Pérdida de registros de afiliados al cerrar la aplicación (persistencia en memoria). | Datos de clientes y estado de membresía | Alta | Alto | 🔴 **Crítica** | **Mitigar** | Precarga de datos semilla (usuarios de prueba preestablecidos) en el constructor de `UsuarioRepositoryImpl`. |
| **R2** | **Conflictos de integración en Git:** Sobreescritura o colisiones de código al trabajar concurrentemente sobre la rama `master`. | Repositorio remoto en GitHub (`master`) | Media | Alto | 🟠 **Alta** | **Evitar** | Flujo secuencial estricto en "fila india" con sincronización previa obligatoria (`git pull`) antes de editar código. |
| **R3** | **Desfase en cronograma y cuellos de botella:** Retrasos por dependencia lineal de turnos y grabación de evidencias. | Cronograma de entrega y flujo en Trello | Media | Alto | 🟠 **Alta** | **Mitigar** | Delimitación de tiempos (*time-boxing* de 45 min por turno) y seguimiento en tiempo real del tablero de Trello. |
| **R4** | **Indisponibilidad operativa de integrantes:** Fallas de conectividad, suministro eléctrico o imprevistos de un miembro. | Capacidad productiva del equipo de trabajo | Media | Medio | 🟡 **Media** | **Aceptar** | Documentación precisa en cada tarjeta de Trello con los métodos a implementar para permitir relevos técnicos. |

### 2. Integración de Acciones al Tablero de Trello
Para evitar que la gestión de riesgos quede como un ejercicio teórico, cada tratamiento se vinculó a una tarjeta operativa bajo la regla:

$$\text{RIESGO} \longrightarrow \text{ACCIÓN} \longrightarrow \text{RESPONSABLE} \longrightarrow \text{SEGUIMIENTO}$$

* **Riesgo R1 (Datos) →** Tarea: *Implementar la nueva funcionalidad* | **Responsable:** Cristian Martínez | **Seguimiento:** Checklist interno de inicialización de datos base.
* **Riesgo R2 (Integración) →** Tarea: *Sincronización de rama master* | **Responsable:** Todo el equipo | **Seguimiento:** Checklist obligatorio `[x] git pull ejecutado` antes de cada commit.
* **Riesgo R3 (Tiempo) →** Tarea: *Coordinación del incremento* | **Responsable:** Juan Urzola | **Seguimiento:** Control visual de paso de tarjetas a columna *Hecho*.

---

## Pilares de la Programación Orientada a Objetos

- **Encapsulamiento:** Los atributos de la clase `Usuario` (`documento`, `nombre`, `telefono`, `estado`, `plan`) son privados (`private`) y se gestionan exclusivamente a través de métodos de acceso y mutación (*getters/setters*), protegiendo la integridad de las entidades de dominio.
- **Abstracción:** Se define la interfaz `IUsuarioRepository`, la cual expone operaciones esenciales (`agregar`, `buscarPorDocumento`, `cambiarEstado`) ocultando por completo la estructura interna del almacenamiento (colección en memoria `ArrayList`).
- **Polimorfismo:** El controlador opera contra la abstracción `IUsuarioRepository`, lo que permite intercambiar en tiempo de compilación o ejecución la implementación de persistencia sin alterar el código cliente.
- **Modularidad:** El proyecto se estructura en paquetes funcionales independientes (`modelo`, `repositorio`, `controlador`, `vista`), garantizando bajo acoplamiento y alta cohesión.

---

## Principios SOLID Aplicados

| Principio | Aplicación en el Proyecto |
|---|---|
| **Single Responsibility (SRP)** | Cada clase tiene un único propósito: `ConfiguracionGimnasio` almacena parámetros globales, `UsuarioRepositoryImpl` persiste datos, `UsuarioController` coordina la lógica y `FrmRegistro` presenta la interfaz. |
| **Open/Closed (OCP)** | El sistema está abierto a la extensión y cerrado a la modificación: agregar un nuevo medio de almacenamiento solo requiere una clase que implemente `IUsuarioRepository` sin alterar el controlador. |
| **Liskov Substitution (LSP)** | Cualquier implementación de `IUsuarioRepository` puede reemplazar a `UsuarioRepositoryImpl` en el controlador sin degradar o alterar el comportamiento de la aplicación. |
| **Interface Segregation (ISP)** | El contrato de persistencia expone únicamente los métodos que el dominio de negocio necesita para operar (evita interfaces monolíticas con métodos redundantes). |
| **Dependency Inversion (DIP)** | `UsuarioController` depende directamente de la abstracción `IUsuarioRepository` y no de la clase concreta `UsuarioRepositoryImpl`, delegando la inicialización en el ensamblador principal. |

---

## Manual de Uso del Aplicativo

### 1. Inicialización
Al ejecutar el proyecto, se despliega la ventana principal `FrmRegistro`. El título superior de la ventana confirmará la integración del objeto de configuración mostrando el nombre de la sede y la versión correspondiente.

### 2. Registro de Afiliados
1. Diligenciar los campos: Documento, Nombre y Teléfono.
2. Seleccionar el Plan de Membresía deseado y el Rol del usuario.
3. Presionar el botón **Registrar**. El sistema validará que los campos no estén vacíos y asignará el estado `ACTIVO` por defecto, almacenando el objeto en la colección en memoria.

### 3. Búsqueda y Consulta
1. Ingresar el número de documento en el campo correspondiente.
2. Presionar el botón **Buscar**.
3. El sistema consultará el repositorio y cargará la información en los campos del formulario, indicando el estado actual de la membresía.

### 4. Actualización del Estado de Membresía (Nueva Función)
1. Con un afiliado cargado en pantalla, hacer clic en el botón **Cambiar Estado** (o **Inactivar / Activar**).
2. El sistema cambiará el estado de `ACTIVO` a `INACTIVO` (o viceversa) a través de la capa de persistencia.
3. Se desplegará un cuadro de diálogo (`JOptionPane`) confirmando la actualización. Una búsqueda posterior reflejará el nuevo estado de forma inmediata.

---

## Hoja de Ruta (Roadmap de Incrementos)

| Incremento | Estado | Alcance Técnico |
|:--:|:---:|:---|
| **Incremento 1** | ✅ Completado | Estructura base del modelo (`Usuario`), interfaz Swing inicial y registro de afiliados en memoria. |
| **Incremento 2** | ✅ Completado | Búsqueda por documento, desacoplamiento en repositorio (`IUsuarioRepository`) y control de duplicados. |
| **Incremento 3** | ✅ Completado | Objeto global `ConfiguracionGimnasio`, cambio de estado con botón interactivo, gestión de riesgos y flujo colaborativo Git en `master`. |
| **Incremento 4** | ⏳ Planificado | Listado tabular completo mediante componente `JTable` con filtros dinámicos por estado de membresía. |
| **Incremento 5** | ⏳ Planificado | Persistencia relacional en base de datos mediante JDBC, sustituyendo `UsuarioRepositoryImpl` sin tocar la vista ni el controlador. |

---

## Limitaciones Conocidas

- **Persistencia Volátil:** Los datos residen en memoria RAM (`ArrayList`), por lo que cerrar el aplicativo reinicia los registros al conjunto de datos base inicial.
- **Concurrencia de Escritorio:** Aplicación monolítica orientada a una única estación de trabajo; no gestiona concurrencia multihilo sobre la colección compartida.

---

## Equipo de Desarrollo y Roles

**Corporación Universitaria Remington — Facultad de Ingeniería**  
Ingeniería de Software II — Sede Sahagún / Montería, Córdoba

* **Juan Urzola** — *Líder de Proyecto / Módulo de Configuración Global*
* **Cristian Martínez** — *Lógica de Negocio, Repositorio y Control de Persistencia*
* **José Pérez** — *Interfaz Gráfica de Usuario (UI/UX) y Validación Funcional*
```
