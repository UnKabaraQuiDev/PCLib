package test;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.hook.VersionRule;
import lu.kbra.pclib.db.utils.DatabaseScanner;

import shared.PersonData;
import shared.PersonTable;

public interface DBTest extends GenericDBTest {

	@Test
	default void testTable() throws SQLException {
		final PersonTable people = new PersonTable(this.getDatabase());
		people.getDatabaseEntryUtils().getQueryableHookManager().add(new VersionRule(true));
		System.err.println("Hooks:\n" + people.getDatabaseEntryUtils().getQueryableHookManager().toTreeString());
		new DatabaseScanner(this.getDatabase(), null).register(people).doScan();
		System.err.println(people.getStructure().toTreeString());
		System.err.println(Arrays.toString(people.getCreateSQL()));
		assert !people.exists() : "Table shouldn't exists.";
		assert people.create().created() : "Failed to create table";
		assert people.truncate() == 0 : "There shouldn't be any entries";

		Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)));
		final PersonData p1 = new PersonData("Name1", date);
		people.insertAndReload(p1);
		assert p1.getBirthYear() == date.getYear() + 1900 : p1.getBirthYear() + " <> " + date.getYear() + " (" + p1.getBirthDate() + ")";
		date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 590_000_000)));
		final PersonData p2 = new PersonData("Name2", date);
		people.insertAndReload(p2);
		assert p2.getBirthYear() == date.getYear() + 1900 : p2.getBirthYear() + " <> " + date.getYear() + " (" + p2.getBirthDate() + ")";

		System.err.println("Hooks:\n" + people.getDatabaseEntryUtils().getQueryableHookManager().toTreeString());

		{
			final PersonData p1Duplicate = people.load(p1.clone());
			assert p1Duplicate != p1 : "Clone returned same instance.";
			// edit p1 and update
			System.err.println("before: " + p1);
			p1.setName("Name1-Changed");
			people.updateAndReload(p1);
			System.err.println("after: " + p1);
			System.err.println("other: " + p1Duplicate);
			assert p1.getVersion() > p1Duplicate.getVersion();
			// will cause p1Duplicate to be outdated
			Assertions.assertThrows(DBException.class, () -> people.updateAndReload(p1Duplicate));
		}

		Assertions.assertThrows(DBException.class, () -> people.insertAndReload(p1));

		assert people.exists(p2);
		assert people.existsUnique(p2);
		people.delete(p2);
		assert !people.exists(p2);
		assert !people.existsUnique(p2);

		assert !people.deleteIfExists(p2).isPresent();
		assert people.countUniques(p1) == 1;
		assert people.countUniques(p2) == 0;
		assert people.countNotNull(p1) == 1;

		final PersonData p3 = new PersonData("Name3", p1.getBirthDate());
		people.insertAndReload(p3);
		assert p3.getBirthYear() == date.getYear() + 1900
				: p3.getBirthDate() + " <> " + p1.getBirthDate().getYear() + " (" + p3.getBirthDate() + ")";

		final PersonData agePerson = new PersonData();
		agePerson.setBirthDate(p1.getBirthDate());

		System.err.println(agePerson + " matching: " + people.countNotNull(agePerson) + " people");
		assert people.countNotNull(agePerson) == 2;

		assert people.loadUniqueIfExists(p3).isPresent();
		assert people.loadIfExists(p3).isPresent();
		assert people.loadIfExistsElseInsert(p3) == p3;
		assert people.deleteIfExists(p3).isPresent();

		// default value
		{
			final PersonData pp = new PersonData("name only");
			final PersonData returned = people.insertAndReload(pp);
			assert returned == pp;
			assert returned.getBirthDate() != null;
			people.delete(returned);
		}

		{
			final Collection<PersonData> persons = Arrays.asList(
					new PersonData("name1", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS))),
					new PersonData("name2", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS))),
					new PersonData("name3", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(4, TimeUnit.DAYS))),
					new PersonData("name4", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(5, TimeUnit.DAYS))),
					new PersonData("name5"));
			Collection<PersonData> returned = people.insertAll(persons);
			assert returned.size() == persons.size();
			returned.forEach(c -> {
				assert c.getId() != 0 : c;
			});

			returned = people.deleteAll(persons);
			assert returned.size() == persons.size();
			returned.forEach(c -> {
				assert !people.exists(c) : c;
			});
		}

		{
			final Collection<PersonData> persons = Arrays.asList(
					new PersonData("name1", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS))),
					new PersonData("name2", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS))),
					new PersonData("name3", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(4, TimeUnit.DAYS))),
					new PersonData("name4", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(5, TimeUnit.DAYS))),
					new PersonData("name5"));
			Collection<PersonData> returned = people.insertAndReloadAll(persons);
			assert returned.size() == persons.size();
			returned.forEach(c -> {
				assert c.getId() != 0 : c;
				assert c.getBirthDate() != null : c;
			});

			int[] ageBefore = returned.stream().mapToInt(PersonData::getBirthYear).toArray();
			returned.forEach(c -> {
				c.setName(c.getName() + " RENAMED \\>0</");
				c.setBirthDate(Date.valueOf(c.getBirthDate().toLocalDate().minusYears(1)));
			});
			returned = people.updateAll(persons);
			int index = 0;
			for (final PersonData pd : returned) {
				assert pd.getBirthYear() == ageBefore[index]; // shouldn't have changed
				index++;
			}
			returned.forEach(c -> {
				assert people.load(new PersonData(c.getId())).getName().endsWith(" RENAMED \\>0</");
			});

			returned = people.loadAll(persons);
			index = 0;
			for (final PersonData pd : returned) {
				assert pd.getBirthYear() != ageBefore[index]; // should have changed
				index++;
			}

			ageBefore = returned.stream().mapToInt(PersonData::getBirthYear).toArray();
			returned.forEach(c -> {
				c.setName(c.getName().replace(" RENAMED \\>0</", ""));
				c.setBirthDate(Date.valueOf(c.getBirthDate().toLocalDate().minusYears(1)));
			});
			returned = people.updateAndReloadAll(persons);
			index = 0;
			for (final PersonData pd : returned) {
				assert pd.getBirthYear() != ageBefore[index]; // should have changed
				index++;
			}

			people.clear();
		}

		{
			final List<PersonData> persons = Arrays.asList(
					new PersonData("name1", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS))),
					new PersonData("name2", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS))),
					new PersonData("name3", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(4, TimeUnit.DAYS))),
					new PersonData("name4", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(5, TimeUnit.DAYS))),
					new PersonData("name5"));

			people.insertAll(persons);

			final List<PersonData> persons2 = new ArrayList<>();
			persons2.add(persons.get(0));
			persons2.add(persons.get(1));
			persons2.add(persons.get(2));
			persons2.add(persons.get(3));
			persons2.add(new PersonData(6900));

			List<PersonData> returned = people.loadIfExists(persons2, ArrayList::new);
			assert returned.size() < persons2.size() : returned;

			returned = people.filterExists(persons2, ArrayList::new);
			assert returned.size() < persons2.size() : returned;

			returned = people.filterExistsUnique(persons2, ArrayList::new);
			assert returned.size() < persons2.size() : returned;

			returned = people.deleteIfExists(persons2, ArrayList::new);
			assert returned.size() < persons2.size() : returned;
		}
	}

}
