alter table Reservations
    drop constraint fk_customer_id;

alter table Reservations
    drop column fk_customer_id

create table "reservations_customer"
(
    "reservations_id" bigint not null,
    "customer_id"     bigint not null
        constraint fk_reservations foreign key (reservations_id)
            references Reservations (id),
        constraint fk_customer foreign key (customer_id)
            references Customer (id),
);