import akka.actor.Props;
import akka.persistence.AbstractPersistentActor;

public class Child extends AbstractPersistentActor {
	
	int numOfPoints;
	String playerName;

	public static Props props(String playerName) {
		return Props.create(Child.class, playerName, 100);
	}
	
	public Child() {
		
	}
	
	public Child(String playerName, int numOfPoints) {
		this.playerName = playerName;
		this.numOfPoints = numOfPoints;
	}
	
	public void showStatus(String s) {
		System.out.println("Child " + playerName + " health " + numOfPoints);
	}
	
	public void hitPlayer(Integer i) {
		System.out.println("Child " + playerName + " hit " + i.intValue());
		System.out.println("Child " + playerName + " persisting");
		
		persist(i, it -> {
			System.out.println("Child " + playerName + " persisted with hit of " + it.intValue());
			numOfPoints -= i;
		});
	}
	
	public void recoverHitMessage(Integer i) {
		System.out.println("Child " + playerName + " replaying hit message ");
		numOfPoints -= i;
	}
	
	@Override
	public void preStart() throws Exception {
		System.out.println("Child " + playerName + " created");
		super.preStart();
	}
	
	@Override
	public void postStop() {
		System.out.println("Child " + playerName + " stopped");
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		System.out.println("Child " + playerName + " restarted");
		super.postRestart(reason);
	}
		
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.matchEquals("status", this::showStatus)
				.match(Integer.class, this::hitPlayer)
				.match(Exception.class, exception -> { 
					System.out.println("Crashing Actor");
					throw exception;})
				.build();
	}

	@Override
	public String persistenceId() {
		return "childactor";
	}

	@Override
	public Receive createReceiveRecover() {
		return receiveBuilder()
				.match(Integer.class, this::recoverHitMessage)
				.build();
	}
}
