# ADR-001: Arquitectura Hexagonal con Módulos por Dominio

## Estado
Aceptada — 2026-05-12

## Contexto

WIX Networks ISP necesita un sistema de facturación que:
- Permita agregar nuevas funcionalidades sin romper las existentes
- Sea testeable de forma aislada (lógica de negocio independiente de frameworks)
- Pueda integrarse con servicios externos potencialmente cambiantes (WhatsApp Business API)
- Respete principios SOLID
- Sea desplegable en la nube como un monolito (sin la complejidad de microservicios)

## Decisión

Adoptar **Arquitectura Hexagonal (Ports & Adapters)** sobre un **Monolito Modular** con los siguientes principios:

1. Cada módulo de negocio (clientes, facturación, pagos, etc.) es internamente hexagonal
2. El núcleo de dominio es Java puro: no depende de Spring, Hibernate ni de ninguna librería externa
3. Las dependencias siempre apuntan hacia adentro (regla de dependencia)
4. Los aggregates entre módulos se referencian por ID, no por objeto completo
5. La comunicación entre módulos se hace a través de puertos públicos (interfaces)

## Consecuencias

### Positivas
- Cambiar de Hibernate a otra librería de persistencia solo requiere reescribir un adaptador
- Cambiar de proveedor de WhatsApp solo requiere implementar un nuevo adaptador
- Los tests del dominio son rapidísimos: no necesitan levantar Spring ni una BD
- Si en el futuro un módulo necesita escalar independientemente, se puede extraer como microservicio
- La estructura del código refleja directamente el modelo de negocio

### Negativas
- Más archivos y carpetas que en una arquitectura por capas tradicional
- Curva de aprendizaje inicial: requiere disciplina para no acoplar el dominio a frameworks
- Más mapeos entre representaciones (dominio ↔ JPA ↔ DTO)

## Alternativas consideradas

### Arquitectura por capas tradicional (Controller → Service → Repository)
**Rechazada**: tiende a acoplar el dominio a JPA, dificulta el testing y envejece mal cuando crece la lógica de negocio.

### Microservicios
**Rechazada**: complejidad operacional innecesaria para el tamaño actual del problema y para un equipo de un solo desarrollador.

### Clean Architecture (Uncle Bob)
**Rechazada**: prácticamente equivalente a Hexagonal pero con más ceremonia. Hexagonal da el 90% del beneficio con el 60% de la complejidad.
