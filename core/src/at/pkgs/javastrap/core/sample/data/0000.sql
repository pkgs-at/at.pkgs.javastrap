CREATE TABLE IF NOT EXISTS "t_employee"(
	"employee_id" IDENTITY NOT NULL,
	"family_name" VARCHAR NOT NULL,
	"given_name" VARCHAR NOT NULL,
	"mail_address" VARCHAR NOT NULL,
	"telephone_number" VARCHAR NOT NULL,
	"created_at" TIMESTAMP NOT NULL,
	"updated_at" TIMESTAMP NOT NULL,
	CONSTRAINT "pk_employee" PRIMARY KEY("employee_id")
);
