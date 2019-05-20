/**
 * CArtAgO - DEIS, University of Bologna
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cartago;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.logging.LogManager;
import java.io.*;

/**
 * Base class for defining artifacts.
 * 
 * @author aricci
 * 
 */
public abstract class Artifact {

	// private Logger logger = Logger.getLogger("log");

	private ArtifactId id;
	Workspace wsp;

	protected OpId thisOpId;
	private OpExecutionFrame opExecFrame;
	private ArrayList<OpExecutionFrame> opsInExecution;

	private HashMap<String, Method> guardMap;
	private HashMap<String, OpDescriptor> operationMap;

	private int obsPropId;
	private ObsPropMap obsPropertyMap;
	private HashMap<String, ArtifactOutPort> outPortsMap;

	private ReentrantLock lock;
	private Condition guards;

	private InterArtifactCallback opCallback;
	private Manual manual;
	private java.util.concurrent.atomic.AtomicInteger opIds;
	
	private AgentId creatorId;
	
	/* experimental */
	
	/* position of the artifact in the workspace */
	protected AbstractWorkspacePoint position;
	
	/* defines the radius in which the artifact can be perceived*/
	protected double observabilityRadius;
	
	void bind(ArtifactId id, AgentId creatorId, Workspace env) throws CartagoException {
		this.id = id;
		this.creatorId = creatorId;
		this.wsp = env;
		obsPropId = 0;
		opIds = new java.util.concurrent.atomic.AtomicInteger(0);

		lock = new ReentrantLock(true);
		guards = lock.newCondition();

		opsInExecution = new ArrayList<OpExecutionFrame>();
		guardMap = new HashMap<String, Method>();
		operationMap = new HashMap<String, OpDescriptor>();
		outPortsMap = new HashMap<String, ArtifactOutPort>();

		obsPropertyMap = new ObsPropMap();
		opCallback = new InterArtifactCallback(this.lock);

		if (getClass().isAnnotationPresent(ARTIFACT_INFO.class)) {
			ARTIFACT_INFO info = getClass().getAnnotation(ARTIFACT_INFO.class);

			for (OUTPORT port : info.outports()) {
				outPortsMap.put(port.name(), new ArtifactOutPort(port.name()));
			}

			/*
			 * loading the manual
			 *//*
			if (!info.manual_file().equals("")) {
				try {
					String src = env.loadManualSrc(info.manual_file());
					manual = env.registerManual(this.getClass().getName(), src);
					// log("artifact manual loaded "+manual.getName());
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("LOCAL PATH: "
							+ new File(".").getAbsolutePath());
				}
			} else {
				manual = Manual.EMPTY_MANUAL;
			}*/
		}

		// setting ops and obs properties
		try {
			setupOperations();
		} catch (CartagoException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * Set up artifact operations.
	 * 
	 * Method called during artifact initialization to set up operations. By
	 * default, reflection is used to link operations to annotated methods.
	 * 
	 * @throws CartagoException
	 */
	protected void setupOperations() throws CartagoException {
		Class<?> c = getClass();
		while (c != null) {
			Method[] methods = c.getDeclaredMethods();
			for (Method m : methods) {
				// log("method "+m.getName());
				// log("annotations "+m.getAnnotations().length);
				if (m.isAnnotationPresent(OPERATION.class)) {
					OPERATION op = m.getAnnotation(OPERATION.class);
					String guard = op.guard();
					ArtifactGuardMethod guardBody = null;
					if (!guard.equals("")) {
						Method guardMethod = getMethodInHierarchy(guard, m
								.getParameterTypes());
						if (guardMethod == null) {
							throw new CartagoException("invalid guard: "
									+ guard);
						} else {
							guardBody = new ArtifactGuardMethod(this,
									guardMethod);
						}
					}
					String name = null;
					if (!m.isVarArgs()) {
						name = Artifact.getOpKey(m.getName(), m
								.getParameterTypes().length);
					} else {
						name = Artifact.getOpKey(m.getName(), -1);
					}
					OpDescriptor opdesc = new OpDescriptor(
							name,
							new ArtifactOpMethod(this, m), guardBody,
							OpDescriptor.OpType.UI);
					// log("registering "+name);
					operationMap.put(name, opdesc);
				} else if (m.isAnnotationPresent(LINK.class)) {
					LINK op = m.getAnnotation(LINK.class);
					String guard = op.guard();
					ArtifactGuardMethod guardBody = null;
					if (!guard.equals("")) {
						Method guardMethod = getMethodInHierarchy(guard, m
								.getParameterTypes());
						if (guardMethod == null) {
							throw new CartagoException("invalid guard: "
									+ guard);
						} else {
							guardBody = new ArtifactGuardMethod(this,
									guardMethod);
						}
					}
					String name = Artifact.getOpKey(m.getName(), m
							.getParameterTypes().length);
					OpDescriptor opdesc = new OpDescriptor(
							name,
							new ArtifactOpMethod(this, m), guardBody,
							OpDescriptor.OpType.LINK);
					// log("registering "+name);
					operationMap.put(name, opdesc);
				} else if (m.isAnnotationPresent(INTERNAL_OPERATION.class)) {
					INTERNAL_OPERATION op = m
							.getAnnotation(INTERNAL_OPERATION.class);
					String guard = op.guard();
					ArtifactGuardMethod guardBody = null;
					if (!guard.equals("")) {
						Method guardMethod = getMethodInHierarchy(guard, m
								.getParameterTypes());
						if (guardMethod == null) {
							throw new CartagoException("invalid guard: "
									+ guard);
						} else {
							guardBody = new ArtifactGuardMethod(this,
									guardMethod);
						}
					}
					String name = Artifact.getOpKey(m.getName(), m
							.getParameterTypes().length);
					OpDescriptor opdesc = new OpDescriptor(
							name,
							new ArtifactOpMethod(this, m), guardBody,
							OpDescriptor.OpType.INTERNAL);
					// log("registering "+name);
					operationMap.put(name, opdesc);
				} else if (m.isAnnotationPresent(GUARD.class)) {
					String name = Artifact.getOpKey(m.getName(), m
							.getParameterTypes().length);
					guardMap.put(name, m);
				}
			}
			c = c.getSuperclass();
		}
	}

	/**
	 * Get the name of the file containing the manual for the specified artifact
	 * template, by accessing to ARTIFACT_INFO annotation.
	 * 
	 * @param artType
	 *            artifact template
	 * @return file name
	 * @throws UnknownArtifactException
	 */
	public static String getManualSrcFile(String artType)
			throws UnknownArtifactException {
		try {
			Class<Artifact> cl = (Class<Artifact>) Class.forName(artType);
			if (cl.isAnnotationPresent(ARTIFACT_INFO.class)) {
				ARTIFACT_INFO info = cl.getAnnotation(ARTIFACT_INFO.class);
				return info.manual_file();
			} else {
				return "";
			}
		} catch (Exception ex) {
			throw new UnknownArtifactException(artType);
		}
	}

	/**
	 * Get the key of an operation, given its name and n args.
	 * 
	 * The key is used in maps.
	 * 
	 * @param opName
	 *            op name
	 * @param nargs
	 *            number of parameters
	 */
	public static String getOpKey(String opName, int nargs) {
		if (nargs >= 0) {
			return opName + "/" + nargs;
		} else {
			// var args
			return opName + "/_";
		}
	}

	private void doInit(ArtifactConfig cfg) throws CartagoException {
		try {
			lock.lock();
			Method m = getMethodInHierarchy2("init", cfg.getTypes());
			if (m != null) {
				try {
					m.setAccessible(true);
					m.invoke(this, cfg.getValues());
					commitObsStateChanges();
				} catch (Exception ex) {
					ex.printStackTrace();
					obsPropertyMap.rollbackChanges();
					throw new CartagoException("init_failed");
				}
			} else if (cfg.getTypes().length > 0) {
				throw new CartagoException("init_failed");
			}
		} finally {
			lock.unlock();
		}
	}

	private Method getMethodInHierarchy(String name, Class<?>[] types) {
		Class<?> cl = getClass();
		do {
			try {
				return cl.getDeclaredMethod(name, types);
			} catch (Exception ex) {
				cl = cl.getSuperclass();
			}
		} while (cl != null);
		return null;
	}

	private Method getMethodInHierarchy2(String name, Class<?>[] types) {
		Class<?> cl = getClass();
		do {
			Method[] methods = cl.getDeclaredMethods();
			for (Method m : methods) {
				if (m.getName().equals(name)
						&& m.getParameterTypes().length == types.length) {
					return m;
				}
			}
			cl = cl.getSuperclass();
		} while (cl != null);
		return null;
	}

	/**
	 * Core method that executes an artifact operation. Called by the kernel
	 * through the adapter.
	 * 
	 * @param info
	 * @throws CartagoException
	 */
	private void doOperation(OpExecutionFrame info) throws CartagoException {
		ICartagoLoggerManager log = wsp.getLoggerManager();
		try {
			lock.lock();
			Op op = info.getOperation();
			// log("inside doOperation "+op.getName()+" "+info.getAgentBodyId().getAgentName());
			String name = op.getName();
			// System.out.println("LOOKING FOR "+name+"_"+param.getValues().length);
			OpDescriptor opDesc = operationMap.get(Artifact.getOpKey(name, op
					.getParamValues().length));
			boolean varargs = false;
			if (opDesc == null) {
				// try with var args op
				// log("doOp with varargs: "+op);
				opDesc = operationMap.get(Artifact.getOpKey(name, -1));
				if (opDesc == null) {
					if (!info.isInternalOp()) {
						String msg = "Unknown operation "+name+" on artifact "+this.id+" (type "+id.getArtifactType()+")";
						Tuple desc = new Tuple("unknown_operation",this.id,name);
						if (log.isLogging()){
							log.opFailed(System.currentTimeMillis(), info.getOpId(), this.id, info.getOperation(), msg, desc);
						}					
						info.notifyOpFailed(msg,desc);
					}
					return;
				}
				varargs = true;
			}

			IAlignmentTest test = info.getAlignmentTest();
			IArtifactOp opBody = null;
			IArtifactGuard guardBody = null;
			opBody = opDesc.getOp();
			guardBody = opDesc.getGuard();
			Object[] params = null;
			if (!varargs) {
				params = op.getParamValues();
			} else {
				Object[] flat = op.getParamValues();
				int len = opBody.getNumParameters();
				params = new Object[len];
				int var = flat.length - len + 1;
				Object[] varlist = new Object[var];
				for (int i = 0; i < len - 1; i++) {
					params[i] = flat[i];
				}
				params[len - 1] = varlist;
				for (int i = 0; i < var; i++) {
					varlist[i] = flat[len - 1 + i];
				}
				// log("prepared params: "+params.length);
			}
			if (log.isLogging()){
				log.opStarted(System.currentTimeMillis(), info.getOpId(), this.id, info.getOperation());
			}					
			try {
				// check guards
				boolean guardOK = true;
				if (guardBody != null) {
					guardOK = guardBody.eval(params);
					while (!guardOK) {
						guards.await();
						guardOK = guardBody.eval(params);
					}
				}

				/*
				 * Alignment test: check if the observable state of the artifact
				 * matches the one expected by the user
				 */
				if (test != null) {
					boolean aligned = test.match(this.obsPropertyMap);
					if (!aligned) {
						String msg = "Test alignment failed";
						Tuple desc = new Tuple("not_aligned");
						if (log.isLogging()){
							log.opFailed(System.currentTimeMillis(), info.getOpId(), this.id, info.getOperation(), msg, desc);
						}					
						info.notifyOpFailed(msg,desc);
						return;
					}
				}

				opsInExecution.add(info);
				opExecFrame = info;
				thisOpId = opExecFrame.getOpId();
				
				try {
					try {
						opBody.exec(params);
						commitObsStateChanges();
					} catch (InvocationTargetException ex) {
						if (!(ex.getTargetException() instanceof OperationFailedException)) {
							ex.printStackTrace();
							throw ex;
						}
					} finally {
						opsInExecution.remove(info);
					}
					if ((!info.isInternalOp())|(info.getActionId()==-2)) { //operations triggered by the AbstractWSPRuleEngine have info.getActionId()==-2
						if (!info.isFailed()) {
							// notify operation completed successfully
							if (log.isLogging()){
								log.opCompleted(System.currentTimeMillis(), info.getOpId(), this.id, info.getOperation());
							}					
							if (!info.completionNotified()){
								info.notifyOpCompletion();
							}
						} else {
							//log("OP FAILED: "+info.getFailureMsg()+" "+info.getFailureReason());
							String msg = info.getFailureMsg();
							Tuple desc = info.getFailureReason();
							if (log.isLogging()){
								log.opFailed(System.currentTimeMillis(), info.getOpId(), this.id, info.getOperation(), msg, desc);
							}					
							obsPropertyMap.rollbackChanges();
							info.notifyOpFailed();
						}
					}
				} catch (IllegalArgumentException ex) {
					/*
					ex.printStackTrace();
					System.out.println("expected param length: "+opBody.getNumParameters());
					for (Object p:params){
						System.out.println("ACTUAL PARAM "+p+" "+p.getClass());
					}
					for (Object p:((ArtifactOpMethod)opBody).getMethod().getParameterTypes()){
						System.out.println("EXPECTED PARAM "+p+" "+p.getClass());
					}
					*/
					StringBuffer msg = new StringBuffer(
							"Wrong operation arguments: operation "+op.getName()+" on artifact "+this.id+" (type: "+id.getArtifactType()+")\n"+
							"- expected "+
							((ArtifactOpMethod)opBody).getMethod().getParameterTypes().length+
							" parameter(s) - types: ");
					int n = 0;
					for (Object p:((ArtifactOpMethod)opBody).getMethod().getParameterTypes()){
						if (n != 0) {
							msg.append(", ");
						}
						msg.append(""+p);
						n++;
					}
					msg.append("\n- actual parameter(s): ");
					n = 0;
					for (Object p:params){
						if (n != 0) {
							msg.append(", ");
						}
						msg.append(""+p+" type: "+p.getClass());
						n++;
					}

					Tuple desc =  new Tuple(
							"wrong_op_arguments", this.id, opBody.getName() + "/"
							+ opBody.getNumParameters());
					if (log.isLogging()){
						log.opFailed(System.currentTimeMillis(), info.getOpId(), this.id, info.getOperation(), msg.toString(), desc);
					}					
					
					if (!info.isInternalOp()) {
						info.setFailed(msg.toString(),desc);
						info.notifyOpFailed();
					}
				} catch (Exception ex) {
					/*
					ex.printStackTrace();
					System.out.println("expected param length: "+opBody.getNumParameters());
					for (Object p:params){
						System.out.println("ACTUAL PARAM "+p+" "+p.getClass());
					}
					for (Object p:((ArtifactOpMethod)opBody).getMethod().getParameterTypes()){
						System.out.println("EXPECTED PARAM "+p+" "+p.getClass());
					}
					*/
					String msg = "Unknown Operation";
					Tuple desc =  new Tuple(
							"unknown_operation", opBody.getName() + "/"
							+ opBody.getNumParameters());
					if (log.isLogging()){
						log.opFailed(System.currentTimeMillis(), info.getOpId(), this.id, info.getOperation(), msg, desc);
					}					
					
					if (!info.isInternalOp()) {
						info.setFailed(msg,desc);
						info.notifyOpFailed();
					}
				}

			} catch (Exception ex) {
				String msg = "Artifact internal error";
				Tuple desc =  new Tuple(
						"internal_error", opBody.getName() + "/"
						+ opBody.getNumParameters());
				if (log.isLogging()){
					log.opFailed(System.currentTimeMillis(), info.getOpId(), this.id, info.getOperation(), msg, desc);
				}					

				// ex.printStackTrace();
				info.setFailed(msg, desc);
				info.notifyOpFailed();
			}
		} finally {
			guards.signalAll();
			lock.unlock();
		}
	}	
	
	private void restoreOpExecContext(OpId id){
		this.thisOpId = id;
		boolean found = false;
		for (OpExecutionFrame frame: opsInExecution){
			if (frame.getOpId().equals(id)){
				this.opExecFrame = frame;
				found = true;
				break;
			}
		}
		if (!found){
			throw new IllegalArgumentException("INTERNAL ERROR: Op Exec Context cannot be restored.");
		}
	}
	/*
	 * Commit and make it observable the obs state 
	 */
	private void commitObsStateChanges(){
		//log("committing obs state changed:");
		ArtifactObsProperty[] changed = obsPropertyMap.getPropsChanged();
		ArtifactObsProperty[] added = obsPropertyMap.getPropsAdded();
		ArtifactObsProperty[] removed = obsPropertyMap.getPropsRemoved();	
		try {
			if (changed != null || added != null || removed != null){
				wsp.notifyObsEvent(id, null, changed, added, removed);
				guards.signalAll();
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		obsPropertyMap.commitChanges();
	}
	
	/*
	 * Commit and make it observable the obs state and a signal
	 */
	private void commitObsStateChangesAndSignal(AgentId target, Tuple signal){
		//log("committing obs state changed:");
		ArtifactObsProperty[] changed = obsPropertyMap.getPropsChanged();
		ArtifactObsProperty[] added = obsPropertyMap.getPropsAdded();
		ArtifactObsProperty[] removed = obsPropertyMap.getPropsRemoved();	
		try {
			if (target == null){
				wsp.notifyObsEvent(id, signal, changed, added, removed);
			} else {
				wsp.notifyObsEventToAgent(id, target, signal, changed, added, removed);
			}
			guards.signalAll();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		obsPropertyMap.commitChanges();
	}
	
	// inherited

	@OPERATION void observeProperty(String name, OpFeedbackParam<ArtifactObsProperty> prop){
		ObsProperty p = obsPropertyMap.getByName(name);
		if (p != null) {
			prop.set(p.getUserCopy());
		} else {
			failed("Property not found "+name);
		}
	}
	
	// API for programming artifacts

	/**
	 * Get the artifact unique identifier
	 */
	protected ArtifactId getId() {
		return id;
	}

	OpExecutionFrame getOpFrame(){
		return opExecFrame;
	}

	
	/**
	 * Get the identifier of the current user
	 * 
	 */
	protected AgentId getCurrentOpAgentId() {
		return this.opExecFrame.getAgentId();
	}

	@Deprecated
	public String getOpUserName() {
		return getCurrentOpAgentId().getAgentName();
	}

	
	/**
	 * Get the identifier of the current user artifact body (if available)
	 * 
	 */
	protected ArtifactId getCurrentOpAgentBody() {
		return wsp.getAgentBodyArtifact(opExecFrame.getAgentId());
	}


	protected void commit(){
		commitObsStateChanges();
	}
	
	/**
	 * Primitive to generate an event.
	 */
	protected void signal(String type, Object... objs) {
		try {
			commitObsStateChangesAndSignal(null, new Tuple(type, objs));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException("Error in generating the event.");
		}
	}

	/**
	 * Primitive to generate an event.
	 */
	protected void signal(AgentId target, String type, Object... objs) {
		try {
			commitObsStateChangesAndSignal(target,new Tuple(type, objs));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException("Error in generating the event.");
		}
	}
	/**
	 * Terminate current operation with a failure
	 * 
	 * @param reason
	 *            description of the failure
	 */
	protected void failed(String reason) {
		this.opExecFrame.setFailed(reason, null);
		throw new OperationFailedException();
	}

	/**
	 * Terminate current operation with a failure
	 * 
	 * @param reason
	 *            description of the failure
	 * @param tupleDesc
	 *            functor of a machine readable tuple describing the failure
	 * @param params
	 *            parameters of a machine readable tuple describing the failure
	 */
	protected void failed(String reason, String tupleDesc, Object... params) {
		this.opExecFrame.setFailed(reason, new Tuple(tupleDesc, params));
		throw new OperationFailedException();
	}

	// Observable property management functions
	
	/**
	 * Add an observable property
	 * 
	 * @param name
	 *            name of the property
	 * @param values
	 *            values of the property
	 */
	protected ObsProperty defineObsProperty(String name, Object... values) {			
			try {
				String fullId="obs_id_"+this.wsp.getId().getFullName()+this.id.getId()+"_"+obsPropId;
				ObsProperty prop = new ObsProperty(obsPropertyMap,obsPropId, fullId, name, values); 
				obsPropertyMap.add(prop);
				obsPropId++;
				return prop;
				//env.notifyObsPropAddedEvent(id, prop.getUserCopy());
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new IllegalArgumentException("invalid observable property: " + name);
			}
	}

	/**
	 * Remove an observable property
	 * 
	 * @param name
	 *            name of the property
	 */
	protected void removeObsProperty(String name) {
			ObsProperty prop = obsPropertyMap.removeByName(name);
			if (prop == null){
				throw new IllegalArgumentException(
						"invalid observable property: " + name);
			}
	}


	/**
	 * Remove an observable property
	 * 
	 * @param name name of the property
	 * @param values arguments of the property
	 */
	protected void removeObsPropertyByTemplate(String name, Object... values) {
			ObsProperty prop = obsPropertyMap.remove(name,values);
			if (prop == null){
				throw new IllegalArgumentException(
						"invalid observable property: " + name);
			}
	}

	protected ObsProperty getObsProperty(String name){
		return obsPropertyMap.getByName(name);
	}

	protected boolean hasObsProperty(String name){
		return obsPropertyMap.getByName(name)!=null;
	}

	protected ObsProperty getObsPropertyByTemplate(String name, Object... values){
		return obsPropertyMap.get(name,values);
	}
	
	protected boolean hasObsPropertyByTemplate(String name, Object... values){
		return obsPropertyMap.get(name,values) != null;
	}

	
	/** 
	 * For compatibility reason...
	 */
	protected void updateObsProperty(String name, Object...values){
		this.getObsProperty(name).updateValues(values);
	}

	/**
	 * Blocks the execution of current operation until the condition specified
	 * by the guard is satisfied.
	 * 
	 * By calling await the execution of current atomic operation step is
	 * completed.
	 * 
	 * @param guardName
	 *            - name of the boolean method implementing the guard
	 * @param params
	 *            - parameters of the method
	 */
	protected void await(String guardName, Object... params) {
		ICartagoLoggerManager log = wsp.getLoggerManager();
		OpId id = thisOpId;
		try {
			commitObsStateChanges();
			String name = Artifact.getOpKey(guardName, params.length);
			Method guard = guardMap.get(name);
			guard.setAccessible(true);
			boolean guardOK = (Boolean) guard.invoke(this, params);
			while (!guardOK) {
				guards.await();
				guardOK = (Boolean) guard.invoke(this, params);
			}
			restoreOpExecContext(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException("Exception in await "
					+ guardName);
		}
	}

	/**
	 * Blocks the execution of current operation until the specified amount of
	 * time has passed.
	 * 
	 * By calling await the execution of current atomic operation step is
	 * completed.
	 * 
	 * @param dt
	 *            - amount of time in milliseconds
	 */
	protected void await_time(long dt) {
		OpId id = thisOpId;
		try {
			commitObsStateChanges();
			lock.unlock();
			Thread.sleep(dt);
			lock.lock();
			this.restoreOpExecContext(id);
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new IllegalArgumentException("Exception in await " + dt);
		}
	}

	/**
	 * 
	 * Blocks the execution of current operation until the specified blocking
	 * command has been executed.
	 * 
	 * By calling await the execution of current atomic operation step is
	 * completed.
	 * 
	 * @param cmd
	 *            - the command to be executed
	 */
	protected void await(IBlockingCmd cmd) {
		OpId id = thisOpId;
		try {
			commitObsStateChanges();
			lock.unlock();
			cmd.exec();
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new IllegalArgumentException("Exception in await " + cmd);
		} finally {
			lock.lock();
			restoreOpExecContext(id);
		}
	}

	/**
	 * 
	 * Start the execution of an internal operation.
	 * 
	 * The execution/success semantics of the new operation is completely
	 * independent from current operation.
	 * 
	 * @param op
	 *            - the operation to be executed
	 */
	protected void execInternalOp(String opName, Object... params) {
		try {
			wsp.doInternalOp(this.id, new Op(opName, params));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(
					"Error in executing internal op.");
		}
	}

	/**
	 * Execute a linked operation
	 * 
	 * @param outPortName
	 *            name of the out port
	 * @param opName
	 *            name of the operation to execute
	 * @param params
	 *            parameters of the operation
	 * @throws OperationException
	 */
	protected void execLinkedOp(String outPortName, String opName, Object... params) throws OperationException {
		OpId id = thisOpId;
		ArtifactOutPort port = outPortsMap.get(outPortName);
		if (port == null) {
			throw new OperationException("Wrong out port name.");
		} else if (port.getArtifactList().isEmpty()) {
			throw new OperationException("No artifact linked.");
		} else {
			//ArtifactId aid = port.getArtifactList().get(0);
			List<PendingOp> popList = new ArrayList<PendingOp>();
			for (ArtifactId aid: port.getArtifactList()){
				try {
					PendingOp pop = opCallback.createPendingOp();
					AgentId userId = this.getCurrentOpAgentId();
					Op op = new Op(opName, params);
					wsp.execInterArtifactOp(opCallback, pop.getActionId(),
									userId, this.getId(), aid, op,
									Integer.MAX_VALUE, null);
					popList.add(pop);
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new OperationException("execLinkedOp failed " + ex);
				}
			}
			try {
				this.commitObsStateChanges();
				lock.unlock();
				for (PendingOp pop: popList){
					pop.waitForCompletion();
					if (!pop.hasSucceeded()) {
						throw new OperationException("op failed.");
					}
				}
			} finally {
				lock.lock();
				this.restoreOpExecContext(id);
			}

		}
	}
	
	
	
	/**
	 * Check if an artifact is linked to a port
	 * 
	 * @param outPortName
	 * @return
	 */
	protected boolean isLinked(String outPortName) {
		ArtifactOutPort port = outPortsMap.get(outPortName);
		if (port == null) {
			return false;
		} else if (port.getArtifactList().isEmpty()){
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Execute a linked operation, given the artifact id
	 * 
	 * @param aid
	 *            artifact identifier
	 * @param opName
	 *            name of the operation to execute
	 * @param params
	 *            parameters of the operation
	 * @throws OperationException
	 */
	protected void execLinkedOp(ArtifactId aid, String opName, Object... params)
			throws OperationException {
		OpId id = thisOpId;
		try {
			PendingOp pop = opCallback.createPendingOp();
			AgentId userId = this.getCurrentOpAgentId();
			Op op = new Op(opName, params);
			wsp.execInterArtifactOp(opCallback, pop.getActionId(), userId,
							this.getId(), aid, op, Integer.MAX_VALUE, null);
			try {
				this.commitObsStateChanges();
				lock.unlock();
				pop.waitForCompletion();
			} finally {
				lock.lock();
				this.restoreOpExecContext(id);
			}
			if (!pop.hasSucceeded()) {
				throw new OperationException("op failed.");
			}
		} catch (Exception ex) {
			throw new OperationException("execLinkedOp failed " + ex);
		}
	}

	/**
	 * Create an artifact from another artifact.
	 * 
	 * @param name
	 *            name of the artifact
	 * @param type
	 *            template
	 * @param params
	 *            parameters - use ArtifactConfig.DEFAULT_CONFIG for default
	 *            configuration
	 * @return artifact id
	 * @throws OperationException
	 */
	protected ArtifactId makeArtifact(String name, String type, ArtifactConfig params) throws OperationException {
		try {
			return wsp.makeArtifact(this.getCurrentOpAgentId(), name, type, params);
		} catch (Exception ex) {
			throw new OperationException("makeArtifact failed: " + name + " " + type);
		}
	}
	

	/**
	 * Dispose an artifact
	 * 
	 * @param aid
	 *            artifact id
	 * @throws OperationException
	 */
	protected void dispose(ArtifactId aid) throws OperationException {
		try {
			wsp.disposeArtifact(this.getCurrentOpAgentId(),aid);
		} catch (Exception ex) {
			throw new OperationException("disposeArtifact failed: " + aid);
		}
	}

	/**
	 * Lookup an artifact
	 * 
	 * @param name
	 *            artifact name
	 * @return artifact id
	 * @throws OperationException
	 */
	protected ArtifactId lookupArtifact(String name) throws OperationException {
		try {
			return wsp.lookupArtifact(this.getCurrentOpAgentId(),name);
		} catch (Exception ex) {
			throw new OperationException("lookupArtifact failed: " + name);
		}
	}

	/**
	 * Delay the execution of next instruction of the specified amount of time
	 * 
	 * Note that this operation blocks the artifact access
	 * 
	 * @param ms
	 *            amount of time in milliseconds
	 */
	protected void delay(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception ex) {
		}
	}

	/**
	 * Log the information on standard output.
	 * 
	 */
	protected void log(String msg) {
		System.out.println("[" + this.getId().getName() + "] " + msg);
	}

	// meta

	/**
	 * Defining a new artifact operation
	 * 
	 * 
	 * @param op
	 *            operation
	 * @param guard
	 *            guard
	 */
	protected void defineOp(IArtifactOp op, IArtifactGuard guard) {
		String name = null;
		if (!op.isVarArgs()) {
			name = Artifact.getOpKey(op.getName(), op.getNumParameters());
		} else {
			name = Artifact.getOpKey(op.getName(), -1);
		}
		OpDescriptor opdesc = new OpDescriptor(name, op, guard,
				OpDescriptor.OpType.UI);
		// log("registering "+name);
		operationMap.put(name, opdesc);

	}
	
	
	/**
	 * Method automatically called when the artifact is 
	 * disposed. To be overridden by derived Artifact classes. 
	 * 
	 * Dual method with respect to init. 
	 */
	protected void dispose(){		
	}

	
	/* experimental: topology setting */
	
	protected void setupPosition(AbstractWorkspacePoint pos, double observabilityRadius){
		position = pos;
		this.observabilityRadius = observabilityRadius;
		try {
			wsp.notifyArtifactPositionOrRadiusChange(id);	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	protected void updatePosition(AbstractWorkspacePoint pos) {
		position = pos;
		try {
			this.wsp.notifyArtifactPositionOrRadiusChange(id);	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	protected void updateObservabilityRadius(double radius) {
		observabilityRadius = radius;
		try {
			this.wsp.notifyArtifactPositionOrRadiusChange(id);	
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	protected final AbstractWorkspacePoint getPosition(){
		return position;
	}
	
	protected final double getObservabilityRadius(){
		return observabilityRadius;
	}
	
	protected AgentId getCreatorId(){
		return creatorId;
	}
	
	
	/* Direct API interface for external use by ext threads */
	
	/**
	 * Begins an external use session of the artifact.
	 * 
	 * Method to be called by external threads (not agents) before
	 * starting calling methods on the artifact.
	 * 
	 */
	public void beginExternalSession(){
		try {
			lock.lock();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Ends an external use session.
	 * 
	 * Method to be called to close a session started by a thread
	 * to finalize the state.
	 * 
	 * @param success
	 */
	public void endExternalSession(boolean success){
		try {
			if (success){
				commitObsStateChanges();
			} else {
				obsPropertyMap.rollbackChanges();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			guards.signalAll();
			lock.unlock();
		}
	}
	
	
	// API for the adapter (that allows the kernel to access the artifact)
	
	private void linkTo(ArtifactId aid, String outPort) throws CartagoException {
		ArtifactOutPort port = null;
		synchronized (outPortsMap) {
			if (outPort == null) {
				Iterator<ArtifactOutPort> it = outPortsMap.values().iterator();
				if (it.hasNext()) {
					port = it.next();
				}
			} else {
				port = outPortsMap.get(outPort);
			}
			if (port != null) {
				port.addArtifact(aid);
			} else {
				throw new CartagoException("Invalid out port: " + outPort);
			}
		}
	}

	private List<ArtifactObsProperty> readProperties() {
		return obsPropertyMap.readAll();
	}

	private List<OpDescriptor> getOperations() throws CartagoException {
		try {
			List<OpDescriptor> list = new ArrayList<OpDescriptor>();
			synchronized (operationMap) {
				for (OpDescriptor op: operationMap.values()){
					list.add(op);
				}
				return list;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private List<OperationInfo>  getOpInExecution() {
		try {
			synchronized (opsInExecution) {
				List<OperationInfo> opInfo = new ArrayList<OperationInfo>();
				for (OpExecutionFrame op: opsInExecution) {
					int id = op.getOpId().getId();
					String name = op.getOpId().getOpName();
					opInfo.add(new OperationInfo(id, name));
				}
				return opInfo;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the adapter of the artifact
	 * 
	 * @return the adapter
	 */
	AbstractArtifactAdapter getAdapter() {
		return new ArtifactAdapter(this);
	}

	/**
	 * Gets a fresh identifier for a new operation execution
	 * 
	 */
	OpId getFreshId(String opName, AgentId ctxId) {
		int opid = opIds.getAndIncrement();
		OpId oid = new OpId(id, opName, opid, ctxId);
		return oid;
	}
	
	/**
	 * Gets the list of artifacts linked with this one
	 * 
	 */
	public List<ArtifactId> getLinkedArtifacts() {
		List<ArtifactId> linkedArtifacts = new ArrayList<ArtifactId>();
		Iterator it = outPortsMap.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry<String, ArtifactOutPort> pair = (Map.Entry<String, ArtifactOutPort>) it.next();
	    	linkedArtifacts.addAll(pair.getValue().getArtifactList());
	    }
		
		return linkedArtifacts;
	}
	
	/**
	 * Class representing the adapter used to interface the artifact to the
	 * environment
	 * 
	 */
	class ArtifactAdapter extends AbstractArtifactAdapter {

		public ArtifactAdapter(Artifact art) {
			super(art);
		}

		/**
		 * Read a property
		 */
		public ArtifactObsProperty readProperty(String propertyName)
				throws CartagoException {
			return artifact.getObsProperty(propertyName).getUserCopy();
		}

		/**
		 * Read properties
		 */
		public List<ArtifactObsProperty> readProperties() {
			return artifact.readProperties();
		}

		public void initArtifact(ArtifactConfig cfg) throws CartagoException {
			artifact.doInit(cfg);
		}

		/**
		 * Invoke an operation on the artifact
		 * 
		 */
		public void doOperation(OpExecutionFrame info) throws CartagoException {
			// log("doOperation:"+info.getOperation().getName()+" on "+this.artifact.getId()+"...");
			artifact.doOperation(info);
		}
		
		public AbstractWorkspacePoint getPosition(){
			return artifact.position;
		}
		
		public double getObservabilityRadius(){
			return observabilityRadius;
		}

		// meta ops

		public Manual getManual() {
			return artifact.manual;
		}

		public void linkTo(ArtifactId aid, String portName)
				throws CartagoException {
			artifact.linkTo(aid, portName);
		}

		public List<OpDescriptor> getOperations() throws CartagoException {
			return artifact.getOperations();
		}

		public List<OperationInfo>  getOpInExecution() {
			return artifact.getOpInExecution();
		}

		public boolean hasOperation(Op op) {
			String name = Artifact.getOpKey(op.getName(),
					op.getParamValues().length);
			return artifact.operationMap.containsKey(name);
		}		
		
		
	}

	public abstract class AbstractAsyncProcess extends Thread implements IBlockingCmd {
		
		public final void exec(){
			try {
				start();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
		protected final void signal(String type, Object... objs){
			try {
				lock.lock();
				commitObsStateChangesAndSignal(null, new Tuple(type, objs));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

		protected final void signal(AgentId target,String type, Object... objs){
			try {
				lock.lock();
				commitObsStateChangesAndSignal(target, new Tuple(type, objs));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

		
	}
}
