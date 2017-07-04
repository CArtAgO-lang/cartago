package cartago;

import java.util.*;

public class ObsPropMap implements IObsPropMap {

	private HashMap<String,ArrayList<ObsProperty>> props;
	
	private ArrayList<ObsProperty> propsChanged;
	private ArrayList<ObsProperty> propsAdded;
	private ArrayList<ObsProperty> propsRemoved;

	public ObsPropMap(){
		props = new HashMap<String,ArrayList<ObsProperty>>();
		propsChanged = new ArrayList<ObsProperty>();
		propsAdded = new ArrayList<ObsProperty>();
		propsRemoved = new ArrayList<ObsProperty>();
		
		
	}
	
	public void add(ObsProperty prop){
		ArrayList<ObsProperty> list = props.get(prop.getName());
		if (list == null){
			list = new ArrayList<ObsProperty>();
			props.put(prop.getName(), list);
		}
		list.add(prop);		
		propsAdded.add(prop);
	}
	
	private void addProp(ObsProperty prop){
		ArrayList<ObsProperty> list = props.get(prop.getName());
		if (list == null){
			list = new ArrayList<ObsProperty>();
			props.put(prop.getName(), list);
		}
		list.add(prop);
	}
	
	public ObsProperty getByName(String name){
		ArrayList<ObsProperty> list = props.get(name);
		if (list != null){
			return list.get(0);
		} else {
			return null;
		}
	}

	public ObsProperty removeByName(String name){
		ArrayList<ObsProperty> list = props.get(name);
		if (list != null){
			ObsProperty p = list.remove(0);
			propsRemoved.add(p);
			if (list.size()==0){
				props.remove(name);
			}
			return p;
		} else {
			return null;
		}
	}

	public ObsProperty get(String name, Object... values){
		ArrayList<ObsProperty> list = props.get(name);
		if (list != null){
			if (values.length == 0){
				return list.get(0);
			} else {
				for (ObsProperty prop: list){
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
	

	public ObsProperty remove(String name, Object... values){
		ArrayList<ObsProperty> list = props.get(name);
		if (list != null){
			if (values.length == 0){
				props.remove(name);
				ObsProperty p = list.get(0);
				propsRemoved.add(p);
				return p;
			} else {
				Iterator<ObsProperty> it = list.iterator();
				while (it.hasNext()){
					ObsProperty prop = it.next();
					if (prop.match(name, values)){	
						try {
							it.remove();
							propsRemoved.add(prop);
							return prop;
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				return null;
			}
		} else {
			return null;
		}
	}
	
	private void remove(String name, long id){
		ArrayList<ObsProperty> list = props.get(name);
		if (list != null){
				Iterator<ObsProperty> it = list.iterator();
				while (it.hasNext()){
					ObsProperty prop = it.next();
					if (prop.getId() == id){
						try {
							it.remove();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
		} else {
			throw new IllegalArgumentException("Internal error: removing a not existing prop "+name+" "+id);
		}
	}
	
	public void addPropChanged(ObsProperty prop){
		propsChanged.add(prop);
	}

	public void addPropAdded(ObsProperty prop){
		propsAdded.add(prop);
	}

	public void addPropRemoved(ObsProperty prop){
		propsRemoved.add(prop);
	}

	
	public ArtifactObsProperty[] getPropsChanged(){
		if (propsChanged.size() > 0){
			ArtifactObsProperty[] v = new ArtifactObsProperty[propsChanged.size()];
			for (int i = 0; i < v.length; i++){
				v[i] = propsChanged.get(i).getUserCopy();
			}
			return v;
		} else {
			return null;
		}
	}
	
	public ArtifactObsProperty[] getPropsAdded(){
		if (propsAdded.size() > 0){
			ArtifactObsProperty[] v = new ArtifactObsProperty[propsAdded.size()];
			for (int i = 0; i < v.length; i++){
				v[i] = propsAdded.get(i).getUserCopy();
			}
			return v;
		} else {
			return null;
		}
	}

	public ArtifactObsProperty[] getPropsRemoved(){
		if (propsRemoved.size() > 0){
			ArtifactObsProperty[] v = new ArtifactObsProperty[propsRemoved.size()];
			for (int i = 0; i < v.length; i++){
				v[i] = propsRemoved.get(i).getUserCopy();
			}
			return v;
		} else {
			return null;
		}
	}
	
	public void commitChanges(){
		for (ObsProperty p: propsChanged){
			p.commitChanges();
		}
		propsChanged.clear();
		propsRemoved.clear();
		propsAdded.clear();
	}

	public void rollbackChanges(){
		for (ObsProperty p: propsChanged){
			p.rollbackChanges();
		}
		propsChanged.clear();
		for (ObsProperty p: propsAdded){
			remove(p.getName(), p.getId());
		}
		propsRemoved.clear();
		for (ObsProperty p: propsRemoved){
			addProp(p);
		}
		propsAdded.clear();
	}

	// 
	
	public ArtifactObsProperty getPropValue(String name, Object... values){
		ObsProperty prop = get(name,values);
		if (prop!=null){
			return new ArtifactObsProperty(prop.getFullId(),prop.getId(),prop.getName(),prop.getValues()).setAnnots(prop.getAnnots());
		} else {
			return null;
		}
	}

	public ArrayList<ArtifactObsProperty> readAll(){
		ArrayList<ArtifactObsProperty> list = new ArrayList<ArtifactObsProperty>();
		for (ArrayList<ObsProperty> l: props.values()){
			for (ObsProperty p: l){
				list.add(p.getUserCopy());
			}
		}
		return list;
	}
	
	public ArtifactObsProperty getPropValueByName(String name){
		ObsProperty prop = getByName(name);
		if (prop!=null){
			return new ArtifactObsProperty(prop.getFullId(),prop.getId(),prop.getName(),prop.getValues()).setAnnots(prop.getAnnots());
		} else {
			return null;
		}
	}

}
