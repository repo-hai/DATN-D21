-- Seed demo products for cart testing.
-- Import this file after the schema has been created.
-- The script uses fixed IDs so the inserted products are easy to test.

START TRANSACTION;

INSERT INTO `product` (
  `id`, `create_date`, `description`, `image`, `name`, `p_sku`, `status`, `category_id`
) VALUES
  (
    'demo-prod-cart-001',
    CURDATE(),
    'San pham demo de kiem tra them vao gio hang',
    'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80',
    'Demo Cart Phone Case',
    'DEMO-CART-001',
    1,
    12
  ),
  (
    'demo-prod-cart-002',
    CURDATE(),
    'San pham demo thu hai de kiem tra luong dat hang',
    'https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=900&q=80',
    'Demo Cart Charger',
    'DEMO-CART-002',
    1,
    12
  )
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `description` = VALUES(`description`),
  `image` = VALUES(`image`),
  `status` = VALUES(`status`),
  `category_id` = VALUES(`category_id`);

INSERT INTO `product_variant` (
  `id`, `color`, `sku`, `product_id`, `sort_index`
) VALUES
  (
    'demo-variant-cart-001',
    'Den',
    'DEMO-CART-001-BLACK',
    'demo-prod-cart-001',
    0
  ),
  (
    'demo-variant-cart-002',
    'Trang',
    'DEMO-CART-002-WHITE',
    'demo-prod-cart-002',
    0
  )
ON DUPLICATE KEY UPDATE
  `color` = VALUES(`color`),
  `sku` = VALUES(`sku`),
  `product_id` = VALUES(`product_id`),
  `sort_index` = VALUES(`sort_index`);

INSERT INTO `product_attribute` (
  `id`, `discount`, `final_price`, `name`, `original_price`,
  `sold_quantity`, `status`, `stock_quantity`, `variant_id`
) VALUES
  (
    'demo-attr-cart-001',
    10,
    99000,
    'Ban tieu chuan',
    110000,
    0,
    0,
    25,
    'demo-variant-cart-001'
  ),
  (
    'demo-attr-cart-002',
    0,
    249000,
    'Cong suat 30W',
    249000,
    0,
    0,
    15,
    'demo-variant-cart-002'
  )
ON DUPLICATE KEY UPDATE
  `discount` = VALUES(`discount`),
  `final_price` = VALUES(`final_price`),
  `name` = VALUES(`name`),
  `original_price` = VALUES(`original_price`),
  `sold_quantity` = VALUES(`sold_quantity`),
  `status` = VALUES(`status`),
  `stock_quantity` = VALUES(`stock_quantity`),
  `variant_id` = VALUES(`variant_id`);

INSERT INTO `product_image` (
  `id`, `url`, `product_id`, `variant_id`, `sort_index`
) VALUES
  (
    'demo-product-image-cart-001',
    'https://images.unsplash.com/photo-1601593346740-925612772716?auto=format&fit=crop&w=900&q=80',
    'demo-prod-cart-001',
    NULL,
    0
  ),
  (
    'demo-variant-image-cart-001',
    'https://images.unsplash.com/photo-1603314585442-ee3b3c16fbcf?auto=format&fit=crop&w=900&q=80',
    NULL,
    'demo-variant-cart-001',
    0
  ),
  (
    'demo-product-image-cart-002',
    'https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=900&q=80',
    'demo-prod-cart-002',
    NULL,
    0
  ),
  (
    'demo-variant-image-cart-002',
    'https://images.unsplash.com/photo-1615526675159-e248c3021d3f?auto=format&fit=crop&w=900&q=80',
    NULL,
    'demo-variant-cart-002',
    0
  )
ON DUPLICATE KEY UPDATE
  `url` = VALUES(`url`),
  `product_id` = VALUES(`product_id`),
  `variant_id` = VALUES(`variant_id`),
  `sort_index` = VALUES(`sort_index`);

COMMIT;

-- Quick test IDs after import:
-- Product 1: demo-prod-cart-001
-- Attribute 1: demo-attr-cart-001
-- Product 2: demo-prod-cart-002
-- Attribute 2: demo-attr-cart-002
