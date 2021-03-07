!do_test.


+!do_test <-
	.print("waiting to be killed.").

+!focus_art(A) <-
  lookupArtifact(A,Aid);
	focus(Aid);
	.print("Fucused at ",A).

+count(X) <- .print("Count is ".X).
