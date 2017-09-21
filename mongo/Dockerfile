FROM docker.opexanalytics.com:5000/centos:7

RUN echo -e "[mongodb-org-3.4]\nname=MongoDB Repository\nbaseurl=https://repo.mongodb.org/yum/redhat/7/mongodb-org/3.4/x86_64/\ngpgcheck=1\nenabled=1\ngpgkey=https://www.mongodb.org/static/pgp/server-3.4.asc" > /etc/yum.repos.d/mongodb.repo && \
	yum -y update && \
	yum install -y mongodb-org-server-3.4.1 mongodb-org-shell-3.4.1 mongodb-org-mongos-3.4.1 && \
	yum clean all && \
	mkdir -p /data/db && \
	mkdir -p /data/configdb

VOLUME /data/db

EXPOSE 27017
