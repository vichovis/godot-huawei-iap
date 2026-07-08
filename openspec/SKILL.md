---
name: openspec
description: Generate structured technical specifications in 3 phases (Requirements → Design → Tasks) using EARS format and Mermaid diagrams. Creates .openspec/ directories with input.md, requirements.md, design.md, and tasks.md. Use when the user asks for "open spec", "generar especificación", "armar plan", "create spec", "EARS", or wants a structured plan for a feature, project, or infrastructure change.
---

# OpenSpec — Generador de Especificaciones Técnicas

Genera especificaciones técnicas estructuradas en 3 fases secuenciales, guardando todo en un directorio `.openspec/<project-name>/` dentro del proyecto actual.

## Workflow de 3 fases

```
FASE 1: REQUISITOS (EARS)       FASE 2: DISEÑO              FASE 3: TAREAS
input.md  ──→  requirements.md  ──→  design.md  ──→  tasks.md
              (formato EARS)         (diagramas Mermaid)   (checklist trazable)
```

Cada fase construye sobre la anterior. No se puede saltar de fase 1 a 3 sin aprobar la 2.

---

## Estructura de salida

Siempre crear esta estructura dentro del proyecto actual:

```
.openspec/<project-name>/
├── input.md           ← Descripción inicial del proyecto/feature
├── requirements.md    ← Requisitos en formato EARS
├── design.md          ← Diseño técnico con diagramas Mermaid
└── tasks.md           ← Plan de implementación con checkboxes
```

`<project-name>`: kebab-case, descriptivo. Ej: `observability`, `auth-migration`, `api-v2`.

---

## FASE 1 — Requisitos (formato EARS)

### Paso 1.1 — Crear `input.md`

Escribir la descripción del proyecto en lenguaje natural. Incluir:

- **Propósito**: Qué se quiere lograr
- **Alcance**: Qué cubre y qué no
- **Contexto**: Sistema actual, restricciones, dependencias
- **Stakeholders**: Quiénes se ven afectados

Preguntar al usuario si falta información antes de proseguir.

### Paso 1.2 — Generar `requirements.md`

Usar formato EARS (Enhanced At-a-glance Requirements Specification):

```markdown
# Requirements Document — [Project Name]

## Introduction
[Propósito, alcance, valor]

## Requirements

### Requirement 1: [Título descriptivo]
**User Story:** As a [rol], I want [funcionalidad], so that [beneficio]

#### Acceptance Criteria
1. WHEN [evento] THEN [sistema] SHALL [respuesta]
2. IF [precondición] THEN [sistema] SHALL [respuesta]
3. WHILE [condición] [sistema] SHALL [comportamiento]
4. WHERE [contexto] [sistema] SHALL [regla]
```

**Reglas EARS:**
- `WHEN`: Evento que dispara el comportamiento
- `IF`: Precondición que debe cumplirse
- `WHILE`: Comportamiento durante una condición
- `WHERE`: Contexto o ubicación específica
- Siempre usar `SHALL` (no should, must, may)
- Criterios de aceptación testeables y específicos
- Numeración jerárquica

Ver `references/ears-format.md` para más ejemplos.

### Paso 1.3 — Revisión

Mostrar los requisitos al usuario. Preguntar:
- ¿Falta algo?
- ¿Hay algo que sobre?
- ¿Los criterios son testeables?

Refinar hasta que el usuario apruebe. **No pasar a Fase 2 sin aprobación explícita.**

---

## FASE 2 — Diseño

### Paso 2.1 — Generar `design.md`

**IMPORTANTE**: Solo usar los requisitos aprobados. NO usar `input.md` original.

```markdown
# Design Document — [Project Name]

## Overview
[Enfoque arquitectónico, decisiones clave, justificación]

## Architecture
```mermaid
graph TB
    ...
```

## Components and Interfaces
- **Componente X**: Responsabilidad, inputs, outputs, dependencias

## Data Models
```mermaid
erDiagram
    ...
```

## Error Handling
[Estrategia de manejo de errores]

## Testing Strategy
[Enfoque de testing]
```

**Requisitos de los diagramas Mermaid:**
- Al menos un diagrama de arquitectura (`graph TB/LR`)
- Si hay datos, un diagrama ERD (`erDiagram`)
- Sintaxis válida de Mermaid (verificar mentalmente)

### Paso 2.2 — Revisión

Mostrar el diseño al usuario. Preguntar:
- ¿La arquitectura tiene sentido?
- ¿Falta algún componente?
- ¿Los diagramas son correctos?

Refinar hasta aprobación. **No pasar a Fase 3 sin aprobación explícita.**

---

## FASE 3 — Tareas

### Paso 3.1 — Generar `tasks.md`

**IMPORTANTE**: Usar requisitos + diseño aprobados. NO usar `input.md`.

```markdown
# Implementation Plan — [Project Name]

- [ ] 1. [Tarea principal]
  - [Descripción de entregables]
  - _Requirements: 1.1, 1.2_

- [ ] 2. [Tarea principal]
  - [ ] 2.1 [Subtarea]
    - [Entregables específicos]
    - _Requirements: 2.1_
  - [ ] 2.2 [Subtarea]
    - [Entregables específicos]
    - _Requirements: 2.2_
```

**Reglas:**
- Formato checkbox: `- [ ]`
- Máximo 2 niveles de jerarquía (1, 1.1, 2, 2.1)
- Cada tarea debe referenciar requisitos: `_Requirements: X.Y_`
- Entregables específicos y accionables (escribir código, crear archivo, configurar servicio)
- Tareas de 1-3 días de trabajo cada una

### Paso 3.2 — Revisión final

Mostrar tasks.md al usuario. Verificar:
- ¿Todas las tareas son accionables?
- ¿Cubren todos los requisitos?
- ¿El orden tiene sentido?

---

## Flujo completo recomendado

```
1. Usuario describe la idea
2. Crear .openspec/<project>/input.md
3. Revisar input → generar requirements.md → pedir aprobación
4. Aprobado → generar design.md → pedir aprobación
5. Aprobado → generar tasks.md → pedir aprobación
6. Tasks.md listo para implementar
```

## Reglas de oro

- **NUNCA** pasar a la siguiente fase sin aprobación explícita del usuario
- **NUNCA** usar `input.md` en las fases 2 y 3. Solo requisitos/diseño aprobados
- **SIEMPRE** usar formato EARS con `SHALL` en requirements
- **SIEMPRE** incluir diagramas Mermaid en design
- **SIEMPRE** usar checkboxes con trazabilidad en tasks
- **SIEMPRE** crear los archivos en `.openspec/<project-name>/`
