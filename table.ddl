CREATE EXTERNAL TABLE vpc_flow_logs(
  version SMALLINT,
  account_id BIGINT,
  interface_id STRING,
  src_addr STRING,
  dst_addr STRING,
  src_port INT,
  dst_port INT,
  protocol SMALLINT,
  packets INT,
  bytes BIGINT,
  start_epoc BIGINT,
  end_epoc BIGINT,
  action STRING,
  log_status STRING
)
STORED AS ORC
LOCATION 's3://test-orc-athena/'
tblproperties ("orc.compress"="ZLIB");


