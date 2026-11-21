# DogApp - equipoCuatro

Agenda de citas para mascotas. Miniproyecto 1 de Desarrollo de Aplicaciones para Dispositivos Móviles (Universidad del Valle, 2026-2).

## Requisitos para abrir el proyecto

- Android Studio con el SDK de Android 34.
- **JDK 17 para Gradle.** El proyecto usa Gradle 8.0, AGP 8.1.1 y Kotlin 1.9.0 (la misma configuración de los ejemplos de clase), que no funcionan con un JDK más nuevo. En Android Studio: `Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK` y elegir un JDK 17.

## Arquitectura

MVVM con Repository. Todo el código está en `app/src/main/java/com/univalle/dogapp`:

| Paquete | Contenido |
|---|---|
| `view` | `MainActivity` (contenedor del Navigation), `fragment`, `adapter` y `viewholder` |
| `viewmodel` | ViewModels con LiveData |
| `repository` | Puente entre los ViewModels y las fuentes de datos |
| `model` | Data class y entidades de Room |
| `data` | DAO y base de datos de Room |
| `webservice` | Servicio de Retrofit para la API de Dog CEO |
| `utils` | Constantes |

Regla del equipo: ningún fragment o activity consulta la base de datos ni la API directamente; siempre se pasa por ViewModel y Repository.

## Capa de datos de las citas

Todas las historias usan el mismo `AppointmentViewModel`:

```kotlin
private val appointmentViewModel: AppointmentViewModel by viewModels()
```

| Necesito | Llamar | Observar |
|---|---|---|
| Listar las citas (Home) | `getListAppointments()` | `listAppointments` |
| Traer una cita por su id (Detalle, Editar) | `getAppointmentById(id)` | `appointment` |
| Guardar una cita (Nueva Cita) | `saveAppointment(cita) { ok -> ... }` | |
| Editar una cita | `updateAppointment(cita) { ok -> ... }` | |
| Eliminar una cita | `deleteAppointment(cita) { ok -> ... }` | |
| Lista de razas para el campo Raza | `getBreeds()` | `listBreeds` |
| Saber si hay una operación en curso (loading) | | `progressState` |

- La cita es la entidad `Appointment` (`id`, `dogName`, `breed`, `ownerName`, `phone`, `symptom`, `photoUrl`). El `id` es autogenerado y es el turno.
- Al guardar no se envía `photoUrl`: el repositorio consulta la imagen de la raza en la API y la guarda con la cita. Al editar solo se cambia la imagen si cambió la raza.
- La función que va entre llaves en guardar, editar y eliminar se ejecuta cuando la operación termina; ahí se navega al Home.
- Las pruebas de esta capa están en `app/src/androidTest` y se ejecutan en un dispositivo con internet.

## Navegación

Un solo `MainActivity` con el grafo `res/navigation/nav_graph.xml`:

| Historia | Fragment |
|---|---|
| HU 1.0 Ventana Login | `LoginFragment` |
| HU 2.0 Home Administrador de Citas | `HomeFragment` |
| HU 3.0 Ventana Nueva Cita | `AddAppointmentFragment` |
| HU 4.0 Ventana Detalle de la Cita | `AppointmentDetailsFragment` |
| HU 5.0 Ventana Editar Cita | `EditAppointmentFragment` |

## Librerías

Navigation, Room (con KSP), corrutinas, ViewModel y LiveData, DataBinding, Retrofit con Gson, Glide, Biometric y Lottie.

## Convención de commits

[Conventional Commits](https://www.conventionalcommits.org/es/): `tipo: descripción en minúscula`, por ejemplo `feat: agregar animación de huella en el login`. Tipos usados: `feat`, `fix`, `chore`, `build`, `docs`, `style`, `refactor`, `test`.
