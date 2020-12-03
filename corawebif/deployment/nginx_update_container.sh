scp -q -i ~/.ssh/corawebif.pem nginx.conf nginx-selfsigned.crt nginx-selfsigned.key ubuntu@$CW_HOST:/mnt/corawebif/nginx
ssh -i ~/.ssh/corawebif.pem ubuntu@$CW_HOST <<-'ENDSSH'
    set -e
    set -x
    export MY_IP=`curl http://169.254.169.254/latest/meta-data/local-ipv4`
    sed -i -e "s/@NGINX_DOCKER_HOST@/$MY_IP/" /mnt/corawebif/nginx/nginx.conf
    docker stop nginx
    docker rm nginx
    docker run --name nginx -v /mnt/corawebif/nginx:/etc/nginx:ro -p 443:443 -p 80:80 -d nginx
ENDSSH

