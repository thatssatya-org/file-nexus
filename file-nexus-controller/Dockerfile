FROM 317536284039.dkr.ecr.ap-south-1.amazonaws.com/base_images:pe_jdk17.3

EXPOSE 8080

VOLUME /var/log/filenexus

RUN groupadd --system --gid="9999" filenexus && \
    useradd --system  --uid="9999" --gid="filenexus" filenexus

# Create required path and add jar files
RUN mkdir -p /srv/www/application/current
# Create this folder so that config can be injected to docker
RUN mkdir -p /srv/www/application/current/config
# Create this folder and put local config
RUN mkdir -p /srv/www/application/current/local-config

WORKDIR /srv/www/application/current

COPY ./target/file-nexus*.jar .
RUN mv file-nexus*.jar file-nexus.jar

COPY ./target/classes/application.yml ./local-config/application.yml

COPY run.sh .

COPY opentelemetry-javaagent.jar .

# Define all default env variables
ENV JMX_PORT="9000"
ENV VM_MEM="-Xms512m -Xmx512m"
ENV VM_MIN_META_MEM="256m"
ENV VM_MAX_META_MEM="256m"
ENV TIMEZONE_STR="Asia/Kolkata"
ENV OTEL_AGENT="-javaagent:/srv/www/application/current/opentelemetry-javaagent.jar"

USER filenexus

CMD ["/bin/bash", "run.sh"]
