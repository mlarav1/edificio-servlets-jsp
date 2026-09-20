-- Datos iniciales. Claves de prueba (guardadas con BCrypt):
--   admin@edificios.com    / Admin123
--   operador@edificios.com / Operador123
--   consulta@edificios.com / Consulta123

INSERT INTO usuario (id, clave, nombre, rol) VALUES
('admin@edificios.com',    '$2a$10$eouYRIAf4rBd/N.GHdsG1unfVeGvvWuWMUNx6VkovIe1Mg.AlBaBK', 'Administrador General', 'ADMIN'),
('operador@edificios.com', '$2a$10$PMdKrzgf6Na9gRxgOEDYC.O06n6oL6Lk9JUzz6ITwzbxxsKEraqKa', 'Laura Operadora',       'OPERADOR'),
('consulta@edificios.com', '$2a$10$yBMKpUxF0JWdMFiR9vNZ2eLUa1vcrvZMqsoW2Yoma0t80P4lqXduS', 'Carlos Consulta',      'CONSULTA');

INSERT INTO edificio (nombre, metros_cuadrados, altura, num_pisos, num_apartamentos, num_oficinas, nombre_parqueadero, num_piscinas, pais, departamento, ciudad, tiene_ascensor, valor_administracion, tiene_zona_social) VALUES
('Torre Colpatria',            41000.00, 196.00, 50,   0, 180, 'Parqueadero Colpatria',      0, 'Colombia', 'Cundinamarca', 'Bogotá',       TRUE, 3500000.00, TRUE),
('Edificio Coltejer',          32000.00, 175.00, 36,   0, 120, 'Parqueadero Coltejer',       0, 'Colombia', 'Antioquia',    'Medellín',     TRUE, 2800000.00, FALSE),
('Torre Reserva del Mar',      18500.00,  95.00, 28, 112,   0, 'Parqueadero Reserva',        2, 'Colombia', 'Bolívar',      'Cartagena',    TRUE,  650000.00, TRUE),
('Edificio Bocagrande Plaza',  12000.00,  70.00, 20,  80,   4, 'Parqueadero Bocagrande',     1, 'Colombia', 'Bolívar',      'Cartagena',    TRUE,  520000.00, TRUE),
('Torres del Parque',          22000.00, 110.00, 32, 150,   0, 'Parqueadero Torres',         1, 'Colombia', 'Cundinamarca', 'Bogotá',       TRUE,  780000.00, TRUE),
('Edificio Miraflores',         3500.00,  24.00,  8,  32,   0, 'Parqueadero Miraflores',     0, 'Colombia', 'Valle del Cauca','Cali',       FALSE, 180000.00, FALSE),
('Torre de Cali',              25500.00, 120.00, 40,   0, 150, 'Parqueadero Torre de Cali',  0, 'Colombia', 'Valle del Cauca','Cali',       TRUE, 2100000.00, TRUE),
('Edificio Prado Alto',         6200.00,  35.00, 12,  48,   2, 'Parqueadero Prado',          1, 'Colombia', 'Atlántico',    'Barranquilla', TRUE,  310000.00, TRUE),
('Edificio Centro Empresarial',14000.00,  60.00, 15,   0,  75, 'Parqueadero Centro Emp.',    0, 'Colombia', 'Atlántico',    'Barranquilla', TRUE, 1450000.00, FALSE),
('Conjunto Los Almendros',      2800.00,  18.00,  6,  24,   0, 'Parqueadero Almendros',      0, 'Colombia', 'Santander',    'Bucaramanga',  FALSE, 140000.00, FALSE);
