#! /bin/bash

BASE_DIR=$(pwd)
TLS_DIR=${BASE_DIR}/tls
DOCKER_COMPOSE_TEMPLATE=${BASE_DIR}/docker-compose-template.yml
DOCKER_COMPOSE=${BASE_DIR}/docker-compose.yml

KEY_SIZE=${2:-4096}

AGENT_APP_NAME=${3:-agentapp}
AGENT_APP_ALIAS=${4:-agentapp}
AGENT_APP_KEYSTORE_PASSWORD=${5:-agentapp_pass}

ZUUL_NAME=${6:-zuul}
ZUUL_ALIAS=${7:-zuul}
ZUUL_KEYSTORE_PASSWORD=${8:-zuul_pass}

ADVERTISEMENTS_NAME=${9:-advertisements}
ADVERTISEMENTS_ALIAS=${10:-advertisements}
ADVERTISEMENTS_KEYSTORE_PASSWORD=${11:-advertisements_pass}

CARS_NAME=${12:-cars}
CARS_ALIAS=${13:-cars}
CARS_KEYSTORE_PASSWORD=${14:-cars_pass}

EUREKA_NAME=${15:-eureka}
EUREKA_ALIAS=${16:-eureka}
EUREKA_KEYSTORE_PASSWORD=${17:-eureka_pass}

LOGGER_NAME=${18:-logger}
LOGGER_ALIAS=${19:-logger}
LOGGER_KEYSTORE_PASSWORD=${20:-logger_pass}

RENTING_NAME=${21:-renting}
RENTING_ALIAS=${22:-renting}
RENTING_KEYSTORE_PASSWORD=${23:-renting_pass}

SEARCH_NAME=${24:-search}
SEARCH_ALIAS=${25:-search}
SEARCH_KEYSTORE_PASSWORD=${26:-search_pass}

USERS_NAME=${27:-users}
USERS_ALIAS=${28:-users}
USERS_KEYSTORE_PASSWORD=${29:-users_pass}

AGENT_APP_CLIENT_NAME=${30:-agentapp_client}
AGENT_APP_CLIENT_ALIAS=${31:-agentapp_client}
AGENT_APP_CLIENT_KEYSTORE_PASSWORD=${32:-agentapp_client_pass}

MAIN_APP_CLIENT_NAME=${33:-mainapp_client}
MAIN_APP_CLIENT_ALIAS=${34:-mainapp_client}
MAIN_APP_CLIENT_KEYSTORE_PASSWORD=${35:-mainapp_client_pass}

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
      -e "s/ZUUL_NAME/${ZUUL_NAME}/g" \
      -e "s/ZUUL_ALIAS/${ZUUL_ALIAS}/g" \
      -e "s/ZUUL_KEYSTORE_PASSWORD/${ZUUL_KEYSTORE_PASSWORD}/g" \
      -e "s/ADVERTISEMENTS_NAME/${ADVERTISEMENTS_NAME}/g" \
      -e "s/ADVERTISEMENTS_ALIAS/${ADVERTISEMENTS_ALIAS}/g" \
      -e "s/ADVERTISEMENTS_KEYSTORE_PASSWORD/${ADVERTISEMENTS_KEYSTORE_PASSWORD}/g" \
      -e "s/CARS_NAME/${CARS_NAME}/g" \
      -e "s/CARS_ALIAS/${CARS_ALIAS}/g" \
      -e "s/CARS_KEYSTORE_PASSWORD/${CARS_KEYSTORE_PASSWORD}/g" \
      -e "s/EUREKA_NAME/${EUREKA_NAME}/g" \
      -e "s/EUREKA_ALIAS/${EUREKA_ALIAS}/g" \
      -e "s/EUREKA_KEYSTORE_PASSWORD/${EUREKA_KEYSTORE_PASSWORD}/g" \
      -e "s/LOGGER_NAME/${LOGGER_NAME}/g" \
      -e "s/LOGGER_ALIAS/${LOGGER_ALIAS}/g" \
      -e "s/LOGGER_KEYSTORE_PASSWORD/${LOGGER_KEYSTORE_PASSWORD}/g" \
      -e "s/RENTING_NAME/${RENTING_NAME}/g" \
      -e "s/RENTING_ALIAS/${RENTING_ALIAS}/g" \
      -e "s/RENTING_KEYSTORE_PASSWORD/${RENTING_KEYSTORE_PASSWORD}/g" \
      -e "s/SEARCH_NAME/${SEARCH_NAME}/g" \
      -e "s/SEARCH_ALIAS/${SEARCH_ALIAS}/g" \
      -e "s/SEARCH_KEYSTORE_PASSWORD/${SEARCH_KEYSTORE_PASSWORD}/g" \
      -e "s/USERS_NAME/${USERS_NAME}/g" \
      -e "s/USERS_ALIAS/${USERS_ALIAS}/g" \
      -e "s/USERS_KEYSTORE_PASSWORD/${USERS_KEYSTORE_PASSWORD}/g" \
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
        "${ZUUL_NAME}" "${ZUUL_ALIAS}" "${ZUUL_KEYSTORE_PASSWORD}" \
        "${ADVERTISEMENTS_NAME}" "${ADVERTISEMENTS_ALIAS}" "${ADVERTISEMENTS_KEYSTORE_PASSWORD}" \
        "${CARS_NAME}" "${CARS_ALIAS}" "${CARS_KEYSTORE_PASSWORD}" \
        "${EUREKA_NAME}" "${EUREKA_ALIAS}" "${EUREKA_KEYSTORE_PASSWORD}" \
        "${LOGGER_NAME}" "${LOGGER_ALIAS}" "${LOGGER_KEYSTORE_PASSWORD}" \
        "${RENTING_NAME}" "${RENTING_ALIAS}" "${RENTING_KEYSTORE_PASSWORD}" \
        "${SEARCH_NAME}" "${SEARCH_ALIAS}" "${SEARCH_KEYSTORE_PASSWORD}" \
        "${USERS_NAME}" "${USERS_ALIAS}" "${USERS_KEYSTORE_PASSWORD}" \
        "${AGENT_APP_CLIENT_NAME}" "${AGENT_APP_CLIENT_ALIAS}" "${AGENT_APP_CLIENT_KEYSTORE_PASSWORD}" \
        "${MAIN_APP_CLIENT_NAME}" "${MAIN_APP_CLIENT_ALIAS}" "${MAIN_APP_CLIENT_KEYSTORE_PASSWORD}"
}

generateCertificates