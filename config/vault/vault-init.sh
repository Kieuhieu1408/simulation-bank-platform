#!/bin/bash
# Seed secrets vào Vault KV
vault kv put secret/sim-bank/corebank \
  DB_USERNAME=corebank \
  DB_PASSWORD=corebank_demo \
  KEYCLOAK_ISSUER_URI=http://keycloak:8080/realms/simulation-bank

vault kv put secret/sim-bank/money-bank \
  DB_USERNAME=corebank \
  DB_PASSWORD=corebank_demo \
  OAUTH2_CLIENT_SECRET=moneybank-secret \
  KAFKA_BOOTSTRAP_SERVERS=kafka:9092
  
vault kv put secret/sim-bank/cms \
  DB_USER=cms \
  DB_PASSWORD=cms

vault kv put secret/sim-bank/api-gateway \
  REDIS_PASSWORD=
