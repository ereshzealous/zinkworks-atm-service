START TRANSACTION;

CREATE EXTENSION if not exists "uuid-ossp";

create table account (
                         id uuid primary key default uuid_generate_v4(),
                         account_number text not null unique,
                         pin numeric(4) not null,
                         balance real not null,
                         overdraft real not null default 0,
                         created_at timestamp default now(),
                         updated_at timestamp
);

create table notes (
                       id uuid primary key default uuid_generate_v4(),
                       note integer not null,
                       count integer not null,
                       created_at timestamp default now(),
                       updated_at timestamp
);

insert into notes (note, count) values (50, 10);
insert into notes (note, count) values (20, 30);
insert into notes (note, count) values (10, 30);
insert into notes (note, count) values (5, 20);

insert into account (account_number, pin, balance, overdraft) values (123456789, 1234, 800.00, 200.00);
insert into account (account_number, pin, balance, overdraft) values (987654321, 4321, 1230.00, 150.00);

commit;
