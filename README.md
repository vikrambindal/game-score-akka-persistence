# game-score-akka-persistence

The sample demo project allows to use a player cordinator to create a player and then reduce the health of player, check its health status, and simulate crash so concept of akka persistence can be demonstrated. When the actor crashes, the event sourcing mechanism is used where the entries stored in journal are replayed to restore the previous state of the actor.  Current in-memory storage is used.
