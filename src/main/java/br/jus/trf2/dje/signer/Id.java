package br.jus.trf2.dje.signer;


public class Id {
	long cadernoid;

	public Id(String id) {
		this.cadernoid = Long.valueOf(id);
	}

	public Id(long cadernoid) {
		this.cadernoid = cadernoid;
	}

	public String toString() {
		return Long.toString(cadernoid);
	}
}
