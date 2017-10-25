FROM docker.opexanalytics.com:5000/centos-node:6.0

ENV DIR /map-vis

RUN mkdir $DIR

COPY . $DIR/

RUN npm install

WORKDIR $DIR

EXPOSE 8000

CMD ["npm","run","start"]
