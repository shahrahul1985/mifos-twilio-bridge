CREATE TABLE sms_bridge_config (
  id         BIGINT(20)     NOT NULL AUTO_INCREMENT                                 NOT NULL,
  tenant_id               VARCHAR(32)                                     NOT NULL,
  api_key                 VARCHAR(32)                                     NOT NULL,
  endpoint                VARCHAR(256)                                    NOT NULL,
  mifos_token             VARCHAR(256)                                    NOT NULL,
  sms_provider            VARCHAR(32)                                     NOT NULL,
  sms_provider_account_id VARCHAR(256)                                    NOT NULL,
  sms_provider_token      VARCHAR(32)                                     NOT NULL,
  phone_no                VARCHAR(255)                                    NOT NULL,
  created_on              DATETIME                                       NOT NULL,
  last_modified_on        DATETIME                                       NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (tenant_id)
);

CREATE TABLE event_sourcing (
  id               BIGINT(20)     NOT NULL AUTO_INCREMENT                                                            NOT NULL,
  tenant_id        VARCHAR(32)                                                   NOT NULL,
  entity           VARCHAR(32)                                                   NOT NULL,
  action           VARCHAR(32)                                                   NOT NULL,
  payload          VARCHAR(4096)                                                 NOT NULL,
  processed        BOOLEAN                                                       NOT NULL,
  error_message    VARCHAR(256),
  created_on       DATETIME                                                     NOT NULL,
  last_modified_on DATETIME                                                     NOT NULL,
  PRIMARY KEY (`id`)
);