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
