package jaca;

import java.io.Serializable;

/** 
 * This interface is implemented by objects that can have a representation
 * that follows the Prolog syntax
 */
public interface ToProlog extends Serializable {
    public String getAsPrologStr();
}
