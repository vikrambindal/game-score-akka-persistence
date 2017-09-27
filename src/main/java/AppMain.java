import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class AppMain {

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("mysystem");
		Props superprops = Props.create(Supervisor.class);
		ActorRef supervisor = system.actorOf(superprops, "supervisor");
		supervisor.tell(new String("display"), ActorRef.noSender());
	}

}
