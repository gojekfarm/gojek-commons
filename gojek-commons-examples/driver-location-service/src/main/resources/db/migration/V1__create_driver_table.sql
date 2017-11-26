create table drivers (
	id character varying(36) not null,
	name character varying(255) not null,
	phone character varying(20) not null,
	lat numeric(10,7),
    lng numeric(10,7),
    status character varying(20) not null,
    vehicle_type character varying(20) not null,
    last_seen_time timestamp without time zone,
    created_at timestamp without time zone not null,
	updated_at timestamp without time zone not null,
	deleted boolean not null,
	primary key(id)
);