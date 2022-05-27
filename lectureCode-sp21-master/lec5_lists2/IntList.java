package lists2;

public class IntList {
	public int first;
	public IntList rest;

	public IntList(int f, IntList r) {
		first = f;
		rest = r;
	}

	/**  if 2 numbers in a row are the same, we add them together and make one large node */
	public void addAdjacent() {
		if ( rest == null) {
			return;
		} else {
			if (first == rest.first) {
				first *= first;
				rest = rest.rest;
				rest.addAdjacent();
			}
		}
	}



	/** Return the size of the list using... recursion! */
	public int size() {
		if (rest == null) {
			return 1;
		}
		return 1 + this.rest.size();
	}

	/** Return the size of the list using no recursion! */
	public int iterativeSize() {
		IntList p = this;
		int totalSize = 0;
		while (p != null) {
			totalSize += 1;
			p = p.rest;
		}
		return totalSize;
	}

	/** Returns the ith item of this IntList. */
	public int get(int i) {
		if (i == 0) {
			return first;
		}
		return rest.get(i - 1);
	}

	public static void main(String[] args) {
		IntList L = new IntList(5, null);
		L = new IntList(5, L);
		L = new IntList(5, L);
		L.addAdjacent();
		System.out.println(L);
	}
} 