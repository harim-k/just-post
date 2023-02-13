git pull
./gradlew build
pkill -f 'java -jar'
nohup java -jar build/libs/just-post-0.0.1-SNAPSHOT.jar &
