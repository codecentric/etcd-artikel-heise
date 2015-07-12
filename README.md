#Ein paar Unit-Tests zu etcd4j und etcd-driver

## Voraussetzungen
Um die Tests durchführen zu können wird ein blanker Etcd-Single-Node-Cluster benötigt. Hierfür am Besten folgendes Kommando ausführen.

docker run -d -p 5001:5001 -p 7001:7001 --name inttest quay.io/coreos/etcd:v2.0.12 -name inttest  -advertise-client-urls http://${DOCKER_HOST_IP}:7001  -listen-client-urls http://0.0.0.0:7001  -initial-advertise-peer-urls http://${DOCKER_HOST_IP}:5001  -listen-peer-urls http://0.0.0.0:5001  -initial-cluster-token etcd-cluster-1  -initial-cluster inttest=http://${DOCKER_HOST_IP}:5001

## Links zu den Treibern
[boon etcd driver](https://github.com/boonproject/boon/tree/master/etcd)
[etcd4j driver](https://github.com/jurmous/etcd4j)
