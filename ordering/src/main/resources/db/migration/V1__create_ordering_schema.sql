CREATE TABLE IF NOT EXISTS orders (
                                      id UUID PRIMARY KEY,
                                      table_id UUID NOT NULL,
                                      status_type VARCHAR(50) NOT NULL,
    status_timestamp TIMESTAMP NOT NULL,
    cancellation_reason VARCHAR(255),
    total_amount DECIMAL(19, 4) NOT NULL
    );

CREATE TABLE IF NOT EXISTS order_items (
                                           id UUID PRIMARY KEY,
                                           order_id UUID NOT NULL,
                                           product_id UUID NOT NULL,
                                           product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19, 4) NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id)
    );

