## 🗄️ Diccionario de Datos (`buyio_order_db`)

### Tabla: `orders`
Almacena las cabeceras de las órdenes de compra emitidas en el sistema.

| Campo | Tipo (Java / SQL) | Restricciones | Propósito Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `Long` / `BIGINT` | PK, Auto-increment | Identificador único de la orden de compra. |
| `order_number` | `String` / `VARCHAR(50)` | UNIQUE, NOT NULL | Código correlativo de negocio (e.g., `ORD-2026-001`). |
| `supplier_id` | `Long` / `BIGINT` | NOT NULL (FK Lógica) | Referencia al proveedor (`suppliers.id` en catalog-service). |
| `total_amount` | `BigDecimal` / `NUMERIC(12,2)` | NOT NULL | Monto total consolidado de la orden. |
| `status` 🔴 | `OrderStatus` / `VARCHAR(20)` | NOT NULL | **[ESTADO - SOFT DELETE]** Estado de la orden (`CREATED`, `ACTIVE`, `CANCELLED`). Anulación sin borrado físico. |
| `created_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha y hora de creación del registro. |
| `updated_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha y hora de la última modificación o anulación. |

### Tabla: `order_items`
Almacena los ítems o detalle de productos incluidos en cada orden de compra.

| Campo | Tipo (Java / SQL) | Restricciones | Propósito Funcional |
| :--- | :--- | :--- | :--- |
| `id` | `Long` / `BIGINT` | PK, Auto-increment | Identificador único del ítem de la orden. |
| `order_id` | `Long` / `BIGINT` | FK (`orders.id`), NOT NULL | Relación Uno a Muchos con la orden cabecera. |
| `product_id` | `Long` / `BIGINT` | NOT NULL (FK Lógica) | Referencia al producto (`products.id` en catalog-service). |
| `quantity` | `Integer` / `INT` | NOT NULL, > 0 | Cantidad solicitada del producto. |
| `unit_price` | `BigDecimal` / `NUMERIC(12,2)` | NOT NULL, >= 0 | Precio unitario congelado al momento de la compra. |
| `subtotal` | `BigDecimal` / `NUMERIC(12,2)` | NOT NULL | Subtotal calculado (`quantity * unit_price`). |
| `created_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha y hora de creación del ítem. |
| `updated_at` 🟡 | `LocalDateTime` / `TIMESTAMP` | NOT NULL | **[BITÁCORA]** Fecha y hora de modificación del ítem. |