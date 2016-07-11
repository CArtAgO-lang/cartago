package cartago.util.agent;

import java.util.*;
import cartago.*;

public class ObsPropMap {

	private HashMap<String,ArrayList<ArtifactObsProperty>> props;
	private HashMap<Long,ArrayList<ArtifactObsProperty>> idListMap;
	
	public ObsPropMap(){
		props = new HashMap<String,ArrayList<ArtifactObsProperty>>();
		idListMap = new HashMap<Long,ArrayList<ArtifactObsProperty>>();
	}
	
	public synchronized void addProperties(ArtifactId id, List<cartago.ArtifactObsProperty> list){
		for (cartago.ArtifactObsProperty p: list){
			add(new ArtifactObsProperty(id,p.getId(),p.getName(),p.getValues()));
		}
	}

	public synchronized void updateProperty(ArtifactId aid, cartago.ArtifactObsProperty obs){
		long id = obs.getId();
		List<ArtifactObsProperty> propList = idListMap.get(id);
		Iterator<ArtifactObsProperty> it = propList.iterator();
		while (it.hasNext()){
			ArtifactObsProperty prop = it.next();
			if (prop.getId() == id){
				it.remove();
				propList.add(new ArtifactObsProperty(aid,obs.getId(),obs.getName(),obs.getValues()));
				break;
			}
		}
	}

	public synchronized void remove(cartago.ArtifactObsProperty obs){
		long id = obs.getId();
		List<ArtifactObsProperty> propList = idListMap.remove(id);
		if (propList != null){
			Iterator<ArtifactObsProperty> it = propList.iterator();
			while (it.hasNext()){
				ArtifactObsProperty prop = it.next();
				if (prop.getId() == id){
					it.remove();
					break;
				}
			}
		}
	}

	public synchronized void removeProperties(ArtifactId id){
		Iterator<Map.Entry<String, ArrayList<ArtifactObsProperty>>> it =  props.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, ArrayList<ArtifactObsProperty>> entry = it.next();
			Iterator<ArtifactObsProperty> it2 =  entry.getValue().iterator();
			while (it2.hasNext()){
				ArtifactObsProperty p = it2.next();
				if (p.getArtifactId().equals(id)){
					idListMap.remove(p.getId());
					it2.remove();
				}
			}
		}
	}
	
	public synchronized void add(ArtifactId aid, cartago.ArtifactObsProperty prop){
		add(new ArtifactObsProperty(aid,prop.getId(),prop.getName(),prop.getValues()));	
	}

	public synchronized void add(ArtifactObsProperty prop){
		ArrayList<ArtifactObsProperty> list = props.get(prop.getName());
		if (list == null){
			list = new ArrayList<ArtifactObsProperty>();
			props.put(prop.getName(), list);
		}
		list.add(prop);
		idListMap.put(prop.getId(), list);
	}

	public synchronized ArtifactObsProperty getByName(String name){
		ArrayList<ArtifactObsProperty> list = props.get(name);
		if (list != null){
			return list.get(0);
		} else {
			return null;
		}
	}
	
	/*
	public synchronized ArtifactObsProperty removeByName(String name){
		ArrayList<ArtifactObsProperty> list = props.get(name);
		if (list != null){
			ArtifactObsProperty prop = list.remove(0);
			idListMap.remove(prop.getId());
			return prop;
		} else {
			return null;
		}
	}*/

	public synchronized ArtifactObsProperty get(String name, Object... values){
		ArrayList<ArtifactObsProperty> list = props.get(name);
		if (list != null){
			if (values.length == 0){
				return list.get(0);
			} else {
				for (ArtifactObsProperty prop: list){
					if (prop.match(name, values)){
						return prop;
					}
				}
				return null;
			}
		} else {
			return null;
		}
	}
	
	public synchronized ArtifactObsProperty getPropValue(String name, Object... values){
		return get(name,values);
	}


	public synchronized ArtifactObsProperty getPropValueByName(String name){
		return getByName(name);
	}

}
