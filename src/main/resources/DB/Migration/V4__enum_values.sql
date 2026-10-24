alter table "customer" drop constraint "customer_country_of_customer_check";
alter table "customer" add constraint "customer_country_of_customer_check"
    check ("country_of_customer" in ('BRAZIL', 'PARAGUAY', 'UNITED_STATES', 'EUROPE'));

alter table "tour" drop constraint "tour_country_of_tour_check";
alter table "tour" add constraint "tour_country_of_tour_check"
    check ("country_of_tour" in ('ARGENTINA', 'BRAZIL', 'PARAGUAY'));

alter table "payment" drop constraint "payment_status_check";
alter table "payment" add constraint "payment_status_check"
    check ("status" in ('PENDING', 'CANCELATED', 'CONFIRMED'));

alter table "reservation" drop constraint "reservation_status_check";
alter table "reservation" add constraint "reservation_status_check"
    check ("status" in ('PENDING', 'CANCELATED', 'CONFIRMED'));

alter table "user" drop constraint "user_permission_check";
alter table "user" add constraint "user_permission_check"
    check ("permission" in ('MANAGER', 'EMPLOYEE'));

alter table "clientes_languages" drop constraint "clientes_languages_language_check";
alter table "clientes_languages" add constraint "clientes_languages_language_check"
    check ("language" in ('SPANISH', 'PORTUGUESE', 'ENGLISH'));

alter table "languages_funcionario" drop constraint "languages_funcionario_language_check";
alter table "languages_funcionario" add constraint "languages_funcionario_language_check"
    check ("language" in ('SPANISH', 'PORTUGUESE', 'ENGLISH'));
