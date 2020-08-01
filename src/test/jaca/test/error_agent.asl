!acme.
//!test_make_initfailed.

// artifact creation
+!test_make_initfailed
	<- makeArtifact(counter,"acme.Counter",[1],Id).

+!test_make_already_existing
	<- 	makeArtifact(counter,"acme.Counter",[],Id);
		makeArtifact(counter,"acme.Counter",[],Id2).

+!test_make_wrongtype
	<- makeArtifact(counter,"acme.Counter2",[],Id).

// operation execution

+!test_wrongop
	<- makeArtifact(counter,"acme.Counter",[],Id);
	   wrong_inc(5) [artifact_id(Id)].

+!test_op_with_wrongparams
	<- makeArtifact(counter,"acme.Counter",[],Id);
	   inc("x") [artifact_id(Id)].

-!G <- println("goal failure: ",G).

-X <- println("gen failure: ",G).

+!test 
	<- makeArtifact(counter,"acme.Counter",[],Id);
	   focus(Id);
	   inc.
	   
+count(X)
	<- println(X);
		makeArtifact(counter,"acme.Counter",[1],Id). // error
	   	   
	   
/* 
-!G [error(H),error_msg(Msg),code(C),code_line(L),code_src(Src),env_failure_reason(R)]
	<- println("error: ",H," msg: ",Msg);
	   println("code: ",C," - line: ",L," - src: ",Src);
	   println("reason ",R).

-!G [error(H),error_msg(Msg),code(C),code_line(L),code_src(Src)]
	<- println("error: ",H," msg: ",Msg);
	   println("code: ",C," - line: ",L," - src: ",Src);
	   println("reason ",R).
*/
	   