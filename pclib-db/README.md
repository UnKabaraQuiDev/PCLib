# PCLib DB

Database helpers, SQL builders, annotations, and query utilities.

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-db</artifactId>
</dependency>
````

## What it contains

This module includes:

* annotations for entries and views
* SQL table and column builders
* query annotations
* database connectors and base classes
* helpers for MySQL and SQLite testing

Main areas:

* `lu.kbra.pclib.db.annotations`
* `lu.kbra.pclib.db.autobuild`
* `lu.kbra.pclib.db.base`
* `lu.kbra.pclib.db.connector`

## Example

### Data classes:

```java
public class PersonData implements DataBaseEntry {

  @Column
  @AutoIncrement
  @PrimaryKey
  protected int id;

  @Column(length = 30)
  @Unique
  protected String name;

  @Column
  protected Date birthDate;

  @Column
  @Generated(Type.VIRTUAL)
  @DefaultValue("YEAR(birth_date)")
  protected Integer birthYear;

  public PersonData() {
  }

  public PersonData(int id) {
    this.id = id;
  }

  public PersonData(String name, Date birthDate) {
    this.name = name;
    this.birthDate = birthDate;
  }

}
```

### Table classes
```java
public class PersonTable extends DataBaseTable<PersonData> {

  public PersonTable(DataBase dataBase) {
    super(dataBase);
  }

}
```

### Connect to the db

```java
MySQLDataBaseConnector connector = new MySQLDataBaseConnector("user", "pass", "localhost", 3306);
DataBase db = new DataBase(connector, "db_name");

// Load the default MySQL types
((BaseDataBaseEntryUtils) db.getDataBaseEntryUtils()).loadMySQLTypes();
```

### Inserting data

```java
PersonTable people = new PersonTable(db);

Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis())));
PersonData p1 = new PersonData("Name1", date);

// insert the PersonData in the table, reloads the generated keys (primary keys and/or generated columns)
people.insertAndReload(p1)

// delete the PersonData by primary key
people.delete(p1)

// delete by unique
people.deleteUnique(p1)
```

### Transactions

```java
try (DBTransaction tt = db.createTransaction()) {
  // DBTransaction#use creates a proxy to the given table that uses a different connection

  tt.use(people).insertAndReload(p1);
  assert tt.use(people).exists(p1);

  assert !people.exists(p1);

  tt.rollback();
  // or:
  tt.commit();
  // the AutoCloseable#close operation rollbacks by default
}
```

### Views

```java
public class PersonCarROData implements ReadOnlyDataBaseEntry {

  @Column
  protected Integer personId;

  @Column
  protected String personName;

  @Column
  protected Integer carId;

  @Column
  protected String carBrand;

  public PersonCarROData() {
  }

}

public class CarData implements DataBaseEntry {

  @Column
  @AutoIncrement
  @PrimaryKey
  protected int id;

  @Column(name = "person_id")
  @ForeignKey(table = PersonTable.class)
  protected int personId;

  @Column(length = 50)
  protected String brand;

  public CarData() {
  }

  public CarData(int personId, String brand) {
    this.personId = personId;
    this.brand = brand;
  }

}

public class CityData implements DataBaseEntry {

  @Column
  @AutoIncrement
  @PrimaryKey
  protected int id;

  @Column(name = "garage_id")
  @ForeignKey(table = GarageTable.class)
  protected int garageId;

  @Column(length = 80)
  protected String name;

  public CityData() {
  }

  public CityData(int garageId, String name) {
    this.garageId = garageId;
    this.name = name;
  }
  
}

public class GarageData implements DataBaseEntry {

  @Column
  @AutoIncrement
  @PrimaryKey
  protected int id;

  @Column(name = "car_id")
  @ForeignKey(table = CarTable.class)
  protected int carId;

  @Column(length = 80)
  protected String name;

  public GarageData() {
  }

  public GarageData(int carId, String name) {
    this.carId = carId;
    this.name = name;
  }

}

public class CarTable extends DataBaseTable<CarData> {

  public CarTable(DataBase dataBase) {
    super(dataBase);
  }

}

public class CityTable extends DataBaseTable<CityData> {

  public CityTable(DataBase dataBase) {
    super(dataBase);
  }
}

public class GarageTable extends DataBaseTable<GarageData> {

  public GarageTable(DataBase dataBase) {
    super(dataBase);
  }

}


@DB_View(
  name = "person_car_view",
  tables = {
    @ViewTable(
      typeName = PersonTable.class,
      asName = "p",
      columns = { @ViewColumn(name = "id", asName = "person_id"), @ViewColumn(name = "name", asName = "person_name") }
    ),
    @ViewTable(
      typeName = CarTable.class,
      join = ViewTable.Type.INNER,
      asName = "c",
   // on = "p.id = c.person_id",
      columns = { @ViewColumn(name = "id", asName = "car_id"), @ViewColumn(name = "brand", asName = "car_brand") }
    )
  }
)
public class PersonCarView extends DataBaseView<PersonCarROData> {

  public PersonCarView(DataBase dataBase) {
    super(dataBase);
  }

}
```

The **ON** clause for joins is computed automatically if omitted, throwing an error if no path is found between the given classes or if too many paths are found.





