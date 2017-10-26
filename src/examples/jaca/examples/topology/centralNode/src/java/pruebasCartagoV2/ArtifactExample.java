package pruebasCartagoV2;

import cartago.Artifact;
import cartago.ArtifactId;
import cartago.OPERATION;


public class ArtifactExample extends Artifact 
{

	public ArtifactExample() 
	{
		
	}
	
	public void ini()
	{
		
	}
	
	//it works
	@OPERATION 
	public void test1(ArtifactId id)
	{
		System.out.println(id.getId());
	}
	
	//it works
	@OPERATION
	public void test2(Object [] id)
	{
		System.out.println(((ArtifactId)id[0]).getId());
	}
	
	//it does not work
	@OPERATION
	public void test3(ArtifactId [] id)
	{
		System.out.println(id[0].getId());
	}
}
