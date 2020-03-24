package inti.demo.batch.config;

import java.util.Date;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

//	@Autowired
//	public JavaMailSender emailSender;
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("########## job terminé "+new Date()+ " ##########");

		if( jobExecution.getStatus() == BatchStatus.COMPLETED ){
			System.out.println("---->>> job success");

				// Envoyer un mail de notification
//				SimpleMailMessage email = new SimpleMailMessage();
//				email.setTo("mbensalha@adaming.fr");
//				email.setSubject("New Student in Database");
//				email.setText("Hello, \n A new Student has been added to database. \n\n Thx");
//				emailSender.send(email);

				// deplacer les fichiers traités

			} else if(jobExecution.getStatus() == BatchStatus.FAILED){
				//job FAIL
			System.out.println("---->>> job échec");
		}
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("---->>> Job débute");
	}

}
