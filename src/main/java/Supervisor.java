import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

public class Supervisor extends AbstractActor {

	private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES),
			DeciderBuilder.match(ArithmeticException.class, e -> SupervisorStrategy.resume())
					.match(NullPointerException.class, e -> SupervisorStrategy.restart())
					.match(IllegalArgumentException.class, e -> SupervisorStrategy.stop())
					.matchAny(o -> SupervisorStrategy.escalate()).build());

	@Override
	public void preStart() throws Exception {
		System.out.println("Parent - preStart()");
	}
	
	@Override
	public void postStop() throws Exception {
		System.out.println("Parent - postStop()");
	}
	
	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	ActorRef child = null;
	
	public void displayMenu(String s) {
		
		System.out.println("<playername> create");
		System.out.println("<playername> status");
		System.out.println("<playername> hit");
		System.out.println("<playername> crash");
		
		while(true) {
			
			Scanner scanner = new Scanner(System.in);			
			String userInput = scanner.nextLine();
			
			StringTokenizer stringTokenizer = new StringTokenizer(userInput, " ");
			String playerName = stringTokenizer.nextToken();
			String command = stringTokenizer.nextToken();
			
			switch(command) {
				case "create":
					child = getContext().actorOf(Child.props(playerName));
					getContext().watch(child);
					break;
				case "status":
					child.tell(new String("status"), getSelf());
					break;
				case "hit":
					child.tell(new Integer(Integer.parseInt(stringTokenizer.nextToken())), getSelf());
					break;
				case "crash":
					Exception e = new NullPointerException();
					child.tell(e, getSelf());
					strategy.restartChild(child, e, false);
					break;
				default:
					break;
			}
		}
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.matchEquals("display", this::displayMenu)
				.build();
	}
}