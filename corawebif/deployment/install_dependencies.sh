ssh -i ~/.ssh/corawebif.pem ubuntu@$CW_HOST <<-'ENDSSH'
    sudo snap install docker
    sudo snap install aws-cli
    sudo mkdir -p /mnt/corawebif
    sudo mkdir -p /mnt/corawebif/uploads
    sudo mkdir -p /mnt/corawebif/working
    sudo mkdir -p /mnt/corawebif/nginx
    sudo chmod -R 777 /mnt/corawebif
ENDSSH

ssh -i ~/.ssh/corawebif.pem ubuntu@$CW_HOST ls /mnt/corawebif/auth_info.json
if [[ "$?" == "0" ]]
then
    echo auth_file found
else
    echo auth_file not found - copying file from ~/CORA_Website/auth_info.json
    scp -i ~/.ssh/corawebif.pem ~/CORA_Website/auth_info.json ubuntu@$CW_HOST:/mnt/corawebif/auth_info.json
fi
scp -i ~/.ssh/corawebif.pem -r ~/git/coraSchedule/RingetteSchedule/properties ubuntu@$CW_HOST:/mnt/corawebif/working/
