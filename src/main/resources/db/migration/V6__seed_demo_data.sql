CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO warehouse (name, description, street, city, state, zip_code)
SELECT 'GKN Automotive BR', 'Automotive components manufacturing plant',
       'Av. Industrial, 1500', 'Gravataí', 'RS', '94110-000'
    WHERE NOT EXISTS (SELECT 1 FROM warehouse WHERE name = 'GKN Automotive BR');


INSERT INTO users (username, email, password, role, warehouse_id)
SELECT 'Igor Admin', 'admin@flowline.com',
       crypt('Admin@2025', gen_salt('bf', 10)),
       'ADMIN', (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR')
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@flowline.com');

INSERT INTO users (username, email, password, role, warehouse_id)
SELECT 'Carlos Manager', 'manager@flowline.com',
       crypt('Manager@2025', gen_salt('bf', 10)),
       'MANAGE', (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR')
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'manager@flowline.com');

INSERT INTO users (username, email, password, role, warehouse_id)
SELECT 'Ana Operator', 'operator@flowline.com',
       crypt('Operator@2025', gen_salt('bf', 10)),
       'OPERATOR', (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR')
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'operator@flowline.com');

INSERT INTO sector (name, description, building, warehouse_id, responsible_id)
SELECT 'Receiving Dock', 'Incoming materials receiving area', 'Building A',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR'),
       (SELECT id FROM users WHERE email = 'manager@flowline.com')
    WHERE NOT EXISTS (SELECT 1 FROM sector WHERE name = 'Receiving Dock');

INSERT INTO sector (name, description, building, warehouse_id, responsible_id)
SELECT 'Assembly Line 1', 'Main automotive assembly line', 'Building B',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR'),
       (SELECT id FROM users WHERE email = 'manager@flowline.com')
    WHERE NOT EXISTS (SELECT 1 FROM sector WHERE name = 'Assembly Line 1');

INSERT INTO sector (name, description, building, warehouse_id, responsible_id)
SELECT 'Finished Goods', 'Finished products storage', 'Building C',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR'),
       (SELECT id FROM users WHERE email = 'manager@flowline.com')
    WHERE NOT EXISTS (SELECT 1 FROM sector WHERE name = 'Finished Goods');

INSERT INTO sector (name, description, building, warehouse_id, responsible_id)
SELECT 'Quality Control', 'QC inspection area', 'Building B',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR'),
       (SELECT id FROM users WHERE email = 'manager@flowline.com')
    WHERE NOT EXISTS (SELECT 1 FROM sector WHERE name = 'Quality Control');


INSERT INTO product (name, weight, unit, location, warehouse_id)
SELECT 'CV Joint Assembly', 2.450, 'unit', 'Rack A-01',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'CV Joint Assembly');

INSERT INTO product (name, weight, unit, location, warehouse_id)
SELECT 'Driveshaft 1800mm', 8.200, 'unit', 'Rack B-03',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Driveshaft 1800mm');

INSERT INTO product (name, weight, unit, location, warehouse_id)
SELECT 'Grease Cartridge 500g', 0.550, 'kg', 'Shelf C-12',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Grease Cartridge 500g');

INSERT INTO product (name, weight, unit, location, warehouse_id)
SELECT 'Bearing Kit 6205', 0.320, 'kit', 'Shelf A-08',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Bearing Kit 6205');

INSERT INTO product (name, weight, unit, location, warehouse_id)
SELECT 'Boot Kit Universal', 0.180, 'kit', 'Shelf C-04',
       (SELECT id FROM warehouse WHERE name = 'GKN Automotive BR')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Boot Kit Universal');

INSERT INTO orders (origin_sector_id, destination_sector_id, user_id, product_id, status, quantity, created_at)
SELECT
    (SELECT id FROM sector WHERE name = 'Receiving Dock'),
    (SELECT id FROM sector WHERE name = 'Assembly Line 1'),
    (SELECT id FROM users WHERE email = 'operator@flowline.com'),
    (SELECT id FROM product WHERE name = 'CV Joint Assembly'),
    'DELIVERED', 50, NOW() - INTERVAL '5 days'
WHERE NOT EXISTS (SELECT 1 FROM orders LIMIT 1);

INSERT INTO orders (origin_sector_id, destination_sector_id, user_id, product_id, status, quantity, created_at)
VALUES (
           (SELECT id FROM sector WHERE name = 'Receiving Dock'),
           (SELECT id FROM sector WHERE name = 'Quality Control'),
           (SELECT id FROM users WHERE email = 'operator@flowline.com'),
           (SELECT id FROM product WHERE name = 'Driveshaft 1800mm'),
           'DELIVERED', 12, NOW() - INTERVAL '4 days'
       );

INSERT INTO orders (origin_sector_id, destination_sector_id, user_id, product_id, status, quantity, created_at)
VALUES (
           (SELECT id FROM sector WHERE name = 'Quality Control'),
           (SELECT id FROM sector WHERE name = 'Finished Goods'),
           (SELECT id FROM users WHERE email = 'operator@flowline.com'),
           (SELECT id FROM product WHERE name = 'Driveshaft 1800mm'),
           'DELIVERED', 10, NOW() - INTERVAL '3 days'
       );

INSERT INTO orders (origin_sector_id, destination_sector_id, user_id, product_id, status, quantity, created_at)
VALUES (
           (SELECT id FROM sector WHERE name = 'Receiving Dock'),
           (SELECT id FROM sector WHERE name = 'Assembly Line 1'),
           (SELECT id FROM users WHERE email = 'operator@flowline.com'),
           (SELECT id FROM product WHERE name = 'Bearing Kit 6205'),
           'DELIVERING', 100, NOW() - INTERVAL '1 day'
       );

INSERT INTO orders (origin_sector_id, destination_sector_id, user_id, product_id, status, quantity, created_at)
VALUES (
           (SELECT id FROM sector WHERE name = 'Receiving Dock'),
           (SELECT id FROM sector WHERE name = 'Assembly Line 1'),
           (SELECT id FROM users WHERE email = 'operator@flowline.com'),
           (SELECT id FROM product WHERE name = 'Boot Kit Universal'),
           'PENDING', 75, NOW()
       );

INSERT INTO orders (origin_sector_id, destination_sector_id, user_id, product_id, status, quantity, created_at)
VALUES (
           (SELECT id FROM sector WHERE name = 'Assembly Line 1'),
           (SELECT id FROM sector WHERE name = 'Quality Control'),
           (SELECT id FROM users WHERE email = 'operator@flowline.com'),
           (SELECT id FROM product WHERE name = 'CV Joint Assembly'),
           'PENDING', 30, NOW()
       );

INSERT INTO orders (origin_sector_id, destination_sector_id, user_id, product_id, status, quantity, created_at)
VALUES (
           (SELECT id FROM sector WHERE name = 'Receiving Dock'),
           (SELECT id FROM sector WHERE name = 'Finished Goods'),
           (SELECT id FROM users WHERE email = 'operator@flowline.com'),
           (SELECT id FROM product WHERE name = 'Grease Cartridge 500g'),
           'CANCELLED', 20, NOW() - INTERVAL '2 days'
       );

INSERT INTO orders (origin_sector_id, destination_sector_id, user_id, product_id, status, quantity, created_at)
VALUES (
           (SELECT id FROM sector WHERE name = 'Receiving Dock'),
           (SELECT id FROM sector WHERE name = 'Assembly Line 1'),
           (SELECT id FROM users WHERE email = 'operator@flowline.com'),
           (SELECT id FROM product WHERE name = 'CV Joint Assembly'),
           'PENDING', 45, NOW()
       );