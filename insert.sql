-- -- ========================================
-- -- Insert mock data for helloDevops Project
-- -- Compatible with V1__init_ecommerce.sql
-- -- ========================================

-- -- Users
-- INSERT INTO users (user_id, name, email, phone_number, address, user_type)
-- VALUES
-- (1, 'Ploy',  'ploy@example.com',  '0812345678', 'Bangkok, Thailand', 'admin'),
-- (2, 'Grace', 'grace@example.com', '0812345679', 'Bangkok, Thailand', 'admin'),
-- (3, 'Mint',  'mint@example.com',  '0812345680', 'Chiang Mai, Thailand', 'customer'),
-- (4, 'Fluke', 'fluke@example.com', '0812345681', 'Khon Kaen, Thailand', 'customer'),
-- (5, 'Pair',  'pair@example.com',  '0812345682', 'Phuket, Thailand', 'customer');

-- -- Authen (mapping user_id + password hash)
-- INSERT INTO authen (auth_id, user_id, email, password_hash)
-- VALUES
-- (1, 1, 'ploy@example.com',  'admin123'),
-- (2, 2, 'grace@example.com', 'admin123'),
-- (3, 3, 'mint@example.com',  'user123'),
-- (4, 4, 'fluke@example.com', 'user123'),
-- (5, 5, 'pair@example.com',  'user123');

-- -- Categories
-- INSERT INTO categories (category_id, category_name, category_image)
-- VALUES
-- (1, 'Fruits', '/images/cat_fruits.png'),
-- (2, 'Vegetables', '/images/cat_vegetables.png'),
-- (3, 'Beverages', '/images/cat_beverages.png');

-- -- Products
-- INSERT INTO products (product_id, category_id, product_name, description, price, product_image)
-- VALUES
-- (1, 1, 'Apple Fuji', 'Fresh Fuji apples', 50.00, '/images/products/apple.png'),
-- (2, 1, 'Strawberry', 'Fresh strawberries', 60.00, '/images/products/strawberry.png'),
-- (3, 2, 'Broccoli', 'Organic broccoli', 25.00, '/images/products/broccoli.png'),
-- (4, 3, 'Orange Juice', '100% natural orange juice', 35.00, '/images/products/orange_juice.png'),
-- (5, 3, 'Mineral Water 500ml', 'Pure mineral water', 15.00, '/images/products/water.png');

-- -- Carts (หนึ่ง user มีตะกร้าเดียว)
-- INSERT INTO carts (cart_id, user_id)
-- VALUES
-- (1, 3),
-- (2, 4),
-- (3, 5);

-- -- Cart Items
-- INSERT INTO cart_items (cart_item_id, cart_id, product_id, quantity, price_each)
-- VALUES
-- (1, 1, 1, 2, 50.00),   -- Mint ใส่ Apple Fuji 2 ชิ้น
-- (2, 1, 5, 1, 15.00),   -- Mint ใส่ Water 1 ชิ้น
-- (3, 2, 3, 2, 25.00),   -- Fluke ใส่ Broccoli 2 ชิ้น
-- (4, 3, 2, 1, 60.00);   -- Pair ใส่ Strawberry 1 ชิ้น

-- -- Orders
-- INSERT INTO orders (order_id, user_id, status, shipping_address, discount_amount, total_amount)
-- VALUES
-- (1, 3, 'completed', 'Chiang Mai, Thailand', 0.00, 115.00),
-- (2, 4, 'pending',   'Khon Kaen, Thailand',  0.00, 50.00),
-- (3, 5, 'shipped',   'Phuket, Thailand',     5.00, 55.00);

-- -- Order Items
-- INSERT INTO order_items (order_item_id, order_id, product_id, product_name, quantity, price_each)
-- VALUES
-- (1, 1, 1, 'Apple Fuji', 2, 50.00),
-- (2, 1, 5, 'Mineral Water 500ml', 1, 15.00),
-- (3, 2, 3, 'Broccoli', 2, 25.00),
-- (4, 3, 2, 'Strawberry', 1, 60.00);
