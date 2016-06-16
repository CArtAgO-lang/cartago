package c4jexamples;

import cartago.*;

import java.net.*;

public class Port extends Artifact {

	DatagramSocket socket;
	ReadCmd cmd;
	boolean receiving;

	@OPERATION
	void init(int port) throws Exception {
		socket = new DatagramSocket(port);
		cmd = new ReadCmd();
		receiving = false;
	}

	@OPERATION
	void sendMsg(String msg, String fullAddress) {
		try {
			int index = fullAddress.indexOf(':');
			InetAddress address = InetAddress.getByName(fullAddress.substring(
					0, index));
			int port = Integer.parseInt(fullAddress.substring(index + 1));
			socket.send(new DatagramPacket(msg.getBytes(),
					msg.getBytes().length, address, port));
		} catch (Exception ex) {
			this.failed(ex.toString());
		}
	}

	@OPERATION
	void receiveMsg(OpFeedbackParam<String> msg, OpFeedbackParam<String> sender) {
		await(cmd);
		msg.set(cmd.getMsg());
		sender.set(cmd.getSender());
	}

	@OPERATION
	void startReceiving() {
		receiving = true;
		execInternalOp("receiving");
	}

	@INTERNAL_OPERATION
	void receiving() {
		while (true) {
			await(cmd);
			signal("new_msg", cmd.getMsg(), cmd.getSender());
		}
	}

	@OPERATION
	void stopReceiving() {
		receiving = false;
	}

	class ReadCmd implements IBlockingCmd {

		private String msg;
		private String sender;
		private DatagramPacket packet;

		public ReadCmd() {
			packet = new DatagramPacket(new byte[1024], 1024);
		}

		public void exec() {
			try {
				socket.receive(packet);
				byte[] info = packet.getData();
				msg = new String(info);
				sender = packet.getAddress().toString();
			} catch (Exception ex) {
			}
		}

		public String getMsg() {
			return msg;
		}

		public String getSender() {
			return sender;
		}
	}
}
