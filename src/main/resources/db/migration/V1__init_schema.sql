-- =============================================
-- V1: Inicializacion del esquema
-- Habilita extensiones requeridas
-- =============================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tabla de control para verificar que la aplicacion esta conectada
CREATE TABLE IF NOT EXISTS application_health (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version VARCHAR(20) NOT NULL,
    initialized_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

INSERT INTO application_health (version) VALUES ('0.0.1-SNAPSHOT');
