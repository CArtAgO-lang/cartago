package jaca;

import cartago.Op;
import jason.asSyntax.Atom;

public class NameSpaceOp extends Op {
	private Atom NS;

	public NameSpaceOp(String opName, Object[] args, Atom nsp) {
		super(opName, args);
		NS = nsp;
	}

	public Atom getNS() {
		return NS;
	}
}
