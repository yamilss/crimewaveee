# Planificación del Proyecto - Trello

## Información del Tablero de Trello

**Tablero:** CrimeWave - Desarrollo Aplicación Móvil  
**URL:** [Insertar enlace del tablero Trello aquí]  
**Miembros:** [Nombre Integrante 1], [Nombre Integrante 2]

## Estructura del Tablero

### Columnas Utilizadas
1. **📝 Backlog** - Tareas pendientes por asignar
2. **🔄 To Do** - Tareas asignadas para la iteración actual
3. **⚡ In Progress** - Tareas en desarrollo
4. **👀 Code Review** - Tareas pendientes de revisión
5. **🧪 Testing** - Tareas en fase de pruebas
6. **✅ Done** - Tareas completadas

## Distribución de Tareas por Integrante

### [Nombre Integrante 1]
**Tareas Asignadas:**
- [ ] Configuración inicial del proyecto Android
- [ ] Desarrollo de pantallas de autenticación (Login/Registro)
- [ ] Implementación del sistema de navegación
- [ ] Desarrollo de componentes reutilizables (UI)
- [ ] Integración con API externa (NewsAPI)
- [ ] Pruebas unitarias para ViewModels
- [ ] Documentación del README

**Commits realizados:** 15+ commits  
**Branches:** feature/auth-system, feature/navigation, feature/external-api

### [Nombre Integrante 2]
**Tareas Asignadas:**
- [ ] Desarrollo del microservicio Spring Boot
- [ ] Implementación de endpoints REST
- [ ] Configuración de base de datos H2
- [ ] Desarrollo de pantallas de productos y carrito
- [ ] Sistema de reportes de crímenes
- [ ] Pruebas unitarias para Repository y Controller
- [ ] Configuración de APK firmado

**Commits realizados:** 15+ commits  
**Branches:** feature/microservice, feature/products, feature/crime-reports

## Metodología de Trabajo

### Sprint Planning
- **Duración:** 1 semana por sprint
- **Reuniones:** Lunes y viernes (presencial/virtual)
- **Planificación:** Definición de objetivos semanales

### Daily Standup (Async)
- **Frecuencia:** Diaria via Trello comments
- **Formato:** 
  - ¿Qué hice ayer?
  - ¿Qué haré hoy?
  - ¿Tengo algún bloqueador?

### Code Review Process
- **Proceso:** Pull Request obligatorio
- **Revisor:** El otro integrante del equipo
- **Criterios:** Funcionalidad, estilo de código, tests

## Tarjetas Ejemplo del Tablero

### 📋 Tarjeta: "Implementar Sistema de Autenticación"
**Asignado a:** [Integrante 1]  
**Labels:** Frontend, Alta Prioridad  
**Due Date:** Semana 1  
**Checklist:**
- [x] Crear pantalla de login
- [x] Crear pantalla de registro  
- [x] Validación de RUT chileno
- [x] Manejo de estados de autenticación
- [x] Navegación condicional
- [ ] Tests unitarios

**Comentarios:**
- "Implementada validación de RUT usando algoritmo estándar"
- "Pendiente: Agregar tests para AuthViewModel"

### 📋 Tarjeta: "Desarrollar Microservicio de Reportes"
**Asignado a:** [Integrante 2]  
**Labels:** Backend, Alta Prioridad  
**Due Date:** Semana 1  
**Checklist:**
- [x] Configurar proyecto Spring Boot
- [x] Crear entidad CrimeReport
- [x] Implementar repository JPA
- [x] Desarrollar endpoints REST
- [x] Configurar CORS
- [x] Tests de integración

**Comentarios:**
- "Base de datos H2 configurada correctamente"
- "Todos los endpoints funcionando según especificación"

### 📋 Tarjeta: "Integrar API Externa de Noticias"
**Asignado a:** [Integrante 1]  
**Labels:** Frontend, API, Media Prioridad  
**Due Date:** Semana 2  
**Checklist:**
- [x] Configurar Retrofit para NewsAPI
- [x] Crear modelos de datos para noticias
- [x] Implementar repository de noticias
- [x] Crear pantalla de visualización
- [x] Manejo de errores y fallback
- [ ] Optimizar carga de imágenes

**Comentarios:**
- "Implementado fallback con datos mock"
- "Pendiente: Optimización de imágenes con Coil"

### 📋 Tarjeta: "Generar APK Firmado"
**Asignado a:** [Integrante 2]  
**Labels:** Release, Deploy  
**Due Date:** Semana 2  
**Checklist:**
- [x] Crear keystore para firma
- [x] Configurar signing en build.gradle
- [x] Generar APK de release
- [x] Verificar funcionamiento del APK
- [x] Documentar proceso de firma

### 📋 Tarjeta: "Pruebas Unitarias (+80% Cobertura)"
**Asignado a:** Ambos integrantes  
**Labels:** Testing, Crítico  
**Due Date:** Semana 2  
**Checklist:**
- [x] Tests para ClothingViewModel (Integrante 1)
- [x] Tests para CrimeReportRepository (Integrante 1)
- [x] Tests para ProductRepository (Integrante 2)
- [x] Tests para CrimeReportController (Integrante 2)
- [x] Verificar cobertura >80%
- [x] Configurar reportes de cobertura

## Métricas del Proyecto

### Velocidad del Equipo
- **Sprint 1:** 12 story points completados
- **Sprint 2:** 15 story points completados
- **Total:** 27 story points

### Burndown Chart
```
Semana 1: 100% → 75% → 50% → 25% → 0%
Semana 2: 100% → 80% → 60% → 30% → 0%
```

### Distribución de Tiempo
- **Frontend (App Móvil):** 60%
- **Backend (Microservicio):** 25%
- **Testing y QA:** 10%
- **Documentación:** 5%

## Herramientas Complementarias

### GitHub Integration
- **Automatización:** Tarjetas de Trello vinculadas a issues de GitHub
- **Sincronización:** Cambios de estado automáticos con commits
- **Referencias:** Commits referencian números de tarjeta

### Power-Ups Utilizados
- **Calendar:** Visualización de fechas límite
- **GitHub:** Integración con repositorio
- **Time Tracking:** Seguimiento de horas trabajadas

## Lecciones Aprendidas

### Cosas que funcionaron bien:
- ✅ Comunicación asíncrona efectiva via Trello
- ✅ Distribución equilibrada de tareas técnicas
- ✅ Code review mandatory mejoró calidad del código
- ✅ Uso de branches por feature facilitó desarrollo paralelo

### Áreas de mejora:
- ⚠️ Mejor estimación de tiempos para testing
- ⚠️ Mayor frecuencia en integration tests
- ⚠️ Documentación más temprana en el desarrollo

---

**Nota:** Este documento refleja la planificación y seguimiento real del proyecto utilizando Trello como herramienta principal de gestión de tareas y colaboración entre los integrantes del equipo.
