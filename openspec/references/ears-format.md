# EARS Format Reference

EARS = Enhanced At-a-glance Requirements Specification

## Keywords

| Keyword | Propósito | Ejemplo |
|---------|-----------|---------|
| `WHEN` | Evento que dispara comportamiento | `WHEN user clicks "Save" THEN system SHALL persist data` |
| `IF` | Precondición que debe cumplirse | `IF password is incorrect THEN system SHALL display error` |
| `WHILE` | Comportamiento durante una condición | `WHILE session is active THEN system SHALL maintain auth state` |
| `WHERE` | Contexto o ubicación específica | `WHERE user is offline THEN system SHALL queue changes` |

## Estructura de requisito

```markdown
### Requirement N: [Título]
**User Story:** As a [rol], I want [acción], so that [beneficio]

#### Acceptance Criteria
1. WHEN [evento] THEN [sistema] SHALL [comportamiento]
2. IF [condición] THEN [sistema] SHALL [comportamiento]
3. WHILE [estado] [sistema] SHALL [comportamiento]
4. WHERE [contexto] [sistema] SHALL [comportamiento]
```

## Reglas

1. Siempre usar `SHALL` (obligatorio), nunca `should`, `must`, `may`
2. Criterios de aceptación testeables — que se puedan verificar con un test
3. Un requisito = 3-6 criterios de aceptación
4. Numeración: `1`, `1.1`, `2`, `2.1` (2 niveles máximo)

## Ejemplos

### Bueno ✅

```
WHEN user submits login form THEN system SHALL validate credentials against database
IF credentials are invalid THEN system SHALL return 401 with error message
WHILE request is processing THEN system SHALL show loading indicator
WHERE rate limit is exceeded THEN system SHALL return 429 with retry-after header
```

### Malo ❌

```
The system should handle login somehow
Users can log in when they want
The login page needs to work
```
