package ps8;

import java.util.Iterator;
import java.util.List;

public class ResultPage<T> implements Iterable<T> {

	private List<T> results;
	private int pageNumber;
	private boolean isBeginning;
	private boolean isEnd;
	
	public ResultPage(List<T> results, int pageNumber, boolean isBeginning,
			boolean isEnd) {
		super();
		this.results = results;
		this.pageNumber = pageNumber;
		this.isBeginning = isBeginning;
		this.isEnd = isEnd;
	}

	@Override
	public Iterator<T> iterator() {
		return results.iterator();
	}
	
	public List<T> getResults() {
		return results;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public boolean getIsBeginning() {
		return isBeginning;
	}

	public boolean getIsEnd() {
		return isEnd;
	}

}
