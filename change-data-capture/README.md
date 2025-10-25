# Change Data Capture (CDC) - E-Commerce Microservices

## 📚 What is Change Data Capture (CDC)?

**Change Data Capture (CDC)** is a design pattern that identifies and captures changes made to data in a database and delivers those changes in real-time to downstream systems. Instead of polling databases or making synchronous API calls, CDC listens to the database transaction log (binlog in MySQL) and propagates changes automatically.

### Key Concepts

#### **How CDC Works:**
1. **Database Changes** → Any INSERT, UPDATE, DELETE operation
2. **Transaction Log** → MySQL binlog records all changes
3. **CDC Connector** → Debezium reads the binlog
4. **Event Stream** → Changes published to Kafka topics
5. **Consumers** → Other services react to changes

#### **Benefits:**
- ✅ **Real-time data synchronization** - Changes propagate in milliseconds
- ✅ **Low latency** - No polling delays
- ✅ **Minimal overhead** - Reads transaction logs, doesn't query tables
- ✅ **Guaranteed delivery** - No data loss
- ✅ **Event sourcing** - Complete audit trail of all changes
- ✅ **Decoupling** - Services don't need direct database access

#### **CDC vs Traditional Approaches:**

| Aspect | CDC | API Calls | Database Polling |
|--------|-----|-----------|------------------|
| Latency | Milliseconds | Seconds | Minutes |
| Database Load | Minimal | Medium | High |
| Reliability | High | Medium | Low |
| Scalability | Excellent | Good | Poor |
| Complexity | Medium | Low | Low |

## 🏗️ Project Architecture

This project implements a **real-world e-commerce scenario** with three microservices communicating via CDC:

```
┌─────────────────┐      ┌──────────────────┐      ┌─────────────────┐
│  Order Service  │      │ Inventory Service│      │ Billing Service │
│   (Port 8081)   │      │   (Port 8082)    │      │   (Port 8083)   │
└────────┬────────┘      └────────┬─────────┘      └────────┬────────┘
         │                        │                         │
         │ MySQL Binlog           │ MySQL Binlog            │ MySQL Binlog
         ▼                        ▼                         ▼
    ┌─────────┐             ┌─────────┐              ┌─────────┐
    │Debezium │             │Debezium │              │Debezium │
    │Connector│             │Connector│              │Connector│
    └────┬────┘             └────┬────┘              └────┬────┘
         │                       │                        │
         └───────────────────────┼────────────────────────┘
                                 ▼
                           ┌──────────┐
                           │  Kafka   │
                           │ (Port    │
                           │  9092)   │
                           └────┬─────┘
                                │
         ┌──────────────────────┼────────────────────────┐
         │                      │                        │
         ▼                      ▼                        ▼
  order-created          inventory-updated       billing-updated
  order-updated                                        
  order-deleted
```

### **Data Flow:**

1. **Order Created** (POST `/api/orders`)
   - Order Service: Creates order → MySQL INSERT → Debezium captures
   - Debezium: Publishes to `order-created` topic
   - Inventory Service: Consumes event → Reserves inventory
   - Inventory change → Debezium captures → Publishes to `inventory-updated`
   - Billing Service: Consumes event → Creates invoice

2. **Invoice Payment** (POST `/api/billing/invoice/{orderId}/pay`)
   - Billing Service: Marks invoice as PAID → MySQL UPDATE → Debezium captures
   - Debezium: Publishes to `billing-updated` topic
   - Order Service: Consumes event → Updates order status to PAID

## 🛠️ Technology Stack

- **Spring Boot 3.2.0** - Microservices framework
- **Debezium 2.5.0** - CDC connector
- **Apache Kafka 3.7.0** - Event streaming platform
- **MySQL 8.0** - Database with binlog
- **Maven** - Build tool
- **Lombok** - Boilerplate reduction

## 📋 Prerequisites

1. **Java 17** or higher
2. **Maven 3.8+**
3. **MySQL 8.0** running on `localhost:3306`
   - Username: `root`
   - Password: `root`
4. **Kafka 3.7.0** at `D:\kafka_2.13-3.7.0`

## ⚙️ Setup Instructions

### Step 1: Enable MySQL Binlog (REQUIRED for CDC)

CDC requires MySQL binary logging to be enabled. Add the following to your MySQL configuration file:

**Windows:** `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`
**Linux/Mac:** `/etc/mysql/my.cnf`

```ini
[mysqld]
server-id=1
log_bin=mysql-bin
binlog_format=ROW
binlog_row_image=FULL
expire_logs_days=10
```

**Restart MySQL after making changes:**
```powershell
# Windows (Run as Administrator)
net stop MySQL80
net start MySQL80

# Linux/Mac
sudo systemctl restart mysql
```

**Verify binlog is enabled:**
```sql
SHOW VARIABLES LIKE 'log_bin';
-- Should show: log_bin = ON
```

### Step 2: Initialize Database

Run the SQL script to create databases and sample data:

```powershell
mysql -u root -p < scripts\mysql-init.sql
```

Or manually execute in MySQL Workbench/CLI.

### Step 3: Start Kafka

```powershell
# Start Kafka and Zookeeper
.\scripts\start-kafka.bat

# Wait 10-15 seconds, then create topics
.\scripts\create-topics.bat
```

**Verify topics created:**
```powershell
cd D:\kafka_2.13-3.7.0
bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

You should see:
- `order-created`
- `order-updated`
- `order-deleted`
- `inventory-updated`
- `billing-updated`

### Step 4: Build the Project

```powershell
mvn clean install
```

### Step 5: Start Services

Open **3 separate terminals** and start each service:

**Terminal 1 - Order Service:**
```powershell
cd order-service
mvn spring-boot:run
```

**Terminal 2 - Inventory Service:**
```powershell
cd inventory-service
mvn spring-boot:run
```

**Terminal 3 - Billing Service:**
```powershell
cd billing-service
mvn spring-boot:run
```

**Service Ports:**
- Order Service: http://localhost:8081
- Inventory Service: http://localhost:8082
- Billing Service: http://localhost:8083

## 🧪 Testing the CDC Flow

### Test Case 1: Complete Order Flow

#### Step 1: Create an Order
```powershell
curl -X POST http://localhost:8081/api/orders -H "Content-Type: application/json" -d "{\"customerId\":\"CUST-001\",\"productId\":\"PROD-001\",\"quantity\":2,\"totalAmount\":2000.00,\"status\":\"PENDING\"}"
```

**What happens:**
1. ✅ Order created in `order_db.orders` with status `PENDING`
2. ✅ Debezium captures INSERT → Publishes to `order-created` topic
3. ✅ Inventory Service receives event → Checks stock for PROD-001
4. ✅ If stock available (50 laptops):
   - Reserves 2 units
   - Creates record in `inventory_db.inventory` with status `RESERVED`
   - Updates `products.available_stock` from 50 → 48
5. ✅ Debezium captures inventory INSERT → Publishes to `inventory-updated` topic
6. ✅ Order Service receives event → Updates order status to `INVENTORY_RESERVED`
7. ✅ Billing Service receives event → Creates invoice in `billing_db.invoices`
8. ✅ Debezium captures invoice INSERT → Publishes to `billing-updated` topic
9. ✅ Order Service receives event → Updates order status to `BILLED`

#### Step 2: Verify Order Status
```powershell
curl http://localhost:8081/api/orders/1
```

**Expected Response:**
```json
{
  "id": 1,
  "customerId": "CUST-001",
  "productId": "PROD-001",
  "quantity": 2,
  "totalAmount": 2000.00,
  "status": "BILLED",
  "createdAt": "2025-10-25T07:14:40",
  "updatedAt": "2025-10-25T07:14:42"
}
```

#### Step 3: Check Inventory
```sql
USE inventory_db;
SELECT * FROM products WHERE product_id = 'PROD-001';
-- available_stock should be 48 (50 - 2)

SELECT * FROM inventory WHERE order_id = 1;
-- status should be 'RESERVED'
```

#### Step 4: Mark Invoice as Paid
```powershell
curl -X POST http://localhost:8083/api/billing/invoice/1/pay
```

**What happens:**
1. ✅ Invoice status updated to `PAID` in `billing_db.invoices`
2. ✅ Debezium captures UPDATE → Publishes to `billing-updated` topic
3. ✅ Order Service receives event → Updates order status to `PAID`

#### Step 5: Verify Final Order Status
```powershell
curl http://localhost:8081/api/orders/1
```

**Expected: status = "PAID"**

### Test Case 2: Insufficient Inventory

```powershell
# Try ordering 100 laptops (only 48 available now)
curl -X POST http://localhost:8081/api/orders -H "Content-Type: application/json" -d "{\"customerId\":\"CUST-002\",\"productId\":\"PROD-001\",\"quantity\":100,\"totalAmount\":200000.00,\"status\":\"PENDING\"}"
```

**Expected Flow:**
1. Order created with status `PENDING`
2. Inventory Service tries to reserve → Fails (insufficient stock)
3. Inventory record created with status `FAILED`
4. Order Service updated to `INVENTORY_FAILED`
5. **No invoice created** (Billing Service only processes RESERVED inventory)

### Test Case 3: Monitor Kafka Topics

**Monitor order events:**
```powershell
cd D:\kafka_2.13-3.7.0
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic order-created --from-beginning
```

**Monitor inventory events:**
```powershell
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic inventory-updated --from-beginning
```

**Monitor billing events:**
```powershell
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic billing-updated --from-beginning
```

## 📊 Database Schema

### Order Service (order_db)

**orders table:**
```sql
CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id VARCHAR(255) NOT NULL,
  product_id VARCHAR(255) NOT NULL,
  quantity INT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
```

### Inventory Service (inventory_db)

**inventory table:**
```sql
CREATE TABLE inventory (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id VARCHAR(255) NOT NULL,
  quantity_reserved INT NOT NULL,
  available_quantity INT NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
```

**products table:**
```sql
CREATE TABLE products (
  product_id VARCHAR(50) PRIMARY KEY,
  product_name VARCHAR(255) NOT NULL,
  available_stock INT NOT NULL
);
```

### Billing Service (billing_db)

**invoices table:**
```sql
CREATE TABLE invoices (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  customer_id VARCHAR(255) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
```

## 🔍 Monitoring & Debugging

### Check Debezium Offset Files

Debezium tracks processed binlog positions in offset files:
```
offsets/
├── order-offset.dat
├── inventory-offset.dat
└── billing-offset.dat
```

**To reset CDC (reprocess all events):**
```powershell
# Stop all services
# Delete offset files
Remove-Item offsets\*.dat
# Restart services
```

### Common Issues

#### 1. **Debezium can't connect to MySQL**
```
Error: Could not find first log file name in binary log index file
```

**Solution:** Enable binlog (see Step 1) and restart MySQL

#### 2. **Services can't connect to Kafka**
```
Error: Connection to node -1 (localhost/127.0.0.1:9092) could not be established
```

**Solution:** Ensure Kafka is running:
```powershell
cd D:\kafka_2.13-3.7.0
bin\windows\kafka-server-start.bat config\server.properties
```

#### 3. **Topics not found**
```
Error: Topic order-created not present in metadata
```

**Solution:** Create topics:
```powershell
.\scripts\create-topics.bat
```

## 🎓 Learning Points

### CDC Implementation Details

**Debezium Configuration:**
- Uses embedded Debezium engine in each service
- Reads MySQL binlog in real-time
- Publishes changes to Kafka topics
- Stores offset positions for fault tolerance

**Event Format:**
```json
{
  "operation": "c",  // c=create, u=update, d=delete
  "data": {
    "id": 1,
    "customer_id": "CUST-001",
    "status": "PENDING",
    ...
  }
}
```

### Microservices Communication

**Choreography Pattern:**
- Services react to events independently
- No central orchestrator
- Loose coupling
- High scalability

### Best Practices Demonstrated

1. ✅ **Idempotent consumers** - Services handle duplicate events
2. ✅ **Event versioning** - Operation type included in payload
3. ✅ **Offset management** - Debezium tracks processing position
4. ✅ **Error handling** - Failed inventory reservation doesn't block other services
5. ✅ **Separation of concerns** - Each service owns its database

## 🚀 Production Considerations

For production deployments, consider:

1. **Kafka Connect** instead of embedded Debezium
2. **Schema Registry** for event schema management
3. **Dead Letter Queues** for failed event processing
4. **Circuit breakers** for service resilience
5. **Distributed tracing** (Zipkin/Jaeger)
6. **Monitoring** (Prometheus/Grafana)
7. **High availability** - Multi-node Kafka cluster

## 📚 Further Reading

- [Debezium Documentation](https://debezium.io/documentation/)
- [CDC Design Patterns](https://martinfowler.com/eaaDev/EventSourcing.html)
- [Kafka Streams](https://kafka.apache.org/documentation/streams/)
- [Event-Driven Architecture](https://www.confluent.io/blog/event-driven-architecture/)

## 📝 Project Structure

```
change-data-capture/
├── order-service/
│   ├── src/main/java/com/systemdesign/order/
│   │   ├── entity/          # Order, OrderStatus
│   │   ├── repository/      # OrderRepository
│   │   ├── service/         # OrderService
│   │   ├── controller/      # OrderController
│   │   ├── config/          # DebeziumConfig
│   │   └── listener/        # DebeziumListener, KafkaEventListener
│   └── pom.xml
├── inventory-service/
│   ├── src/main/java/com/systemdesign/inventory/
│   │   ├── entity/          # Inventory, Product, InventoryStatus
│   │   ├── repository/      # InventoryRepository, ProductRepository
│   │   ├── service/         # InventoryService
│   │   ├── config/          # DebeziumConfig
│   │   └── listener/        # DebeziumListener, OrderEventListener
│   └── pom.xml
├── billing-service/
│   ├── src/main/java/com/systemdesign/billing/
│   │   ├── entity/          # Invoice, InvoiceStatus
│   │   ├── repository/      # InvoiceRepository
│   │   ├── service/         # BillingService
│   │   ├── controller/      # BillingController
│   │   ├── config/          # DebeziumConfig
│   │   └── listener/        # DebeziumListener, InventoryEventListener
│   └── pom.xml
├── scripts/
│   ├── mysql-init.sql       # Database initialization
│   ├── start-kafka.bat      # Kafka startup script
│   └── create-topics.bat    # Topic creation script
├── offsets/                 # Debezium offset storage
├── pom.xml                  # Parent POM
└── README.md
```

## 👨‍💻 Author

System Design Learning Series - Change Data Capture Implementation

---

**Happy Learning! 🎉**
