@echo off
echo Creating Kafka Topics for CDC...

cd D:\kafka_2.13-3.7.0

echo Creating order-created topic...
bin\windows\kafka-topics.bat --create --topic order-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

echo Creating order-updated topic...
bin\windows\kafka-topics.bat --create --topic order-updated --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

echo Creating order-deleted topic...
bin\windows\kafka-topics.bat --create --topic order-deleted --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

echo Creating inventory-updated topic...
bin\windows\kafka-topics.bat --create --topic inventory-updated --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

echo Creating billing-updated topic...
bin\windows\kafka-topics.bat --create --topic billing-updated --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

echo.
echo All topics created successfully!
echo.
echo Listing all topics:
bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

pause
