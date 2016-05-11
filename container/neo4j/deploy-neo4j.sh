#!/usr/bin/env sh

docker rm -f neo4j
docker run --detach --publish 7473:7473 --publish 7474:7474 --name neo4j \
                --volume /srv/neo4j/data:/data neo4j:enterprise

cp import-data.cql /srv/neo4j/data/
docker exec neo4j /var/lib/neo4j/bin/neo4j-shell -file /data/import-data.cql