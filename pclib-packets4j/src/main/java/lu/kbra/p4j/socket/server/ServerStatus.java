package lu.kbra.p4j.socket.server;

public enum ServerStatus {

	UNINITIALIZED(), BOUND(), ACCEPTING(), REFUSING(), CLOSING(), CLOSED(), ERROR();

}
