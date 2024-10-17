scp -q -i ~/.ssh/corawebif.pem -r nginx ubuntu@$CW_HOST:/mnt/corawebif/nginx/
ssh -i ~/.ssh/corawebif.pem ubuntu@$CW_HOST <<-'ENDSSH'
    set -e
    set -x
    export MY_IP=`curl http://169.254.169.254/latest/meta-data/local-ipv4`
    sed -i -e "s/@NGINX_DOCKER_HOST@/$MY_IP/" /mnt/corawebif/nginx/nginx/proxy-confs/corawebif.subfolder.conf
    sed -i -e "s/@NGINX_DOCKER_HOST@/$MY_IP/" /mnt/corawebif/nginx/nginx/proxy-confs/corawebifswerk.subfolder.conf
    docker stop swag
    docker rm swag
    docker run -d --name swag --cap-add=NET_ADMIN -e PUID=1000 -e PGID=1000 -e URL=coraweb.net -e VALIDATION=http -p 443:443 -p 80:80 -v /mnt/corawebif/nginx:/config linuxserver/swag
    set +e
    docker rmi `docker images -q`
ENDSSH

