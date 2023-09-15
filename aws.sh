git pull -f
./gradlew build -x test
pkill -f 'java -jar'
nohup java -jar build/libs/just-post-0.0.1-SNAPSHOT.jar &
