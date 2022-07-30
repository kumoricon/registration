CREATE EXTENSION IF NOT EXISTS citext;

create table if not exists settings
(
  name varchar(200) not null primary key,
  value varchar(200)
);

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
  online_id citext,
  account_non_expired boolean not null,
  account_non_locked boolean not null,
  force_password_change boolean not null default false,
  enabled boolean not null,
  first_name citext,
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
  last_name citext not null,
  unique (first_name, last_name)
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
            references users,
    till_name varchar(255)
);

create index if not exists tillsessions_open_index
  on tillsessions (open);

create index if not exists tillsessions_user_id_index
  on tillsessions (user_id);


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

create index if not exists ageranges_badge_id_index
    on ageranges (badge_id);


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
  square_receipt_number varchar(255),
  check_number varchar(255),
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

create index if not exists payments_till_session_id_index
    on payments (till_session_id);

create index if not exists payments_order_id_index
    on payments (order_id);


create table if not exists orderhandoffs
(
    order_id integer not null
        constraint orderhandoffs_order_id_orders_fk
            references orders,
    user_id integer not null
        constraint orderhandoffs_user_id_users_fk
            references users,
    timestamp timestamp with time zone not null,
    stage text not null
);


create unique index if not exists orderhandoffs_order_id_uindex
    on orderhandoffs (order_id);



create table if not exists attendees
(
  id serial not null
    constraint attendees_pkey
      primary key,
  badge_id integer not null
    constraint fk_attendee_badgeid_badges
      references badges,
  badge_number varchar(255) not null
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
  membership_revoked boolean not null,
  name_is_legal_name boolean,
  preferred_pronoun varchar(255),
  custom_pronoun varchar(255),
  paid boolean,
  paid_amount numeric(19,2),
  parent_form_received boolean,
  parent_full_name varchar(255),
  parent_is_emergency_contact boolean,
  parent_phone varchar(255),
  phone_number varchar(255),
  pre_registered boolean not null,
  zip varchar(255),
  accessibility_sticker boolean default false not null,
  last_modified timestamptz not null,
  order_id integer not null
    constraint fk_attendee_orderid_orders
      references orders
);

create index if not exists attendees_order_id_index
    on attendees (order_id);

create index if not exists attendees_checked_in_index
    on attendees (checked_in);

create index if not exists attendees_last_modified_index
    on attendees (last_modified desc);

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

create index if not exists attendeehistory_attendee_id_index
    on attendeehistory (attendee_id);


create table if not exists loginsessions
(
  start timestamp with time zone not null,
  users_id integer not null
    constraint fk_loginsessions_usersid_users
      references users,
  constraint loginsessions_pk
    unique (start, users_id)
);

create index if not exists loginsessions_start_index
  on loginsessions (start);


create table if not exists guests
(
    id serial not null
        constraint guests_pk
            primary key,
    online_id text not null,
    first_name text not null,
    last_name text,
    preferred_pronoun text,
    legal_first_name text,
    legal_last_name text,
    age_category_at_con text,
    fan_name text,
    birth_date date,
    has_badge_image boolean default false not null,
    badge_image_file_type text,
    badge_number varchar(255) not null
        constraint uk_guest_badge_number
        unique
);

create unique index if not exists guests_online_id_uindex
    on guests (online_id);


create table if not exists staff
(
	id serial not null
		constraint staff_pkey
			primary key,
	age_category_at_con varchar(255),
	badge_image_file_type varchar(255),
	badge_print_count integer default 0 not null,
	badge_printed boolean not null,
	birth_date date,
	phone_number varchar(255),
	checked_in boolean,
	checked_in_at timestamp with time zone,
	deleted boolean,
	department varchar(255) not null,
	department_color_code varchar(255),
	first_name varchar(255) not null,
	has_badge_image boolean not null,
	last_name varchar(255) not null,
	legal_first_name varchar(255),
	legal_last_name varchar(255),
    privacy_name_first varchar(255) not null,
    privacy_name_last varchar(255) not null,
	preferred_pronoun varchar(255),
	shirt_size varchar(255),
	suppress_printing_department boolean not null,
	uuid varchar(255) not null,
	information_verified boolean default false not null,
	picture_saved boolean default false not null,
	accessibility_sticker boolean default false not null,
	last_modified timestamptz not null,
    badge_number varchar(255) not null
        constraint uk_staff_badge_number
            unique
);

create index if not exists staff_checked_in_index
    on staff (checked_in);

create index if not exists staff_last_modified_index
    on staff (last_modified desc);


create table if not exists staff_positions
(
	id int not null
		constraint fk2stj1bd4u7fx9l7mswhfqa78t
			references staff,
	position varchar(255) not null
);


create table if not exists inlineregistrations
(
    id bigserial not null
        constraint inlineregistrations_pk
            primary key,
    uuid uuid not null UNIQUE,
    first_name citext,
    last_name citext,
    legal_first_name citext,
    legal_last_name citext,
    name_is_legal_name boolean default true not null,
    preferred_pronoun text,
    zip text,
    country text,
    phone_number text,
    email text,
    birth_date date,
    emergency_contact_fullname text,
    emergency_contact_phone text,
    parent_fullname text,
    parent_phone text,
    parent_is_emergency_contact boolean default false,
    confirmation_code text not null,
    membership_type text,
    order_uuid uuid
);

create index if not exists inlineregistrations_registration_code_index
    on inlineregistrations (confirmation_code);

create unique index if not exists inlineregistrations_uuid_uindex
    on inlineregistrations (uuid);

create index if not exists inlineregistrations_registration_code_index
    on inlineregistrations (confirmation_code);


create table if not exists badgenumbers
(
    id serial
        constraint badgenumbers_pk
            primary key,
    prefix char(2) not null,
    number integer not null
);

create unique index if not exists badgenumbers_prefix_uindex
    on badgenumbers (prefix);

