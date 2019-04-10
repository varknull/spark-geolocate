FROM java:openjdk-8

ENV HADOOP_HOME /opt/spark/hadoop-2.7.1
ENV SBT_VERSION 1.2.8
ENV SCALA_VERSION 2.12.8

#RUN mkdir /opt/spark
#WORKDIR /opt/spark

# Install Scala
RUN \
  cd /root && \
  curl -o scala-$SCALA_VERSION.tgz http://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz && \
  tar -xf scala-$SCALA_VERSION.tgz && \
  rm scala-$SCALA_VERSION.tgz && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

# Update sbt package
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb

RUN update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

RUN \
  wget http://apache.mirror.anlx.net/spark/spark-2.4.1/spark-2.4.1-bin-hadoop2.7.tgz && \
    tar -xvzf spark-2.4.1-bin-hadoop2.7.tgz && \
    rm -f spark-2.4.1-bin-hadoop2.7.tgz
