package lu.kbra.pclib.db.loader;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class PagedEnumeration<B> implements Enumeration<B> {

	protected final int pageSize;
	protected final int total;
	protected int currentPage;
	protected Iterator<B> currentPageData;

	public PagedEnumeration(int pageSize, int total) {
		this.pageSize = pageSize;
		this.total = total;
		this.currentPage = 0;
	}

	@Override
	public boolean hasMoreElements() {
		if (currentPageData == null) {
			currentPageData = fetchPage(0, pageSize);
		}
		if (currentPageData.hasNext()) {
			return true;
		}
		final int nextOffset = (currentPage + 1) * pageSize;
		if (nextOffset >= total) {
			return false;
		}
		currentPage++;
		currentPageData = fetchPage(currentPage, pageSize);
		return currentPageData.hasNext();
	}

	@Override
	public B nextElement() {
		return currentPageData.next();
	}

	public void forEachRemaining(Consumer<B> consumer) {
		while (hasMoreElements()) {
			consumer.accept(nextElement());
		}
	}

	protected abstract Iterator<B> fetchPage(int page, int size);

	// not provided in 1.8
	public Iterator<B> asIterator() {
		return new Iterator<B>() {
			@Override
			public boolean hasNext() {
				return hasMoreElements();
			}

			@Override
			public B next() {
				return nextElement();
			}
		};
	}

}
