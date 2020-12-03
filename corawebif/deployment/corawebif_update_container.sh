ssh -i ~/.ssh/corawebif.pem ubuntu@$CW_HOST <<-'ENDSSH'
    `aws ecr get-login --region ca-central-1 --no-include-email`
    docker stop corawebif
    docker rm corawebif
    docker rmi `docker images -q`
    docker run -d --name corawebif -v /mnt/corawebif:/mnt/corawebif -e CW_FILE_PATH=/mnt/corawebif -e CW_MAIN_CLASS=schedule.ScheduleMaker -e CW_CLASS_PATH=/home/cora/RingetteSchedule-0.0.1-SNAPSHOT-jar-with-dependencies.jar -p 8080:8080 353923860258.dkr.ecr.ca-central-1.amazonaws.com/corawebif 
ENDSSH

