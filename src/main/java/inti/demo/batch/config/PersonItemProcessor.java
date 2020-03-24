package inti.demo.batch.config;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import inti.demo.batch.entity.Person;

@Component
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	@Autowired
	public JavaMailSender emailSender;
	
    @Override
    public Person process(Person person) throws Exception {
        /*
            Possibilité d'ajouter des traitements sur l'item person avant insertion en base de donnee
            Exemple : generation mot de passe
         */

        person.setPassword(generatePassword(10));
        
		// Envoyer un mail de notification
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(person.getEmail());
		email.setSubject("Nouvelle Plateforme! :)");
		email.setText("Bonjour "+person.getNom()+"! \n\n"
				+ "Une nouvelle plateforme disponible pour vous. \n"
				+ "Voici votre password (vous pouvez le modifier après connection) : \n"
				+ "\t "+person.getPassword()+"\t\n\n"
				+ "Votre Université,");
		emailSender.send(email);

        return person;
    }

    private  String generatePassword(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }
}