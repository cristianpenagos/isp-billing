-- Migración V2: Crea las tablas del módulo Clientes
--
-- Esta migración crea:
--  - clientes: aggregate root
--  - equipos_cpe: entity dentro del aggregate Cliente
--
-- Regla sagrada: una vez en producción, NO se modifica. Para cambios,
-- crear V3 que aplique el nuevo cambio.

-- Extensión para generar UUIDs dentro de la BD (opcional, nosotros los generamos en Java)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- Tabla: clientes
-- ============================================================================
CREATE TABLE clientes (
                          id UUID PRIMARY KEY,
                          codigo_interno VARCHAR(20) NOT NULL UNIQUE,
                          cedula VARCHAR(10) NOT NULL UNIQUE,
                          nombre_completo VARCHAR(150) NOT NULL,
                          telefono_whatsapp VARCHAR(20) NOT NULL,
                          email VARCHAR(254) NOT NULL,

    -- Dirección: campos aplanados (sin tabla aparte)
                          direccion_descripcion VARCHAR(200) NOT NULL,
                          direccion_barrio VARCHAR(100),
                          direccion_ciudad VARCHAR(100) NOT NULL,

                          estado VARCHAR(20) NOT NULL,
                          fecha_registro DATE NOT NULL,

                          CONSTRAINT chk_estado_cliente CHECK (estado IN ('ACTIVO', 'SUSPENDIDO', 'RETIRADO'))
);

COMMENT ON TABLE clientes IS 'Clientes del ISP WIX Networks';
COMMENT ON COLUMN clientes.codigo_interno IS 'Código único interno formato WX-NNNNN';

-- Índices para búsquedas frecuentes
CREATE INDEX idx_clientes_estado ON clientes (estado);
CREATE INDEX idx_clientes_cedula ON clientes (cedula);
CREATE INDEX idx_clientes_codigo_interno ON clientes (codigo_interno);

-- ============================================================================
-- Tabla: equipos_cpe
-- ============================================================================
CREATE TABLE equipos_cpe (
                             id UUID PRIMARY KEY,
                             cliente_id UUID NOT NULL,
                             tipo VARCHAR(20) NOT NULL,
                             marca VARCHAR(50),
                             modelo VARCHAR(50),
                             mac_address VARCHAR(17),
                             propiedad VARCHAR(20) NOT NULL,
                             fecha_instalacion DATE NOT NULL,

                             CONSTRAINT fk_equipo_cliente
                                 FOREIGN KEY (cliente_id)
                                     REFERENCES clientes (id)
                                     ON DELETE CASCADE,

                             CONSTRAINT chk_tipo_equipo CHECK (tipo IN ('ROUTER', 'ONU', 'MIKROTIK', 'ANTENA', 'TVBOX', 'MESH', 'OTRO')),
                             CONSTRAINT chk_propiedad_equipo CHECK (propiedad IN ('PROPIO', 'COMODATO', 'ALQUILADO'))
);

COMMENT ON TABLE equipos_cpe IS 'Equipos CPE instalados en los clientes';

CREATE INDEX idx_equipos_cliente_id ON equipos_cpe (cliente_id);
