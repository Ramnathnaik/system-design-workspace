@echo off
echo Starting Zookeeper and Kafka...

cd D:\kafka_2.13-3.7.0

echo Starting Zookeeper...
start "Zookeeper" cmd /k bin\windows\zookeeper-server-start.bat config\zookeeper.properties

timeout /t 10

echo Starting Kafka...
start "Kafka" cmd /k bin\windows\kafka-server-start.bat config\server.properties

echo Kafka and Zookeeper started successfully!
echo Press any key to exit...
pause
