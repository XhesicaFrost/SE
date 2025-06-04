-- 清空并重新插入测试店铺数据
DELETE FROM shops;
INSERT INTO shops (name, description, image, type, rating, sales, min_price, max_price, delivery_fee, delivery_time, address, latitude, longitude, phone, business_hours, is_open, created_at, updated_at)
VALUES (
    '测试店铺',
    '这是一个测试店铺',
    'https://example.com/shop1.jpg',
    '中餐',
    4.5,
    100,
    20.0,
    100.0,
    5.0,
    30,
    '北京市海淀区中关村大街1号',
    39.9846,
    116.3107,
    '010-12345678',
    '10:00-22:00',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
); 