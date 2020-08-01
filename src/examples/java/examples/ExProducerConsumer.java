package examples;

import cartago.CartagoEnvironment;

public class ExProducerConsumer {

	public static void main(String[] args) throws Exception {
		/* starting CArtAgO */
		CartagoEnvironment.startEnvironment();
		/* Declaration of 10 producers  and 10 consumers*/
		ProducerAgent[] producers = new ProducerAgent[10];
		ConsumerAgent[] consumers = new ConsumerAgent[10];
		
		/* Spawning the agents */
		for (int i=0; i<10; i++){
			producers[i] = new ProducerAgent("Producer"+i);
			consumers[i] = new ConsumerAgent("Consumer"+i);
			producers[i].start();
			consumers[i].start();
		}
	}
}
