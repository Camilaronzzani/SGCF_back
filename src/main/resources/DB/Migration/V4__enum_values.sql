alter table "customer" drop constraint "customer_country_of_customer_check";
alter table "customer" add constraint "customer_country_of_customer_check"
    check ("country_of_customer" in ('Brazil', 'Paraguay', 'United_states', 'Europe'));

alter table "tour" drop constraint "tour_country_of_tour_check";
alter table "tour" add constraint "tour_country_of_tour_check"
    check ("country_of_tour" in ('Argentina', 'Brazil', 'Paraguay'));

alter table "payment" drop constraint "payment_status_check";
alter table "payment" add constraint "payment_status_check"
    check ("status" in ('Pending', 'Cancelled', 'Confirmed'));

alter table "reservation" drop constraint "reservation_status_check";
alter table "reservation" add constraint "reservation_status_check"
    check ("status" in ('Pending', 'Cancelled', 'Confirmed'));

alter table "user" drop constraint "user_permission_check";
alter table "user" add constraint "user_permission_check"
    check ("permission" in ('Manager', 'Employee'));

alter table "clientes_languages" drop constraint "clientes_languages_language_check";
alter table "clientes_languages" add constraint "clientes_languages_language_check"
    check ("language" in ('Spanish', 'Portuguese', 'English'));

alter table "languages_funcionario" drop constraint "languages_funcionario_language_check";
alter table "languages_funcionario" add constraint "languages_funcionario_language_check"
    check ("language" in ('Spanish', 'Portuguese', 'English'));
