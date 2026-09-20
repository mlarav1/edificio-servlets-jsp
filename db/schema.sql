-- Esquema de la base de datos "edificios" (PostgreSQL)
-- Uso: psql -U postgres -d edificios -f db/schema.sql

DROP TABLE IF EXISTS edificio;
DROP TABLE IF EXISTS usuario;

-- El id del usuario es su correo electronico: sirve como nombre de
-- inicio de sesion y como destino de la recuperacion de clave.
CREATE TABLE usuario (
    id      VARCHAR(120) PRIMARY KEY,
    clave   VARCHAR(100) NOT NULL,          -- hash BCrypt
    nombre  VARCHAR(100) NOT NULL,
    rol     VARCHAR(20)  NOT NULL CHECK (rol IN ('ADMIN', 'OPERADOR', 'CONSULTA'))
);

CREATE TABLE edificio (
    id                   SERIAL PRIMARY KEY,
    nombre               VARCHAR(120)  NOT NULL,
    metros_cuadrados     DECIMAL(12,2) NOT NULL CHECK (metros_cuadrados > 0),
    altura               DECIMAL(8,2)  NOT NULL CHECK (altura > 0),
    num_pisos            INT           NOT NULL CHECK (num_pisos > 0),
    num_apartamentos     INT           NOT NULL DEFAULT 0 CHECK (num_apartamentos >= 0),
    num_oficinas         INT           NOT NULL DEFAULT 0 CHECK (num_oficinas >= 0),
    nombre_parqueadero   VARCHAR(120),
    num_piscinas         INT           NOT NULL DEFAULT 0 CHECK (num_piscinas >= 0),
    pais                 VARCHAR(60)   NOT NULL,
    departamento         VARCHAR(60)   NOT NULL,
    ciudad               VARCHAR(60)   NOT NULL,
    tiene_ascensor       BOOLEAN       NOT NULL DEFAULT FALSE,
    valor_administracion DECIMAL(14,2) NOT NULL CHECK (valor_administracion >= 0),
    tiene_zona_social    BOOLEAN       NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_edificio_ciudad ON edificio (ciudad);
