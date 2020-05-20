#! /bin/bash

BASE_DIR=$(pwd)
TLS_DIR=${BASE_DIR}/tls
DOCKER_COMPOSE_TEMPLATE=${BASE_DIR}/docker-compose-template.yml
DOCKER_COMPOSE=${BASE_DIR}/docker-compose.yml

KEY_SIZE=${2:-4096}

AGENT_APP_NAME=${3:-agentapp}
AGENT_APP_ALIAS=${4:-agentapp}
AGENT_APP_KEYSTORE_PASSWORD=${5:-agentapp_pass}

MAIN_APP_NAME=${6:-mainapp}
MAIN_APP_ALIAS=${7:-mainapp}
MAIN_APP_KEYSTORE_PASSWORD=${8:-mainapp_pass}

AGENT_APP_CLIENT_NAME=${9:-agentapp_client}
AGENT_APP_CLIENT_ALIAS=${10:-agentapp_client}
AGENT_APP_CLIENT_KEYSTORE_PASSWORD=${11:-agentapp_client_pass}

MAIN_APP_CLIENT_NAME=${12:-mainapp_client}
MAIN_APP_CLIENT_ALIAS=${13:-mainapp_client}
MAIN_APP_CLIENT_KEYSTORE_PASSWORD=${14:-mainapp_client_pass}

function generateDockerCompose() {
  # sed on MacOSX does not support -i flag with a null extension. We will use
  # 't' for our back-up's extension and delete it at the end of the function
  ARCH=$(uname -s | grep Darwin)
  if [[ "$ARCH" == "Darwin" ]]; then
    OPTS="-it"
  else
    OPTS="-i"
  fi

  # Copy the template to the file that will be modified to add certificates
  cp "${DOCKER_COMPOSE_TEMPLATE}" "${DOCKER_COMPOSE}" || exit

  # The next steps will replace the template's contents with the
  # actual values of the certificates and private key files
  sed ${OPTS} \
      -e "s/AGENT_APP_NAME/${AGENT_APP_NAME}/g" \
      -e "s/AGENT_APP_ALIAS/${AGENT_APP_ALIAS}/g" \
      -e "s/AGENT_APP_KEYSTORE_PASSWORD/${AGENT_APP_KEYSTORE_PASSWORD}/g" \
      -e "s/MAIN_APP_NAME/${MAIN_APP_NAME}/g" \
      -e "s/MAIN_APP_ALIAS/${MAIN_APP_ALIAS}/g" \
      -e "s/MAIN_APP_KEYSTORE_PASSWORD/${MAIN_APP_KEYSTORE_PASSWORD}/g" \
      "${DOCKER_COMPOSE}"

  if [[ "$ARCH" == "Darwin" ]]; then
    rm "${DOCKER_COMPOSE}t"
  fi
}

function generateCertificates() {
  # Clean up previous generated certificates
  rm -rf "${TLS_DIR}"/ca "${TLS_DIR}"/certs "${TLS_DIR}"/crl

  docker run \
       -it \
       --rm \
       --entrypoint sh \
       --mount "type=bind,source=${TLS_DIR},destination=/export" \
       --user "$(id -u):$(id -g)" \
       danijelradakovic/openssl-keytool \
       gen.sh "${KEY_SIZE}" \
        "${AGENT_APP_NAME}" "${AGENT_APP_ALIAS}" "${AGENT_APP_KEYSTORE_PASSWORD}" \
        "${MAIN_APP_NAME}" "${MAIN_APP_ALIAS}" "${MAIN_APP_KEYSTORE_PASSWORD}" \
        "${AGENT_APP_CLIENT_NAME}" "${AGENT_APP_CLIENT_ALIAS}" "${AGENT_APP_CLIENT_KEYSTORE_PASSWORD}" \
        "${MAIN_APP_CLIENT_NAME}" "${MAIN_APP_CLIENT_ALIAS}" "${MAIN_APP_CLIENT_KEYSTORE_PASSWORD}" 
       
}

if [ "${1}" == '-g' ]; then
  generateCertificates
  generateDockerCompose
fi

docker-compose up --build
