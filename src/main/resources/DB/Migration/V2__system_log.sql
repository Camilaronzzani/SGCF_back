create table "system_log" (
          "id" bigserial,
          "level" varchar(10) not null,
          "logger_name" varchar(255),
          "message" varchar(2000),
          "exception_type" varchar(255),
          "stack_trace" text,
          "created_at" timestamp not null,

          primary key ("id")
);

create index "idx_system_log_created_at" on "system_log" ("created_at" desc);
create index "idx_system_log_level" on "system_log" ("level");
