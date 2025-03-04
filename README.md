# JRedis

Solution to the Build Your Own Redis Server [CodingChallenge](https://codingchallenges.fyi/challenges/challenge-redis) by John Crickett.

### Features

- `RESP` (Redis Serialization Procol) v2
- Commands Implemented:
  - **PING**
  - **SET** (with EX, PX EAXT PXAT options)
  - **GET**
  - **EXISTS**
  - **DEL**
  - **INCR**
  - **DECR**
  - **LPUSH**
  - **RPUSH**
  - **LRANGE**
  - **SAVE**

### Usage

- Install `Java 17`.
- Add `JAVA_HOME` to `PATH`.
- Download `Maven`.
- Add `MAVEN_HOME` to `PATH`.
- Fork the repository.

### Download Dependencies

- `cd` into project base directory.
- `mvn dependency:copy-dependencies`
- Dependencies:
  - Log4J2
  - Jedis
  - JUnit5

### Package

- `cd` into project base directory.
- `mvn clean package -DskipTests`.
- `mvn clean package`.

### Run

- After package section `JAR` file created under `target` folder.
- run `java -jar .\target\jredis-1.0-SNAPSHOT.jar`.
- Try out server with `Redis CLI` and `Jedis` client for Redis.

### Redis for Windows

- Download Redis Suite [Redis-x64-3.0.504.msi](https://github.com/microsoftarchive/redis/releases).
- Open terminal in `"C:\Program Files\Redis"`
- Will contain: 
  - `redis-cli.exe`
  - `redis-benchmark.exe`
  - `redis-server.exe`

### Run Tests

- `cd` into project base directory.
- `mvn test`.

### Benchmark

- `.\redis-benchmark -t set,get, -n 100000 -q`:
  - `SET`: 25913.45 requests per second
  - `GET`: 27188.69 requests per second