#! /bin/sh

KEY_SIZE=${1:-4096}

AGENT_APP_NAME=${2:-agentapp}
AGENT_APP_ALIAS=${3:-agentapp}
AGENT_APP_KEYSTORE_PASSWORD=${4:-agentapp_pass}

CLIENT_NAME=${5:-client}
CLIENT_ALIAS=${6:-client}
CLIENT_KEYSTORE_PASSWORD=${7:-client_pass}

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

# Create TLS server request
SAN=DNS:green.no,DNS:www.green.no,DNS:localhost \
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/server.conf \
    -out "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.csr" \
    -keyout "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Rent-a-Car Agent" || exit

# Create TLS server certificate
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.csr" \
    -out "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.crt" \
    -extensions server_ext || exit

# Export TLS server certificate chain to keystore
openssl pkcs12 \
    -export \
    -name "${AGENT_APP_ALIAS}" \
    -inkey "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.key" \
    -in "certs/${AGENT_APP_NAME}/${AGENT_APP_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${AGENT_APP_KEYSTORE_PASSWORD}" \
    -out "certs/${AGENT_APP_NAME}/keystore/${AGENT_APP_NAME}.keystore.p12" || exit


mkdir -p "certs/${CLIENT_NAME}"

# Create TLS client request (for web browser)
openssl req \
    -new \
    -nodes \
    -newkey "rsa:${KEY_SIZE}" \
    -config etc/client.conf \
    -out "certs/${CLIENT_NAME}/${CLIENT_NAME}.csr" \
    -keyout "certs/${CLIENT_NAME}/${CLIENT_NAME}.key" \
    -subj "/C=NO/O=Green AS/OU=Green Certificate Authority/CN=Team 29" || exit
    
# Create TLS client certificate (for web browser)
openssl ca \
    -rand_serial \
    -batch \
    -config etc/tls-ca.conf \
    -in "certs/${CLIENT_NAME}/${CLIENT_NAME}.csr" \
    -out "certs/${CLIENT_NAME}/${CLIENT_NAME}.crt" \
    -policy extern_pol \
    -extensions client_ext || exit

# Export TLS client certificate chain to keystore
openssl pkcs12 \
    -export \
    -name "${CLIENT_ALIAS}" \
    -inkey "certs/${CLIENT_NAME}/${CLIENT_NAME}.key" \
    -in "certs/${CLIENT_NAME}/${CLIENT_NAME}.crt" \
    -certfile ca/tls-ca-chain.pem \
    -passout "pass:${CLIENT_KEYSTORE_PASSWORD}" \
    -out "certs/${CLIENT_NAME}/${CLIENT_NAME}.p12" || exit


# Import Root CA, TLS CA and client certificate into agentapp keystore
keytool \
    -importcert \
    -noprompt \
    -alias "root" \
    -file ca/root-ca.crt \
    -storepass "${AGENT_APP_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${AGENT_APP_NAME}/keystore/${AGENT_APP_NAME}.truststore.p12"  && \
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
    -alias "${CLIENT_ALIAS}" \
    -file "certs/${CLIENT_NAME}/${CLIENT_NAME}.crt" \
    -storepass "${AGENT_APP_KEYSTORE_PASSWORD}" \
    -storetype "PKCS12" \
    -keystore "certs/${AGENT_APP_NAME}/keystore/${AGENT_APP_NAME}.truststore.p12" || exit