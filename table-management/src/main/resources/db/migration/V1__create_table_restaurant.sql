CREATE TABLE IF NOT EXISTS tables (
                                      id UUID PRIMARY KEY,
                                      number INT NOT NULL UNIQUE,
                                      capacity INT NOT NULL,
                                      status_type VARCHAR(50) NOT NULL,
    current_order_id UUID
    );

-- Insertamos algunas mesas de prueba
INSERT INTO tables (id, number, capacity, status_type) VALUES
                                                           ('550e8400-e29b-41d4-a716-446655440000', 1, 4, 'AVAILABLE'),
                                                           ('550e8400-e29b-41d4-a716-446655440001', 2, 2, 'AVAILABLE'),
                                                           ('550e8400-e29b-41d4-a716-446655440002', 3, 6, 'AVAILABLE');