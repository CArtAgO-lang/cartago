package cartago;

public interface IObsPropMap extends java.io.Serializable {

	ArtifactObsProperty getPropValue(String name, Object... values);
	ArtifactObsProperty getPropValueByName(String name);

}
