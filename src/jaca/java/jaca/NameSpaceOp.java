package jaca;

import cartago.Op;
import jason.asSyntax.Atom;

public class NameSpaceOp extends Op {
	private Atom NS;

	public NameSpaceOp(Op op, Atom nsp) {
		super(op.getName(), op.getParamValues());
		NS = nsp;
	}

	public Atom getNS() {
		return NS;
	}
}
