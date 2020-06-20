#! /bin/sh

KEY_SIZE=${1:-4096}

AGENT_APP_NAME=${2:-agentapp}
AGENT_APP_ALIAS=${3:-agentapp}
AGENT_APP_KEYSTORE_PASSWORD=${4:-agentapp_pass}

ZUUL_NAME=${5:-zuul}
ZUUL_ALIAS=${6:-zuul}
ZUUL_KEYSTORE_PASSWORD=${7:-zuul_pass}

ADVERTISEMENTS_NAME=${8:-advertisements}
ADVERTISEMENTS_ALIAS=${9:-advertisements}
ADVERTISEMENTS_KEYSTORE_PASSWORD=${10:-advertisements_pass}

CARS_NAME=${11:-cars}
CARS_ALIAS=${12:-cars}
CARS_KEYSTORE_PASSWORD=${13:-cars_pass}

EUREKA_NAME=${14:-eureka}
EUREKA_ALIAS=${15:-eureka}
EUREKA_KEYSTORE_PASSWORD=${16:-eureka_pass}

LOGGER_NAME=${17:-logger}
LOGGER_ALIAS=${18:-logger}
LOGGER_KEYSTORE_PASSWORD=${19:-logger_pass}

RENTING_NAME=${20:-renting}
RENTING_ALIAS=${21:-renting}
RENTING_KEYSTORE_PASSWORD=${22:-renting_pass}

SEARCH_NAME=${23:-search}
SEARCH_ALIAS=${24:-search}
SEARCH_KEYSTORE_PASSWORD=${25:-search_pass}

USERS_NAME=${26:-users}
USERS_ALIAS=${27:-users}
USERS_KEYSTORE_PASSWORD=${28:-users_pass}

AGENT_APP_CLIENT_NAME=${29:-agentapp_client}
AGENT_APP_CLIENT_ALIAS=${30:-agentapp_client}
AGENT_APP_CLIENT_KEYSTORE_PASSWORD=${31:-agentapp_client_pass}

MAIN_APP_CLIENT_NAME=${32:-mainapp_client}
MAIN_APP_CLIENT_ALIAS=${33:-mainapp_client}
MAIN_APP_CLIENT_KEYSTORE_PASSWORD=${34:-mainapp_client_pass}


# Root CA
mkdir -p ca/root-ca/private ca/root-ca/db crl certs
chmod 700 ca/root-ca/private

# Create database for Root CA
cp /dev/null ca/root-ca/db/root-ca.db
cp /dev/null ca/root-ca/db/root-ca.db.attr
echo 01 > ca/root-ca/db/root-ca.crt.srl
echo 01 > ca/root-ca/db/root-ca.crl.srl

# Create Root CA request
openssl req \
    -new \
    -nodes \
    -config etc/root-ca.conf \
    -out ca/root-ca.csr \
    -keyout ca/root-ca/private/root-ca.key || exit

# Create Root CA certificate
openssl ca \
    -selfsign \
    -rand_serial \
    -batch \
    -config etc/root-ca.conf \
    -in ca/root-ca.csr \
    -out ca/root-ca.crt \
    -extensions root_ca_ext || exit

# Create initial CRL
openssl ca \
    -gencrl \
    -config etc/root-ca.conf \
    -out crl/root-ca.crl || exit

# TLS CA
mkdir -p ca/tls-ca/private ca/tls-ca/db crl certs
chmod 700 ca/tls-ca/private

# Create database for TLS CA
cp /dev/null ca/tls-ca/db/tls-ca.db
cp /dev/null ca/tls-ca/db/tls-ca.db.attr
echo 01 > ca/tls-ca/db/tls-ca.crt.srl
echo 01 > ca/tls-ca/db/tls-ca.crl.srl

# Create TLS CA request
openssl req \
    -new \
    -nodes \
    -config etc/tls-ca.conf \
    -out ca/tls-ca.csr \
    -keyout ca/tls-ca/private/tls-ca.key || exit

# Create TLS CA certificate
openssl ca \
    -rand_serial \
    -batch \
    -config etc/root-ca.conf \
    -in ca/tls-ca.csr \
    -out ca/tls-ca.crt \
    -extensions signing_ca_ext || exit

# Create initial CRL
openssl ca \
    -gencrl \
    -config etc/tls-ca.conf \
    -out crl/tls-ca.crl || exit

cat ca/tls-ca.crt ca/root-ca.crt > \
    ca/tls-ca-chain.pem


mkdir -p "certs/${AGENT_APP_NAME}/keystore"

# Create TLS server request - Agent App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.csr" \
    -keyout "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Rent-a-Car Agent App" || exit

# Create TLS server certificate - Agent App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.csr" \
    -out "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Agent App
openssl pkcs12 \
    -export \
    -name "${AGENT_APP_ALIAS}" \
    -inkey "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.key" \
    -in "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${AGENT_APP_KEYSTORE_PASSWORD}" \
    -out "certs/${AGENT_APP_NAME}/keystore/${AGENT_APP_NAME}.keystore.p12" || exit


mkdir -p "certs/${ZUUL_NAME}/keystore"

# Create TLS server request - Main App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${ZUUL_NAME}/${ZUUL_NAME}.csr" \
    -keyout "certs/${ZUUL_NAME}/${ZUUL_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Zuul gateway" || exit

# Create TLS server certificate - Main App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${ZUUL_NAME}/${ZUUL_NAME}.csr" \
    -out "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Main App
openssl pkcs12 \
    -export \
    -name "${ZUUL_ALIAS}" \
    -inkey "certs/${ZUUL_NAME}/${ZUUL_NAME}.key" \
    -in "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${ZUUL_KEYSTORE_PASSWORD}" \
    -out "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.keystore.p12" || exit


mkdir -p "certs/${ADVERTISEMENTS_NAME}/keystore"

# Create TLS server request - Agent App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.csr" \
    -keyout "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Advertisements service" || exit

# Create TLS server certificate - Agent App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.csr" \
    -out "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Agent App
openssl pkcs12 \
    -export \
    -name "${ADVERTISEMENTS_ALIAS}" \
    -inkey "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.key" \
    -in "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -out "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.keystore.p12" || exit


mkdir -p "certs/${CARS_NAME}/keystore"

# Create TLS server request - Main App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${CARS_NAME}/${CARS_NAME}.csr" \
    -keyout "certs/${CARS_NAME}/${CARS_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Cars service" || exit

# Create TLS server certificate - Main App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${CARS_NAME}/${CARS_NAME}.csr" \
    -out "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Main App
openssl pkcs12 \
    -export \
    -name "${CARS_ALIAS}" \
    -inkey "certs/${CARS_NAME}/${CARS_NAME}.key" \
    -in "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${CARS_KEYSTORE_PASSWORD}" \
    -out "certs/${CARS_NAME}/keystore/${CARS_NAME}.keystore.p12" || exit


mkdir -p "certs/${EUREKA_NAME}/keystore"

# Create TLS server request - Main App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${EUREKA_NAME}/${EUREKA_NAME}.csr" \
    -keyout "certs/${EUREKA_NAME}/${EUREKA_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Eureka service registry" || exit

# Create TLS server certificate - Main App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${EUREKA_NAME}/${EUREKA_NAME}.csr" \
    -out "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Main App
openssl pkcs12 \
    -export \
    -name "${EUREKA_ALIAS}" \
    -inkey "certs/${EUREKA_NAME}/${EUREKA_NAME}.key" \
    -in "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${EUREKA_KEYSTORE_PASSWORD}" \
    -out "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.keystore.p12" || exit


mkdir -p "certs/${LOGGER_NAME}/keystore"

# Create TLS server request - Main App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${LOGGER_NAME}/${LOGGER_NAME}.csr" \
    -keyout "certs/${LOGGER_NAME}/${LOGGER_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Logger service" || exit

# Create TLS server certificate - Main App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${LOGGER_NAME}/${LOGGER_NAME}.csr" \
    -out "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Main App
openssl pkcs12 \
    -export \
    -name "${LOGGER_ALIAS}" \
    -inkey "certs/${LOGGER_NAME}/${LOGGER_NAME}.key" \
    -in "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${LOGGER_KEYSTORE_PASSWORD}" \
    -out "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.keystore.p12" || exit


mkdir -p "certs/${RENTING_NAME}/keystore"

# Create TLS server request - Main App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${RENTING_NAME}/${RENTING_NAME}.csr" \
    -keyout "certs/${RENTING_NAME}/${RENTING_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Renting service" || exit

# Create TLS server certificate - Main App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${RENTING_NAME}/${RENTING_NAME}.csr" \
    -out "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Main App
openssl pkcs12 \
    -export \
    -name "${RENTING_ALIAS}" \
    -inkey "certs/${RENTING_NAME}/${RENTING_NAME}.key" \
    -in "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${RENTING_KEYSTORE_PASSWORD}" \
    -out "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.keystore.p12" || exit


mkdir -p "certs/${SEARCH_NAME}/keystore"

# Create TLS server request - Main App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${SEARCH_NAME}/${SEARCH_NAME}.csr" \
    -keyout "certs/${SEARCH_NAME}/${SEARCH_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Search service" || exit

# Create TLS server certificate - Main App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${SEARCH_NAME}/${SEARCH_NAME}.csr" \
    -out "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Main App
openssl pkcs12 \
    -export \
    -name "${SEARCH_ALIAS}" \
    -inkey "certs/${SEARCH_NAME}/${SEARCH_NAME}.key" \
    -in "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${SEARCH_KEYSTORE_PASSWORD}" \
    -out "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.keystore.p12" || exit


mkdir -p "certs/${USERS_NAME}/keystore"

# Create TLS server request - Main App
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${USERS_NAME}/${USERS_NAME}.csr" \
    -keyout "certs/${USERS_NAME}/${USERS_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Users service" || exit

# Create TLS server certificate - Main App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${USERS_NAME}/${USERS_NAME}.csr" \
    -out "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore - Main App
openssl pkcs12 \
    -export \
    -name "${USERS_ALIAS}" \
    -inkey "certs/${USERS_NAME}/${USERS_NAME}.key" \
    -in "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${USERS_KEYSTORE_PASSWORD}" \
    -out "certs/${USERS_NAME}/keystore/${USERS_NAME}.keystore.p12" || exit


mkdir -p "certs/${AGENT_APP_CLIENT_NAME}"

# Create TLS client request (for web browser) - Agent App
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/client.conf \
    -out "certs/${AGENT_APP_CLIENT_NAME}/${AGENT_APP_CLIENT_NAME}.csr" \
    -keyout "certs/${AGENT_APP_CLIENT_NAME}/${AGENT_APP_CLIENT_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Team 29 Agent App" || exit
    
# Create TLS client certificate (for web browser) - Agent App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${AGENT_APP_CLIENT_NAME}/${AGENT_APP_CLIENT_NAME}.csr" \
    -out "certs/${AGENT_APP_CLIENT_NAME}/${AGENT_APP_CLIENT_NAME}.crt" \
    -policy extern_pol \
    -extensions client_ext || exit

# Export TLS client certificate chain to keystore - Agent App
openssl pkcs12 \
    -export \
    -name "${AGENT_APP_CLIENT_ALIAS}" \
    -inkey "certs/${AGENT_APP_CLIENT_NAME}/${AGENT_APP_CLIENT_NAME}.key" \
    -in "certs/${AGENT_APP_CLIENT_NAME}/${AGENT_APP_CLIENT_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${AGENT_APP_CLIENT_KEYSTORE_PASSWORD}" \
    -out "certs/${AGENT_APP_CLIENT_NAME}/${AGENT_APP_CLIENT_NAME}.p12" || exit


mkdir -p "certs/${MAIN_APP_CLIENT_NAME}"

# Create TLS client request (for web browser) - Main App
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/client.conf \
    -out "certs/${MAIN_APP_CLIENT_NAME}/${MAIN_APP_CLIENT_NAME}.csr" \
    -keyout "certs/${MAIN_APP_CLIENT_NAME}/${MAIN_APP_CLIENT_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Team 29 Main App" || exit
    
# Create TLS client certificate (for web browser) - Main App
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${MAIN_APP_CLIENT_NAME}/${MAIN_APP_CLIENT_NAME}.csr" \
    -out "certs/${MAIN_APP_CLIENT_NAME}/${MAIN_APP_CLIENT_NAME}.crt" \
    -policy extern_pol \
    -extensions client_ext || exit

# Export TLS client certificate chain to keystore - Main App
openssl pkcs12 \
    -export \
    -name "${MAIN_APP_CLIENT_ALIAS}" \
    -inkey "certs/${MAIN_APP_CLIENT_NAME}/${MAIN_APP_CLIENT_NAME}.key" \
    -in "certs/${MAIN_APP_CLIENT_NAME}/${MAIN_APP_CLIENT_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${MAIN_APP_CLIENT_KEYSTORE_PASSWORD}" \
    -out "certs/${MAIN_APP_CLIENT_NAME}/${MAIN_APP_CLIENT_NAME}.p12" || exit



# Import Root CA, TLS CA, Zuul and Agent App client certificate into Agent App keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${AGENT_APP_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${AGENT_APP_NAME}/keystore/${AGENT_APP_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${AGENT_APP_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${AGENT_APP_NAME}/keystore/${AGENT_APP_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ZUUL_ALIAS}" \
    -file "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -storepass "${AGENT_APP_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${AGENT_APP_NAME}/keystore/${AGENT_APP_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${AGENT_APP_CLIENT_ALIAS}" \
    -file "certs/${AGENT_APP_CLIENT_NAME}/${AGENT_APP_CLIENT_NAME}.crt" \
    -storepass "${AGENT_APP_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${AGENT_APP_NAME}/keystore/${AGENT_APP_NAME}.truststore.p12" || exit


# Import Root CA, TLS CA, Agent App, Advertisements, Cars, Eureka, Logger, Renting, Search, Users, RabbitMQ and Main App client certificate into Zuul keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${AGENT_APP_ALIAS}" \
    -file "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ADVERTISEMENTS_ALIAS}" \
    -file "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${CARS_ALIAS}" \
    -file "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${EUREKA_ALIAS}" \
    -file "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${LOGGER_ALIAS}" \
    -file "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${RENTING_ALIAS}" \
    -file "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${SEARCH_ALIAS}" \
    -file "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${USERS_ALIAS}" \
    -file "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${MAIN_APP_CLIENT_ALIAS}" \
    -file "certs/${MAIN_APP_CLIENT_NAME}/${MAIN_APP_CLIENT_NAME}.crt" \
    -storepass "${ZUUL_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ZUUL_NAME}/keystore/${ZUUL_NAME}.truststore.p12" || exit

# Import Root CA, TLS CA, Zuul, Cars, Eureka, Logger, Renting, Search, RabbitMQ and Users certificate into Advertisements keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ZUUL_ALIAS}" \
    -file "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${CARS_ALIAS}" \
    -file "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${EUREKA_ALIAS}" \
    -file "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${LOGGER_ALIAS}" \
    -file "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${RENTING_ALIAS}" \
    -file "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${SEARCH_ALIAS}" \
    -file "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${USERS_ALIAS}" \
    -file "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -storepass "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${ADVERTISEMENTS_NAME}/keystore/${ADVERTISEMENTS_NAME}.truststore.p12" || exit

# Import Root CA, TLS CA, Zuul, Advertisements, Eureka, Logger, Renting, Search, RabbitMQ and Users certificate into Cars keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ZUUL_ALIAS}" \
    -file "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ADVERTISEMENTS_ALIAS}" \
    -file "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${EUREKA_ALIAS}" \
    -file "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${LOGGER_ALIAS}" \
    -file "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${RENTING_ALIAS}" \
    -file "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${SEARCH_ALIAS}" \
    -file "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${USERS_ALIAS}" \
    -file "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -storepass "${CARS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${CARS_NAME}/keystore/${CARS_NAME}.truststore.p12" || exit

# Import Root CA, TLS CA, Zuul, Cars, Advertisements, Logger, Renting, Search, RabbitMQ and Users certificate into Eureka keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ZUUL_ALIAS}" \
    -file "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${CARS_ALIAS}" \
    -file "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ADVERTISEMENTS_ALIAS}" \
    -file "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${LOGGER_ALIAS}" \
    -file "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${RENTING_ALIAS}" \
    -file "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${SEARCH_ALIAS}" \
    -file "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${USERS_ALIAS}" \
    -file "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -storepass "${EUREKA_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${EUREKA_NAME}/keystore/${EUREKA_NAME}.truststore.p12" || exit

# Import Root CA, TLS CA, Zuul, Cars, Eureka, Advertisements, Renting, Search, RabbitMQ and Users certificate into Logger keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ZUUL_ALIAS}" \
    -file "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${CARS_ALIAS}" \
    -file "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${EUREKA_ALIAS}" \
    -file "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ADVERTISEMENTS_ALIAS}" \
    -file "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${RENTING_ALIAS}" \
    -file "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${SEARCH_ALIAS}" \
    -file "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${USERS_ALIAS}" \
    -file "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -storepass "${LOGGER_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${LOGGER_NAME}/keystore/${LOGGER_NAME}.truststore.p12" || exit

# Import Root CA, TLS CA, Zuul, Cars, Eureka, Logger, Advertisements, Search, RabbitMQ and Users certificate into Renting keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12"  && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ZUUL_ALIAS}" \
    -file "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${CARS_ALIAS}" \
    -file "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${EUREKA_ALIAS}" \
    -file "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${LOGGER_ALIAS}" \
    -file "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ADVERTISEMENTS_ALIAS}" \
    -file "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${SEARCH_ALIAS}" \
    -file "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${USERS_ALIAS}" \
    -file "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -storepass "${RENTING_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${RENTING_NAME}/keystore/${RENTING_NAME}.truststore.p12" || exit

# Import Root CA, TLS CA, Zuul, Cars, Eureka, Logger, Renting, Advertisements, RabbitMQ and Users certificate into Search keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12"  && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ZUUL_ALIAS}" \
    -file "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${CARS_ALIAS}" \
    -file "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${LOGGER_ALIAS}" \
    -file "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${RENTING_ALIAS}" \
    -file "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ADVERTISEMENTS_ALIAS}" \
    -file "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${EUREKA_ALIAS}" \
    -file "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${USERS_ALIAS}" \
    -file "certs/${USERS_NAME}/${USERS_NAME}.crt" \
    -storepass "${SEARCH_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${SEARCH_NAME}/keystore/${SEARCH_NAME}.truststore.p12" || exit

# Import Root CA, TLS CA, Zuul, Cars, Eureka, Logger, Renting, Search, RabbitMQ and Advertisements certificate into Users keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12"  && \
keytool \
    -importcert \
    -noprompt \
    -alias "tls" \
    -file ca/tls-ca.crt \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ZUUL_ALIAS}" \
    -file "certs/${ZUUL_NAME}/${ZUUL_NAME}.crt" \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${CARS_ALIAS}" \
    -file "certs/${CARS_NAME}/${CARS_NAME}.crt" \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${EUREKA_ALIAS}" \
    -file "certs/${EUREKA_NAME}/${EUREKA_NAME}.crt" \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${LOGGER_ALIAS}" \
    -file "certs/${LOGGER_NAME}/${LOGGER_NAME}.crt" \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${RENTING_ALIAS}" \
    -file "certs/${RENTING_NAME}/${RENTING_NAME}.crt" \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${SEARCH_ALIAS}" \
    -file "certs/${SEARCH_NAME}/${SEARCH_NAME}.crt" \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12" && \
keytool \
    -importcert \
    -noprompt \
    -alias "${ADVERTISEMENTS_ALIAS}" \
    -file "certs/${ADVERTISEMENTS_NAME}/${ADVERTISEMENTS_NAME}.crt" \
    -storepass "${USERS_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${USERS_NAME}/keystore/${USERS_NAME}.truststore.p12" || exit