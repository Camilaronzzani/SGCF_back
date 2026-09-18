
create table "reservations_customer" (
    "reservations_id" bigint not null,
    "customer_id"     bigint not null,

    constraint "fk_reservations_customer_reservation"
        foreign key ("reservations_id")
        references "reservation" ("id"),

    constraint "fk_reservations_customer_customer"
        foreign key ("customer_id")
        references "customer" ("id")
);
