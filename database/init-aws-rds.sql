-- Script de inicialización para AWS RDS PostgreSQL
-- Base de datos: crimewave_products

-- Crear usuario para la aplicación (ejecutar como administrador)
CREATE USER crimewave_user WITH PASSWORD 'CrimeWave2024!';

-- Crear la base de datos
CREATE DATABASE crimewave_products OWNER crimewave_user;

-- Conectar a la base de datos crimewave_products
\c crimewave_products;

-- Otorgar permisos al usuario
GRANT ALL PRIVILEGES ON DATABASE crimewave_products TO crimewave_user;
GRANT ALL ON SCHEMA public TO crimewave_user;

-- La tabla se creará automáticamente por JPA, pero aquí está el schema para referencia:
/*
CREATE TABLE clothing_items (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    image_url VARCHAR(500),
    category VARCHAR(50) NOT NULL,
    is_new BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    stock INTEGER DEFAULT 0 CHECK (stock >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar performance
CREATE INDEX idx_clothing_items_category ON clothing_items(category);
CREATE INDEX idx_clothing_items_featured ON clothing_items(is_featured);
CREATE INDEX idx_clothing_items_stock ON clothing_items(stock);
CREATE INDEX idx_clothing_items_name ON clothing_items USING gin(to_tsvector('spanish', name));

-- Datos iniciales de prueba
INSERT INTO clothing_items (id, name, description, price, image_url, category, is_new, is_featured, stock) VALUES
('1', 'Polera Satoru Gojo', 'Diseño original de Satoru Gojo del anime Jujutsu Kaisen', 22000.00, 'satorupolera', 'POLERAS', true, false, 50),
('2', 'Polerón Toga Himiko', 'Polerón con diseño de Himiko Toga del anime My Hero Academia', 42000.00, 'togahoodie', 'POLERONES', true, true, 30),
('3', 'Cuadro Given', 'Cuadro decorativo minimalista con diseño del anime Given', 45000.00, 'givencuadro', 'CUADROS', true, false, 15),
('4', 'Cuadro Gojo', 'Cuadro decorativo minimalista con diseño de Satoru Gojo del anime Jujutsu Kaisen', 35000.00, 'satorupolera', 'CUADROS', true, true, 20);
*/
