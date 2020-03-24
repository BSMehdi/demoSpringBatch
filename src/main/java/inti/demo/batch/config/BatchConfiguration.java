package inti.demo.batch.config;

import java.util.Date;

import javax.xml.bind.ValidationException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import inti.demo.batch.entity.Person;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {

    @Value("classpath:/persons.csv")
    private Resource inputResource;

    // Notre ItemReader
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
        		//attribuer un name à notre ItemReader
                .name("personItemReader")
				// On ignore la première ligne du ficher persons.csv
                .linesToSkip(1)
                .resource(inputResource)
                .delimited()
                .names(new String[]{"id", "nom", "prenom", "email"})
				//Préciser le type des objets pour le mapping <Person>
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);

                }})
                .build();
    }
    
    @Autowired
    JobCompletionNotificationListener listener;
    
    @Autowired
    PersonItemProcessor processor;
    
    @Autowired
    PersonWriter writer;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Person, Person> chunk(10)
                    .faultTolerant()
                    .skip(ValidationException.class)
                    .skip(FlatFileParseException.class)
                    .skip(ItemStreamException.class)
                    .skipLimit(9)
                    .reader(reader())
                    .processor(processor)
                    .writer(writer)
                    .build();
    }

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    JobLauncher jobLauncher;

    @Scheduled(fixedDelay = 30*1000) // On va lancer cette méthode toutes les 30 secondes
    public void scheduleFixedDelayTask() throws Exception {

        System.out.println(" ########## Job lancé "+ new Date() + " ##########");
        JobParameters param = new JobParametersBuilder().addString("JobID",
                String.valueOf(System.currentTimeMillis())).toJobParameters();
        
        //importer le Job
        Job job = jobBuilderFactory
                .get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
        
        JobExecution execution = jobLauncher.run(job, param);
        System.out.println("Job finished with status :" + execution.getStatus());

    }

}	//je vous ai dis que c'est le dernier morçeau :)
