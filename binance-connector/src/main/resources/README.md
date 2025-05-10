# Binance Connector Service

## Database Migration with Flyway

This service uses Flyway for database schema migration management. The migration scripts are located in the `src/main/resources/db/migration` directory.

### Migration Scripts

1. **V1__Create_Initial_Tables.sql**: Creates the initial tables for raw market data.
2. **V2__Create_TimescaleDB_Hypertables.sql**: Converts tables to TimescaleDB hypertables if the extension is available.
3. **V3__Add_TimestampTz_Usage.sql**: Updates timestamp columns to use timestamptz for better timezone handling.

### How Flyway Works

- Flyway creates a table called `flyway_schema_history` to keep track of which migrations have been applied.
- Migrations are applied in version order.
- Each migration will only be applied once.
- If a migration fails, Flyway will stop and report the error.

### Adding New Migrations

When you need to make changes to the database schema:

1. Create a new migration script in the `src/main/resources/db/migration` directory.
2. Name it using the following pattern: `V{n}__Descriptive_Name.sql` where `{n}` is the next version number.
3. Add your SQL commands to the script.
4. Flyway will automatically apply this migration when the application starts.

### Configuration

The Flyway configuration is in `application.properties`:

```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
spring.flyway.schemas=public
spring.flyway.validate-on-migrate=true
