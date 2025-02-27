# Vehicle Rental System with MongoDB and Redis databases
This project is a Vehicle Rental System implemented in Java. It provides functionalities for managing clients, vehicles, and rentals, including CRUD operations, business logic for ending rentals,changing vehicle status etc. and caching mechanisms using Redis.

## Project Overview
The system allows for:
- **CRUD operations** on clients, client types, vehicles, and rentals.
- **Business operations** such as ending a rental (archiving it), updating client active rentals, updating vehicle status (rented/free).
- **Caching** of frequently accessed data using Redis (in this case vehicles were chosen to be cached).
- **Data consistency** ensured through MongoDB replica sets and Redis caching.
- **Performance benchmarks** to compare MongoDB and Redis read/write operations.

## Database Structure
### MongoDB Collections
1. **Clients** - Fields: id, FirstName, LastName, email, City, Street, Number, clientTypeId, activeRents.
2. **ClientTypes** - Fields: id, discriminator (e.g., Default, Silver, Gold), discount, maxVehicles.
3. **Vehicles** - Fields: id, plateNumber, discriminator (e.g., bicycle, car, moped), rentedStatus, archive, basePrice. Other fields like transmissionType or engineDisplacement (based on the vehicle type).
4. **Rentals** - Fields: id, clientObject, vehicleObject, startDate, endDate, rentCost. There two collections for active and archive rentals.

## Caching with Redis
1. **Cached Data** - Vehicles: Frequently accessed vehicle data is cached. The key of the cached vehicle expires in 20 seconds.
2. **Cache Invalidation** - Cache is invalidated when a vehicle's rented status changes, a vehicle is updated or deleted or the key expired.
3. **Fallback Mechanism** - If Redis is unavailable or it does not contain data of the requested vehicle, data is fetched directly from MongoDB.

## Business Logic
1. **Renting a Vehicle**
- Checks if the vehicle is available (rented is false).
- Checks if the number of client's current rents reached the max allowed number. If it is the rent operation will not succeed.
- Updates the vehicle's rented to true.
- Increases the client's activeRents count.
2. **Ending a Rental**
- Updates the vehicle's rented status to false.
- Decreases the client's activeRents count.
- Archives the rental (moves rent from active rents to archive rents collection).

## Setup and Testing the Project
### Prerequisites
- Docker
- Java 21
- Maven

### Setup
- Clone this repository.
- Set up MongoDB replica set and Redis by running services directly from docker-compose using IDE or by executing ***docker-compose up -d*** command.

### Testing
- Run all **JUnit** tests using: ***mvn test*** or use your IDE to run tests. 
- To run a Benchmark test go to **BenchmarkTest** class using your IDE and run test.

## Performance Benchmarks
### JMH Benchmarks
- Benchmarks are implemented to measure:
    - Read/write performance of MongoDB.
    - Read/write performance of Redis.
    - Performance with and without cache hits.

### Results
- **Redis** is significantly faster for read operations compared to MongoDB.
- **Cache misses** introduce a slight delay due to fallback to MongoDB.
The following test output summarizes the benchmark results:
```
Benchmark                                  Mode  Cnt   Score   Error  Units
BenchmarkTest.findAllCache                   ss        3,505          ms/op
BenchmarkTest.findAllMongo                   ss        2,623          ms/op
BenchmarkTest.findAllRedis                   ss        0,942          ms/op
BenchmarkTest.findByIdInCache                ss        1,212          ms/op
BenchmarkTest.findByIdNotInCache             ss       11,143          ms/op
BenchmarkTest.findByPlateNumberInCache       ss        1,285          ms/op
BenchmarkTest.findByPlateNumberMongo         ss        2,149          ms/op
BenchmarkTest.findByPlateNumberNotInCache    ss       12,001          ms/op
BenchmarkTest.findByPlateNumberRedis         ss        0,915          ms/op
BenchmarkTest.saveVehicleMongo               ss        9,323          ms/op
BenchmarkTest.saveVehicleRedis               ss        1,683          ms/op
```
1. **Redis vs. MongoDB Read Performance:**
- Fetching data from Redis cache (findByIdInCache, findByPlateNumberInCache) is ~10x faster than fetching from MongoDB when the data is not in the cache (findByIdNotInCache, findByPlateNumberNotInCache). When data is not in the cache, the system incurs a significant performance penalty due to the additional time required to fetch data from MongoDB. 
2. **Write Performance:**
- Writing to Redis (saveVehicleRedis) is faster than writing to MongoDB (saveVehicleMongo).
3. **Hybrid Cache Performance:**
- The hybrid approach (e.g. findAllCache) that combines Redis and MongoDB is slower than pure Redis or pure Mongo performance because it makes more operations, including checking redis connetion, searching for data in Redis and if not found the data is fetched from Mongo. If data are present in Redis the reading operation will be faster (e.g. findByIdInCache - 1,212 ms/op), but not as fast as pure Redis read operation due to connection verification. If data are not present in Redis it needs to be extracted from Mongo, which significantly increases the time of the overall operation (findByIdNotInCache - 11,143 ms/op).

## Authors
### Wiktoria Bilecka
### Grzegorz Janasek
