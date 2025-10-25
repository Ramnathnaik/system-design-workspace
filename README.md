# System Design Workspace 🏗️

A comprehensive learning series exploring **System Design concepts** through hands-on implementations. Each project demonstrates real-world distributed system patterns with working code, detailed explanations, and practical examples.

## 🎯 Purpose

This repository serves as a learning workspace where complex system design patterns are broken down and implemented from scratch. Instead of just theoretical knowledge, you'll find fully functional projects that you can run, test, and modify to understand how distributed systems work in practice.

## 📚 Projects

### 1. [Change Data Capture (CDC)](./change-data-capture/)
**Status:** ✅ Complete | **Difficulty:** Intermediate

An e-commerce microservices system demonstrating real-time data synchronization using Change Data Capture pattern.

**What You'll Learn:**
- How CDC captures database changes in real-time
- Event-driven microservices architecture
- Debezium integration with MySQL binlog
- Kafka event streaming
- Distributed transaction patterns
- Service choreography

**Tech Stack:** Spring Boot, Debezium, Apache Kafka, MySQL

**Key Features:**
- 3 microservices (Order, Inventory, Billing)
- Real-time inventory reservation
- Automatic invoice generation
- Event-driven communication
- Complete API testing suite

[📖 View Project Documentation →](./change-data-capture/README.md)

---

## 🛠️ Technology Stack

Each project may use different technologies, but commonly includes:

- **Languages:** Java, Spring Boot
- **Databases:** MySQL, PostgreSQL
- **Message Brokers:** Apache Kafka, RabbitMQ
- **CDC Tools:** Debezium
- **Containerization:** Docker (where applicable)
- **Build Tools:** Maven

## 🚀 Getting Started

Each project has its own detailed README with:
- ✅ Concept explanation
- ✅ Architecture diagrams
- ✅ Prerequisites
- ✅ Setup instructions
- ✅ Testing scenarios
- ✅ Troubleshooting guide

Navigate to individual project folders to get started!

## 📖 Learning Path

**Recommended Order:**
1. **Change Data Capture** - Understand event-driven architecture fundamentals
2. More projects coming soon...

## 🎓 Who Is This For?

- Software engineers learning distributed systems
- Backend developers exploring microservices patterns
- Anyone preparing for system design interviews
- Practitioners wanting hands-on experience with real implementations

## 📋 Project Structure

```
system-design-workspace/
├── change-data-capture/          # CDC implementation with microservices
│   ├── order-service/
│   ├── inventory-service/
│   ├── billing-service/
│   ├── scripts/
│   └── README.md
├── [future-project-1]/           # Coming soon
├── [future-project-2]/           # Coming soon
└── README.md                     # This file
```

## 🔮 Upcoming Projects

Future topics planned for this workspace:

- **API Gateway Pattern** - Load balancing, routing, and authentication
- **CQRS & Event Sourcing** - Command Query Responsibility Segregation
- **Saga Pattern** - Distributed transaction management
- **Circuit Breaker** - Fault tolerance and resilience
- **Rate Limiting** - API throttling strategies
- **Caching Strategies** - Redis, distributed caching
- **Sharding & Partitioning** - Database scaling patterns
- **Message Queue Patterns** - RabbitMQ, SQS implementations

*Stay tuned for more!*

## 🤝 Contributing

This is primarily a learning repository, but suggestions and improvements are welcome! Feel free to:

- Open issues for questions or clarifications
- Suggest new system design patterns to implement
- Report bugs or improvements in existing projects
- Share your learning experience

## 📝 Notes

- All projects are designed for **learning purposes**
- Production-ready considerations are noted but not always fully implemented
- Code is optimized for **clarity and understanding** over performance
- Each project includes **extensive documentation** to explain design decisions

## 📬 Contact

**Author:** Ramnath Naik  
**Purpose:** System Design Learning Series

---

## ⭐ Star This Repository

If you find this workspace helpful for your system design learning journey, please consider giving it a star! It helps others discover these resources.

---

**Happy Learning! 🎉**

*Building distributed systems one pattern at a time.*
