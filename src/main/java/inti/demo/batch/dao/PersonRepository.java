package inti.demo.batch.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inti.demo.batch.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, String>{

}
