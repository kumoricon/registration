
create table if not exists rights
(
  id serial not null
    constraint rights_pkey
      primary key,
  description varchar(255),
  name varchar(200) not null
    constraint uk_rights_name
      unique
);

create table if not exists roles
(
  id serial not null
    constraint roles_pkey
      primary key,
  name varchar(200) not null
    constraint uk_roles_name
      unique
);

create table if not exists roles_rights
(
  role_id integer not null
    constraint fk_role_id
      references roles,
  rights_id integer not null
    constraint fk_rights_id
      references rights,
  constraint roles_rights_pkey
    primary key (role_id, rights_id)
);


create table if not exists users
(
  id serial not null
    constraint users_pkey
      primary key,
  account_non_expired boolean not null,
  account_non_locked boolean not null,
  credentials_non_expired boolean not null,
  enabled boolean not null,
  first_name citext,
  last_badge_number_created integer not null,
  last_name citext,
  password varchar(255) not null,
  username varchar(200) not null
    constraint uk_username
      unique,
  role_id integer not null
    constraint fk_role_id
      references roles
);



create table if not exists blacklist
(
  id serial not null
    constraint blacklist_pkey
      primary key,
  first_name citext not null,
  last_name citext not null
);

create index if not exists blacklist_firstname_index
  on blacklist (first_name);

create index if not exists blacklist_lastname_index
  on blacklist (last_name);


create table if not exists tillsessions
(
  id serial not null
    constraint tillsessions_pkey
      primary key,
  end_time timestamp with time zone,
  open boolean not null,
  start_time timestamp with time zone,
  user_id integer not null
    constraint fkruie73rneumyyd1bgo6qw8vjt
      references users
);

create index if not exists tillsessions_open_index
  on tillsessions (open);



create table if not exists badges
(
  id serial not null
    constraint badges_pkey
      primary key,
  badge_type integer not null,
  badge_type_background_color varchar(255),
  badge_type_text varchar(255),
  name varchar(255) not null
    constraint uk_cuebofvgkgi4g9fxde2kmpr1h
      unique,
  required_right varchar(255),
  visible boolean not null,
  warning_message varchar(255)
);

create table if not exists ageranges
(
  id serial not null
    constraint ageranges_pkey
      primary key,
  cost numeric(19,2)
    constraint ageranges_cost_check
      check (cost >= (0)::numeric),
  max_age integer not null
    constraint ageranges_max_age_check
      check ((max_age >= 0) AND (max_age <= 255)),
  min_age integer not null
    constraint ageranges_min_age_check
      check ((min_age >= 0) AND (min_age <= 255)),
  name varchar(255) not null,
  stripe_color varchar(255),
  stripe_text varchar(255),
  badge_id integer not null
    constraint fk_ageranges_badge_id
      references badges
      on delete cascade
);


create table if not exists orders
(
  id serial not null
    constraint orders_pkey
      primary key,
  notes varchar(255),
  order_id varchar(32) not null
    constraint uk_orders_order_id
    unique,
  order_taken_by_user integer
    constraint fk_orders_ordertakenbyuser_users
      references users,
  paid boolean not null
);


create table if not exists payments
(
  id serial not null
    constraint payments_pkey
      primary key,
  amount numeric(19,2) not null
    constraint payments_amount_check
      check (amount >= (0)::numeric),
  auth_number varchar(255),
  payment_location varchar(255),
  payment_taken_at timestamp with time zone,
  payment_taken_by integer
    constraint fk_payments_paymentakenby_users
      references users,
  payment_type integer not null,
  till_session_id integer not null ,
  order_id integer not null
    constraint fk_payments_orderid_orders
      references orders
);


create table if not exists attendees
(
  id serial not null
    constraint attendees_pkey
      primary key,
  badge_id integer not null
    constraint fk_attendee_badgeid_badges
      references badges,
  badge_number varchar(255)
    constraint uk_badge_number
      unique,
  badge_pre_printed boolean not null,
  badge_printed boolean not null,
  birth_date date,
  check_in_time timestamp with time zone,
  checked_in boolean,
  comped_badge boolean,
  country varchar(255),
  email varchar(255),
  emergency_contact_full_name varchar(255),
  emergency_contact_phone varchar(255),
  fan_name citext,
  first_name citext,
  last_name citext,
  legal_first_name citext,
  legal_last_name citext,
  name_is_legal_name boolean,
  paid boolean,
  paid_amount numeric(19,2),
  parent_form_received boolean,
  parent_full_name varchar(255),
  parent_is_emergency_contact boolean,
  parent_phone varchar(255),
  phone_number varchar(255),
  pre_registered boolean not null,
  zip varchar(255),
  order_id integer not null
    constraint fk_attendee_orderid_orders
      references orders
);



create table if not exists attendeehistory
(
  id serial not null
    constraint attendeehistory_pkey
      primary key,
  message citext,
  timestamp timestamp with time zone,
  user_id integer not null
    constraint fk_attendeehistory_userid_users
      references users,
  attendee_id integer not null
    constraint fk_attendeehisotry_attendeeid_attendees
      references attendees
);


create table loginsessions
(
  start timestamp with time zone not null,
  users_id integer not null
    constraint fk_loginsessions_usersid_users
      references users,
  constraint loginsessions_pk
    unique (start, users_id)
);

create index loginsessions_start_index
  on loginsessions (start);



