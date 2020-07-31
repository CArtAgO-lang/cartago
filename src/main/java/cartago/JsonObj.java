/**
 * CArtAgO - DISI, University of Bologna
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

import java.time.Instant;


import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Wrapping API for Json Object.
 * 
 * @TODO to be completed
 */
public class JsonObj implements java.io.Serializable {

	private JsonObject obj;
	
	public JsonObj() {
		obj = new JsonObject();
	}
	
	public double getDouble(String key) {
		return obj.getDouble(key);
	}

	public void putDouble(String key, double value) {
		obj.put(key, value);
	}

	public boolean getBoolean(String key) {
		return obj.getBoolean(key);
	}

	public void putBoolean(String key, boolean value) {
		obj.put(key, value);
	}

	public byte[] getBinary(String key) {
		return obj.getBinary(key);
	}

	public void putBinary(String key, byte[] value) {
		obj.put(key, value);
	}
 
	public float getFloat(String key) {
		return obj.getFloat(key);
	}

	public void putFloat(String key, float value) {
		obj.put(key, value);
	}

	public Instant getInstant(String key) {
		return obj.getInstant(key);
	}
	
	public void putInstant(String key, Instant value) {
		obj.put(key, value);
	}

	public int getInteger(String key) {
		return obj.getInteger(key);
	}

	public void putInteger(String key, int value) {
		obj.put(key, value);
	}
	
	public JsonArray getJsonArray(String key) {
		return obj.getJsonArray(key);
	}

	public void putJsonArray(String key, JsonArray value) {
		obj.put(key, value);
	}

	public JsonObject getJsonObject(String key) {
		return obj.getJsonObject(key);
	}
	
	public void putJsonObject(String key, JsonObject obj) {
		obj.put(key, obj);
	}

	public long getLong(String key) {
		return obj.getLong(key);
	}

	public void put(String key, long value) {
		obj.put(key, value);
	}
	
	public String getString(String key) {
		return obj.getString(key);
	}

	public void putString(String key, String value) {
		obj.put(key, value);
	}

	public JsonObject getImplementation() {
		return obj;
	}
	
	public String toString() {
		return obj.encodePrettily();
	}
}
