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

/**
 * A Bounded buffer with basic synchronization support
 * 
 * @author aricci
 *
 */
class BoundedBuffer {
    private int in; // points to the next free position
    private int out; // points to the next full position
    private int count;
    private Object[] buffer;
    
    public BoundedBuffer(int size){
        in = 0;
        out = 0;
        count = 0;
        buffer = new Object[size];
    }
    
    public synchronized void insert(Object item) throws InterruptedException {
        while (isFull()){
            wait();
        }
        count++;
        buffer[in] = item;
        in = (in + 1) % buffer.length;
        notifyAll();
    }
    
    public synchronized Object remove() throws InterruptedException {
        while (isEmpty()){
            wait();
        }
        Object item = buffer[out];
        out = (out + 1) % buffer.length;
        count--;
        notifyAll();
        return item;
    }
    
    private boolean isEmpty(){
        return count == 0;
    }
    
    private boolean isFull(){
        return count == buffer.length;
    }

}
