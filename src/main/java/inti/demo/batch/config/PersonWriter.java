package inti.demo.batch.config;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import inti.demo.batch.dao.PersonRepository;
import inti.demo.batch.entity.Person;

@Component
public class PersonWriter implements ItemWriter<Person> {

	@Autowired
	private PersonRepository personDao;

	@Override
	public void write(List<? extends Person> items) throws Exception {
		for (Person person : items) {
			personDao.save(person);
		}
	}

}
