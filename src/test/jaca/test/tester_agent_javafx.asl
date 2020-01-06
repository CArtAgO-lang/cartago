apiKey("AIzaSyDiPt735ULDnFl9Iwz4ZyeEzt1LKlxOVyE").

// !test_web_service.
!test_map("Paris, France").
// !test_web.
// !test_fft.
// !test_javafx.


+!test_web_service
  <- makeArtifact("web service","maop_ch10.WebService",[],Id);
     acceptGET("/api/count");
     acceptPOST("/api/count/inc");
     focus(Id);
     +count(0);
     start(8090).

+new_req("get","/api/count", Req) : count(C)
  <- .concat("{ \"count\": ", C, " }", Reply);
     println("new GET req on /api/count arrived. Replying: ",Reply);
     sendResponse(Req, Reply).	     

+new_req("post","/api/count/inc", Req) : count(C)
  <- getBodyAsJson(Req,Body);
     C1 = C + 1;
     -+count(C1);
     .concat("{ \"count\": ", C1, " }", Reply);
     println("new POST req on /api/count/inc arrived. Replying: ",Reply);
     sendResponse(Req, Reply).	     
     
+!test_map(Place) : apiKey(APIKey)
  <- makeArtifact("map","maop_ch10.MapArtifact",[APIKey]);
     println("Requesting information about: ",Place,"...");
     getGeoCoordinates(Place,Lat,Long);
     println("Results - latitude: ",Lat,", longitude: ",Long). 
     
+!test_web
  <- makeArtifact("web","maop_ch10.WebResource",[],Id);
     print("posting a new dweet on maop-book thing...");
     post("https://dweet.io:443/dweet/for/maop-book","{ \"msg\": \"hello, world!\" }", _);
     println("done.");
     println("getting the latest dweet on maop-book...");
     get("https://dweet.io:443/get/latest/dweet/for/maop-book",Res);
     println(Res).
     
// get("https://scastagnoli.dyndns.org:8080/api/temp",Res)[artifact_id(Id)];

+!test_fft
  <- makeArtifact("calc","mytools.FFTCalculator",[]);
     cartago.new_array("double[]",[5.0, -2.0, 1.0, 2.0], Data);
     fftTransform(Data, Res);
     !print_result(Res).

+!print_result([]).
+!print_result([V|T]) <-
	 cartago.invoke_obj(V,getReal,Re);
	 cartago.invoke_obj(V,getImaginary,Im);
	 !print_complex(Re,Im);
	 !print_result(T).

+!print_complex(Re,0) <- print("( ", Re, " )").
+!print_complex(0,Im) <- print("( ", Im, "j )").
+!print_complex(Re,Im) : Im < 0 <- print("( ", Re, " - ",-Im, "i )").
+!print_complex(Re,Im) <- print("( ", Re, " + ",Im, "i )").

+!test_javafx
  <-  makeArtifact("myArtifact","test.MainWindowArtifact",[],Id);
  	  focus(Id);
  	  L = [ name("Sofia"), age(11) ];
  	  .println("ready.", L).
  	  
+button("pressed") 
	<- println("Hello!").

