alter table "password_reset" add column if not exists "verified" boolean not null default false;
