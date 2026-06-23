-- ==========================================
-- 1. INVENTORY (Raw Materials Stock)
-- ==========================================
-- Existing items (Scaled Up)
INSERT INTO inventory (component_item_id, component_name, inventory_date, available_quantity)
VALUES (1, 'Pencil', '2026-06-23', 1000);
INSERT INTO inventory (component_item_id, component_name, inventory_date, available_quantity)
VALUES (2, 'Eraser', '2026-06-23', 3000);

-- New items added to the inventory
INSERT INTO inventory (component_item_id, component_name, inventory_date, available_quantity)
VALUES (3, 'Pen', '2026-06-23', 800);
INSERT INTO inventory (component_item_id, component_name, inventory_date, available_quantity)
VALUES (4, 'Notebook', '2026-06-23', 500);
INSERT INTO inventory (component_item_id, component_name, inventory_date, available_quantity)
VALUES (5, 'Ruler', '2026-06-23', 1200);


-- ==========================================
-- 2. GROUP ITEMS (Recipes & Demand)
-- ==========================================
-- Item A (Needs 1 Pencil + 1 Eraser) | Demand: 1500 | Priority: 1
INSERT INTO group_items (group_item_id, group_item_name, component_item_id, quantity_required, priority, demand)
VALUES (1, 'Item A', 1, 1, 1, 1500);
INSERT INTO group_items (group_item_id, group_item_name, component_item_id, quantity_required, priority, demand)
VALUES (1, 'Item A', 2, 1, 1, 1500);

-- Item B (Needs 1 Pencil) | Demand: 500 | Priority: 2
INSERT INTO group_items (group_item_id, group_item_name, component_item_id, quantity_required, priority, demand)
VALUES (2, 'Item B', 1, 1, 2, 500);

-- NEW: Item C - "Study Kit" (Needs 1 Pen + 1 Notebook) | Demand: 600 | Priority: 1
INSERT INTO group_items (group_item_id, group_item_name, component_item_id, quantity_required, priority, demand)
VALUES (3, 'Item C', 3, 1, 1, 600);
INSERT INTO group_items (group_item_id, group_item_name, component_item_id, quantity_required, priority, demand)
VALUES (3, 'Item C', 4, 1, 1, 600);

-- NEW: Item D - "Drafting Kit" (Needs 2 Pencils + 1 Ruler + 1 Notebook) | Demand: 400 | Priority: 3
INSERT INTO group_items (group_item_id, group_item_name, component_item_id, quantity_required, priority, demand)
VALUES (4, 'Item D', 1, 2, 3, 400);
INSERT INTO group_items (group_item_id, group_item_name, component_item_id, quantity_required, priority, demand)
VALUES (4, 'Item D', 5, 1, 3, 400);
INSERT INTO group_items (group_item_id, group_item_name, component_item_id, quantity_required, priority, demand)
VALUES (4, 'Item D', 4, 1, 3, 400);