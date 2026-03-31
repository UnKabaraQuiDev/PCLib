package lu.kbra.pclib.db.loader;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class PagedEnumeration<B> implements Enumeration<B> {

	protected final int pageSize;
	protected final int total;
	protected int currentPage;
	protected Iterator<B> currentPageData;

	public PagedEnumeration(final int pageSize, final int total) {
		this.pageSize = pageSize;
		this.total = total;
		this.currentPage = 0;
	}

	@Override
	public boolean hasMoreElements() {
		if (this.currentPageData == null) {
			this.currentPageData = this.fetchPage(0, this.pageSize);
		}
		if (this.currentPageData.hasNext()) {
			return true;
		}
		final int nextOffset = (this.currentPage + 1) * this.pageSize;
		if (nextOffset >= this.total) {
			return false;
		}
		this.currentPage++;
		this.currentPageData = this.fetchPage(this.currentPage, this.pageSize);
		return this.currentPageData.hasNext();
	}

	@Override
	public B nextElement() {
		return this.currentPageData.next();
	}

	public void forEachRemaining(final Consumer<B> consumer) {
		while (this.hasMoreElements()) {
			consumer.accept(this.nextElement());
		}
	}

	protected abstract Iterator<B> fetchPage(int page, int size);

	// not provided in 1.8
	public Iterator<B> asIterator() {
		return new Iterator<B>() {
			@Override
			public boolean hasNext() {
				return PagedEnumeration.this.hasMoreElements();
			}

			@Override
			public B next() {
				return PagedEnumeration.this.nextElement();
			}
		};
	}

}
