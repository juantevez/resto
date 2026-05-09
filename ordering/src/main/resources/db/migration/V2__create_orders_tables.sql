CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        table_id UUID NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        status_timestamp TIMESTAMP,
                        cancellation_reason VARCHAR(255)
);

CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id UUID REFERENCES orders(id),
                             product_id UUID NOT NULL,
                             product_name VARCHAR(255) NOT NULL,
                             quantity INTEGER NOT NULL,
                             unit_price DECIMAL(19, 2) NOT NULL
);
