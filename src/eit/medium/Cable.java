package eit.medium;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**In this class, the data are stored in the order in which they are
 * stored and are packed in with the transmit functions and received with receive.<br>
 * @author Hamed Nakhei
 * @param <E> is the data type which we put into the cable.
 */

public class Cable<E> implements Line<E> {

	private List<E> data = new ArrayList<E>();

	@Override
	public boolean transmit(E element) {
		data.add(element);
		return true;
	}

	@Override
	public E receive() throws NoSuchElementException {
		if (this.hasData() == false) {
			throw new NoSuchElementException();
		}
		E element = data.get(0);
		data.remove(0);
		return element;

	}

	@Override
	public boolean hasData() {
		if (data.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

}
