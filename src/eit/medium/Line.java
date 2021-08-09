package eit.medium;

import java.util.NoSuchElementException;

/**
 * With this interface, the class must implement the following methods<br>
 * these methods are necessary for packing in the queue and out.<br>
 * <li>transmit</li>
 * <li>receive</li>
 * <li>hasData</li>
 * @param <E> this parameter will adapt to the class and return the 
 * required data type. which we pack in the queue
 */

public interface Line<E> {
	/** this class put the datas in cable
	 * @param element which is packed in queue 
	 * @return true if is successful otherwise false.
	 */
	
	 boolean transmit(E element);
	
	/**this method is used to fetch data from the cable.<br>
	 * @return the datas that have been fetched from the queue.
	 * @throws NoSuchElementException if there are no datas in cable,
	 * NoSuchElementException is thrown.
	 */
	
	 E receive() throws NoSuchElementException;
	
	/** checks whether there are any data in the queue
	 * @return true if queue has any data otherwise false.
	 */
	
	 boolean hasData();

}