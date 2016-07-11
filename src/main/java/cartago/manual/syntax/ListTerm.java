package cartago.manual.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a list node as in prolog .(t1,.(t2,.(t3,.))).
 * 
 * Each nth-ListTerm has both a term and the next ListTerm.
 * The last ListTem is an empty ListTerm (term==null).
 * In lists terms with a tail ([a|X]), next is the Tail (next==X, term==a).
 *
 * @navassoc - element - Term
 * @navassoc - next - ListTerm
 *
 * @author Jomi
 */
public class ListTerm extends Structure implements java.util.List<Term> {
    
    private static final long serialVersionUID = 1L;

    public static final String LIST_FUNCTOR = ".";
    private Term term;
    private Term next;
    
    public ListTerm() {
        super(LIST_FUNCTOR, 0);
    }
    
    private ListTerm(Term t, Term n) {
        super(LIST_FUNCTOR, 0);
        term = t;
        next = n;
    }

    /** make a hard copy of the terms */
    public ListTerm clone() {
        ListTerm t = new ListTerm();
        if (term != null) t.term = this.term.clone();
        if (next != null) t.next = this.next.clone();
        return t;
    }
    
    public ListTerm cloneLT() {
        return clone();
    }
    
    @Override
    public boolean equals(Object t) {
        if (t == null) return false;
        if (t == this) return true;

        if (t instanceof Term &&  ((Term)t).isVar() ) return false; // unground var is not equals a list
        if (t instanceof ListTerm) {
            ListTerm tAsList = (ListTerm)t;
            if (term == null && tAsList.getTerm() != null) return false;
            if (term != null && !term.equals(tAsList.getTerm())) return false;
            if (next == null && tAsList.getNext() != null) return false;
            if (next != null) return next.equals(tAsList.getNext());
            return true;
        } 
        return false;
    }
    
    @Override
    public int calcHashCode() {
        int code = 37;
        if (term != null) code += term.hashCode();
        if (next != null) code += next.hashCode();
        return code;
    }
    
    public void setTerm(Term t) {
        term = t;
    }
    
    /** gets the term of this ListTerm */
    public Term getTerm() {
        return term;
    }
    
    public void setNext(Term l) {
        next = l;
    }
    
    public ListTerm getNext() {
        if (next instanceof ListTerm)
            return (ListTerm)next;
        else
            return null;
    }
    
    // for unifier compatibility
    @Override
    public int getArity() {
        if (isEmpty()) {
            return 0;
        } else {
            return 2; // term and next
        }
    }
    
    // for unifier compatibility
    @Override
    public Term getTerm(int i) {
        if (i == 0) return term;
        if (i == 1) return next;
        return null;
    }

    // for unifier compatibility
    @Override
    public void setTerm(int i, Term t) {
        if (i == 0) term = t;
        if (i == 1) next = t;
    }
    

    public int size() {
        if (isEmpty()) {
            return 0;
        } else if (isTail()) {
            return 1;
        } else {
            return getNext().size() + 1;
        }
    }
    
    @Override
    public boolean isAtom() {
        return false;
    }

    @Override
    public boolean isList() {
        return true;
    }

    public boolean isEmpty() {
        return term == null;
    }
    public boolean isEnd() {
        return isEmpty() || isTail();
    }

    public boolean isGround() {
        if (isEmpty()) {
            return true;
        } else if (isTail()) {
            return false;
        } else if (term != null && term.isGround()) {
            return getNext().isGround();
        }
        return false;
    }

    public boolean isTail() {
        return next != null && next.isVar();
    }
    
    /** returns this ListTerm's tail element in case the List has the Tail, otherwise, returns null */
    public VarTerm getTail() {
        if (isTail()) {
            return (VarTerm)next;
        } else if (next != null) {
            return getNext().getTail();
        } else {
            return null;
        }
    }
    
    /** set the tail of this list */
    public void setTail(VarTerm v) {
        if (getNext().isEmpty())
            next = v;
        else
            getNext().setTail(v);
    }
    
    /** get the last ListTerm of this List */
    public ListTerm getLast() {
        if (isEnd()) {
            return this;
        } else if (next != null) {
            return getNext().getLast();
        } 
        return null; // !!! no last!!!!
    }
    
    public ListTerm getPenultimate() {
        if (getNext() == null)
            return null;
        if (isTail())
            return this;
        if (getNext().isEnd() && !getNext().isTail())
            return this;
        return getNext().getPenultimate();
    }
    
    public Term removeLast() {
        ListTerm p = getPenultimate();
        if (p != null) {
            Term b = p.getTerm();
            p.setTerm(null);
            p.setNext(null);
            return b;
        } else {
            return null;
        }
    }
    
    /** 
     * Adds a term in the end of the list
     * @return the ListTerm where the term was added (i.e. the last ListTerm of the list)
     */
    public ListTerm append(Term t) {
        if (isEmpty()) {
            term = t;
            next = new ListTerm();
            return this;
        } else if (isTail()) {
            // What to do?
            return null;
        } else {
            return getNext().append(t);
        }
    }

    
    /** 
     * Adds a list in the end of this list.
     * This method do not clone <i>lt</i>.
     * @return the last ListTerm of the new list
     */
    public ListTerm concat(ListTerm lt) {
        if (isEmpty()) {
            setValuesFrom(lt);
        } else if (((ListTerm)next).isEmpty() ) {
            next = lt;
        } else {
            ((ListTerm)next).concat(lt);
        }
        return lt.getLast();
    }
    
    /**
     * Creates a new (cloned) list with the same elements of this list, but in the reversed order.
     * The Tail remains the Tail: reverse([a,b|T]) = [b,a|T].
     */
    public ListTerm reverse() {
        return reverse_internal(new ListTerm());
    }
    private ListTerm reverse_internal(ListTerm r) {
        if (isEmpty()) {
            return r;
        } else if (isTail()) {
            r = new ListTerm(term.clone(), r);
            r.setTail((VarTerm)next.clone());
            return r;
        } else {
            return ((ListTerm)next).reverse_internal( new ListTerm(term.clone(), r) );
        }
    }

    /** returns a new (cloned) list representing the set resulting of the union of this list and lt. */
    public ListTerm union(ListTerm lt) {
        Set<Term> set = new TreeSet<Term>();
        set.addAll(lt);
        set.addAll(this);
        return setToList(set);
    }

    /** returns a new (cloned) list representing the set resulting of the intersection of this list and lt. */
    public ListTerm intersection(ListTerm lt) {
        Set<Term> set = new TreeSet<Term>();
        set.addAll(lt);
        set.retainAll(this);
        return setToList(set);
    }
    
    /** returns a new (cloned) list representing the set resulting of the difference of this list and lt. */
    public ListTerm difference(ListTerm lt) {
        Set<Term> set = new TreeSet<Term>();
        set.addAll(this);
        set.removeAll(lt);
        return setToList(set);
    }

    // copy the set to a new list
    private ListTerm setToList(Set<Term> set) {
        ListTerm result = new ListTerm();
        ListTerm tail = result;
        for (Term t: set)
            tail = tail.append(t.clone());
        return result;
    }

    /** 
     * gives an iterator that includes the final empty list or tail, 
     * for [a,b,c] returns [a,b,c]; [b,c]; [c]; and [].
     * for [a,b|T] returns [a,b|T]; [b|T]; [b|T]; and T.
     */
    public Iterator<ListTerm> listTermIterator() {
        return new ListTermIterator<ListTerm>(this) {
            public ListTerm next() {
                moveNext();
                return current;
            }
        };
    }
    

    /** 
     * returns an iterator where each element is a Term of this list,
     * the tail of the list is not considered. 
     * for [a,b,c] returns 'a', 'b', and 'c'.
     * for [a,b|T] returns 'a' and 'b'.
     */  
    public Iterator<Term> iterator() {
        return new ListTermIterator<Term>(this) {
            public boolean hasNext() {
                return nextLT != null && !nextLT.isEmpty() && nextLT.isList(); 
            }
            public Term next() {
                moveNext();
                return current.getTerm();
            }
        };
    }
        
    private abstract class ListTermIterator<T> implements Iterator<T> {
        ListTerm nextLT;
        ListTerm current = null;
        public ListTermIterator(ListTerm lt) {
            nextLT = lt;
        }
        public boolean hasNext() {
            return nextLT != null; 
        }
        public void moveNext() {
            current = nextLT;
            nextLT  = nextLT.getNext();
        }
        public void remove() {
            if (current != null && nextLT != null) {
                current.setTerm(nextLT.getTerm());
                current.setNext(nextLT.getNext());
                nextLT = current;
            }
        }
    }


    /** 
     * Returns this ListTerm as a Java List (implemented by ArrayList). 
     * Note: the tail of the list, if any, is not included!
     */
    public List<Term> getAsList() {
        List<Term> l = new ArrayList<Term>();
        for (Term t: this)
            l.add(t);
        return l;
    }

    
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        ListTerm l = this;
        while (!l.isEmpty()) {
            s.append(l.getTerm());
            if (l.isTail()) {
                s.append('|');
                s.append(l.getNext());
                break;
            }
            l = l.getNext();
            if (!l.isEmpty())
                s.append(',');
        }
        s.append(']');
        return s.toString();
    }

    //
    // Java List interface methods
    //
    
    public void add(int index, Term o) {
        if (index == 0) {
            ListTerm n = new ListTerm(term,next);
            this.term = o;
            this.next = n;
        } else if (index > 0 && getNext() != null) {
            getNext().add(index-1,o);
        }
    }
    public boolean add(Term o) {
        return append(o) != null;
    }

    @SuppressWarnings("unchecked")
    public boolean addAll(Collection c) {
        if (c == null) return false;
        ListTerm lt = this; // where to add
        Iterator<Term> i = c.iterator();
        while (i.hasNext()) {
            lt = lt.append(i.next());
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public boolean addAll(int index, Collection c) {
        Iterator<Term> i = c.iterator();
        int p = index;
        while (i.hasNext()) {
            add(p, i.next()); 
            p++;
        }
        return true;
    }
    public void clear() {
        term = null;
        next = null;
    }

    public boolean contains(Object o) {
        if (term != null && term.equals(o)) {
            return true;
        } else if (getNext() != null) {
            return getNext().contains(o);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean containsAll(Collection c) {
        boolean r = true;
        Iterator<Term> i = c.iterator();
        while (i.hasNext() && r) {
            r = r && contains(i.next()); 
        }
        return r;
    }

    public Term get(int index) {
        if (index == 0) {
            return this.term;
        } else if (getNext() != null) {
            return getNext().get(index-1);
        }
        return null;
    }

    public int indexOf(Object o) {
        if (this.term.equals(o)) {
            return 0;
        } else if (getNext() != null) {
            int n = getNext().indexOf(o);
            if (n >= 0) {
                return n+1;
            }
        }
        return -1;
    }
    public int lastIndexOf(Object arg0) {
        return getAsList().lastIndexOf(arg0);
    }

    public ListIterator<Term> listIterator() {
        return listIterator(0);
    }
    
    // TODO: do not base this implementation on get (that is O(n))
    // conversely, implement all other methods of List based on this iterator
    // (see AbstractSequentialList)
    // merge code of ListTermIterator here and use always the same iterator
    public ListIterator<Term> listIterator(final int startIndex) {
        final ListTerm list = this;
        return new ListIterator<Term>() {
            int pos = startIndex;
            int last = -1;
            int size = size();

            public void add(Term o) {
                list.add(last,o);
            }
            public boolean hasNext() {
                return pos < size;
            }
            public boolean hasPrevious() {
                return pos > startIndex;
            }
            public Term next() {
                last = pos;
                pos++;
                return get(last);
            }
            public int nextIndex() {
                return pos+1;
            }
            public Term previous() {
                last = pos;
                pos--;
                return get(last);
            }
            public int previousIndex() {
                return pos-1;
            }
            public void remove() {
                list.remove(last);
            }
            public void set(Term o) {
                remove();
                add(o);
            }            
        };
    }

    protected void setValuesFrom(ListTerm lt) {
        this.term = lt.getTerm();
        this.next = lt.getNext();
    }
    
    public Term remove(int index) {
        if (index == 0) {
            Term bt = this.term;
            if (getNext() != null) {
                setValuesFrom(getNext());
            } else {
                clear();
            }
            return bt;
        } else if (getNext() != null) {
            return getNext().remove(index-1);
        }
        return null;
    }

    public boolean remove(Object o) {
        if (term != null && term.equals(o)) {
            if (getNext() != null) {
                setValuesFrom(getNext());
            } else {
                clear();
            }
            return true;
        } else if (getNext() != null) {
            return getNext().remove(o);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean removeAll(Collection c) {
        boolean r = true;
        Iterator i = c.iterator();
        while (i.hasNext() && r) {
            r = r && remove(i.next()); 
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    public boolean retainAll(Collection c) {
        boolean r = true;
        Iterator i = iterator();
        while (i.hasNext()) {
            Term t = (Term)i.next();
            if (!c.contains(t)) {
                r = r && remove(t);
            }
        }
        return r;
    }

    public Term set(int index, Term t) {
        if (index == 0) {
            this.term = (Term)t;
            return t;
        } else if (getNext() != null) {
            return getNext().set(index-1, t);
        }
        return null;
    }

    public List<Term> subList(int arg0, int arg1) {
        return getAsList().subList(arg0, arg1);
    }

    public Object[] toArray() {
        return toArray(new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        final int s = size();
        if (a.length < s)
            a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), s);

        int i = 0;
        for (Term t: this) {
            a[i++] = (T)t;
        }
        if (a.length > s)
            a[s] = null;

        return a;
    }
    
 }
