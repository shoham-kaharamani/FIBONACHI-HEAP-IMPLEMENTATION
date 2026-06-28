
//-----------Details------------//
//username - ronbenhaim\kaharamani
//id1      - 208610212
//name1    - ronbenhaim
//id2      - 322549478
//name2    - kaharamani

/**
 * FibonacciHeap
  * An implementation of Fibonacci heap over positive integers.
  */
public class FibonacciHeap
{
	public HeapNode min;
	public int size;
	public int numTrees;
	public int cutLimit;
	public int heapCuts;
	public int heapLinks;
	
	/**
	 *
	 * Constructor to initialize an empty heap.
	 * Pre: c >= 2.
	 * Complexity: O(1) W.C, A.C
	 */
	public FibonacciHeap(int c)
	{
		this.min = null;
		this.cutLimit = c;
	}

	/**
	 * Pre: key > 0
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public HeapNode insert(int key, String info) 
	{    
		HeapNode newNode = new HeapNode(key, info);
		addToRootList(newNode);
		size++;
		return newNode;
	}

	/**
	 * Return the minimal HeapNode, null if empty.
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public HeapNode findMin()
	{
		return min;
	}

	/**
	 * Delete the minimal item.
	 * Return the number of links.
	 *  Complexity: O(n) W.C, O(log(n)) A.C
	 */
	public int deleteMin()
	{
		//1)Delete the minimum
		//2)Create a linked list of the roots
		//3)Call a consolidating function: (input: array list ordered by the rank of each root)
		//   - uses a function that takes two roots of the same rank and generates an increased rank by 1
		//4)Scan the root list and update the pointer to the minimum node
		int linkCounter;
		if (numTrees == 0) { //Empty heap --> do nothing
			return 0;
		}
		if (numTrees == 1) { //Only 1 tree in the heap
			if (min.rank==0){ //Tree has no children
				min.next = null;
				min.prev = null;
				min = null;
				size = 0;
				numTrees = 0;
				return 0;
			}
			else{ //Tree has at least 1 child
				HeapNode child = min.child;
				child.parent = null;
				min.child = null;
				min.next = min.prev = null;
				linkCounter = consolidate(child); //Root List is the list of the minimum's children
			}
		}
		else{ //More than 1 tree in the heap
			if (min.rank==0){//Minimal root has no children
				HeapNode tempMin = min.next;
				HeapNode tempMinPrev = min.prev;
				tempMinPrev.next = tempMin;
				tempMin.prev = tempMinPrev;
				min.next = null;
				min.prev = null;
				min = tempMin;
				linkCounter = consolidate(tempMin); //Root List is the same list but without the minimum
			}
			else{ //Minimal root has at least 1 child
				HeapNode tempMin = min.next;
				HeapNode tempMinPrev = min.prev;
				tempMinPrev.next = tempMin;
				tempMin.prev = tempMinPrev;
				min.next = null;
				min.prev = null;
				mergeLists(tempMin,min.child); //Merging the children list into the root list
				min = tempMin;
				linkCounter = consolidate(min); //Root List is the merged list
			}
		}
		size--;
		heapLinks += linkCounter;
		return linkCounter;
	}

	/**
	 * Performing the consolidating/Successive linking process of Fibonacci Heaps
	 * Complexity: O(n) W.C, O(log(n)) A.C
	 */
	public int consolidate(HeapNode root) {
		java.util.ArrayList<Object> buckets = new java.util.ArrayList<>();
		int linkCounter = 0;
		//to-buckets()
		HeapNode x = root;
		HeapNode last = x.prev;
		HeapNode y;
		boolean stopCondition = false;
		while(!stopCondition){
			x.isRoot = true;
			x.nodeCuts = 0;
			x.parent = null;
			y = x;
			x = x.next;
			if (y == last){
				stopCondition = true; //Stops the loops when reaching the last tree root
			}
			ensureSize(buckets,y.rank+1);
			while (buckets.get(y.rank) != null) {
				ensureSize(buckets,y.rank+1);
				y = link(y,(HeapNode) buckets.get(y.rank));
				buckets.set(y.rank-1, null);
				linkCounter++;
			}
			ensureSize(buckets,y.rank+1);
			buckets.set(y.rank, y);
		}
		//from-buckets()
		int treeCount = 0;
		x = null;
		for (int i = 0; i < buckets.size(); i++) {
			if (buckets.get(i) != null) {
				treeCount++;
				if (x == null) {
					x = (HeapNode) buckets.get(i);
					x.next = x;
					x.prev = x;
				}
				else{
					y = (HeapNode) buckets.get(i);
					last = x.prev;
					last.next = y;
					x.prev = y;
					y.prev = last;
					y.next = x;
					if (y.key<x.key)
						x = y;
				}
			}
		}
		min = x;
		numTrees = treeCount;
		return linkCounter;
	}


	/**
	 * Pads the list with nulls, to ensure the size of the list is compatible to our requirements
	 * Complexity: O(n) W.C, O(1) A.C (for inserting to "independent" re-sizing list)
	 */
	public static void ensureSize(java.util.ArrayList<Object> buckets, int size) {
		while(buckets.size() <= size) {
			buckets.add(null);
		}
	}


	/**
	 * Links two trees of the same degree as part of the consolidating process
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public HeapNode link(HeapNode x, HeapNode y) {
		boolean isLastNode = false;

		//Determine which node should be the parent and which should be the child
		HeapNode root = x;
		HeapNode newChild = y;
		HeapNode child;
		HeapNode lastChild;
		if (y.key<x.key) {
			root = y;
			newChild = x;
		}
		newChild.isRoot = false;
		newChild.nodeCuts = 0;

		//Remove child from the roots list
		newChild.prev.next = newChild.next;
		newChild.next.prev = newChild.prev;

		//Parent's rank is > 0 - has children
		if (root.rank > 0) {
			child = root.child;
			lastChild = child.prev;
			newChild.next = child;
			newChild.prev = lastChild;
			lastChild.next = newChild;
			child.prev = newChild;
			newChild.parent = root;
		}
		//Parent's rank is 0 = has no children
		else {
			root.child = newChild;
			newChild.parent = root;
			newChild.next = newChild;
			newChild.prev = newChild;
		}
		root.rank++;
		return root;
	}


	/**
	 * Pre: 0<diff<x.key
	 * Decrease the key of x by diff and fix the heap.
	 * Return the number of cuts.
	 * Complexity: O(n) W.C, O(1) A.C
	 */
	public int decreaseKey(HeapNode x, int diff) 
	{    
		int cutsCount = 0;
		x.key -= diff;
		HeapNode parent = x.parent;
		if ((!x.isRoot)&&(x.key < parent.key)){
			do{
				parent = x.parent;
				trim(x);
				addToRootList(x);
				cutsCount++;
				x=parent;
			} while ((!x.isRoot)&&(x.nodeCuts >= cutLimit)); //Checking for "marked" nodes
		}
		if ((x.isRoot)&&(x.key<min.key)){
			min = x;
		}
		heapCuts += cutsCount;
		return cutsCount;
	}


	/**
	 * Delete the x from the heap.
	 * Return the number of links.
	 * Complexity: O(n) W.C, O(log(n)) A.C
	 */
	public int delete(HeapNode x) 
	{    
		int linksCount;
		decreaseKey(x, x.key);		//Forcing the node to be the new minimum
		linksCount = deleteMin();	//Deleting the node using delete min method
		return linksCount;
	}


	/**
	 * Return the total number of links.
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public int totalLinks()
	{
		return heapLinks;
	}


	/**
	 * Return the total number of cuts.
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public int totalCuts()
	{
		return heapCuts;
	}


	/**
	 * Meld the heap with heap2
	 * Updates the attributes according to the history of both heaps
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public void meld(FibonacciHeap heap2)
	{
		this.size += heap2.size;
		this.numTrees += heap2.numTrees;
		this.heapCuts += heap2.heapCuts;
		this.heapLinks += heap2.heapLinks;
		if ((heap2.size != 0)&&(this.size == 0)){
				this.min = heap2.min;
		}
		else if ((heap2.size != 0)&&(this.size != 0)){
			mergeLists(min,heap2.min);
			this.min = heap2.min.key < this.min.key ? heap2.min : this.min;
		}
	}


	/**
	 * Merges 2 doubly linked lists
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public void mergeLists(HeapNode h1, HeapNode h2)
	{
		HeapNode last1 = h1.prev;
		HeapNode last2 = h2.prev;
		h1.prev = last2;
		last2.next = h1;
		last1.next = h2;
		h2.prev = last1;
	}


	/**
	 * Return the number of elements in the heap
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public int size()
	{
		return size;
	}


	/**
	 * Return the number of trees in the heap.
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public int numTrees()
	{
		return numTrees;
	}

	/**
	 * get a node (new node/after trimming) and inserts it to the list of root
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public void addToRootList(HeapNode node) {
		if (numTrees == 0) {
			min = node;
			node.next = node;
			node.prev = node;
		}
		else {
				HeapNode first = min;
				HeapNode last = min.prev;
				last.next = node;
				first.prev = node;
				node.prev = last;
				node.next = first;
			}
			if (node.key < min.key) {
				min = node;
			}
		numTrees++;
	}


	/**
	 * Pre: node isn't a root (parent != null)
	 * Trims a node from its parent and organizing the node lists as required
	 * Complexity: O(1) W.C, O(1) A.C
	 */
	public void trim(HeapNode node) {
		HeapNode parent = node.parent;
		if (parent.rank == 1) { // the parent has only 1 child
			parent.child = null;
			node.parent = null;
		}
		else{ //the node has "brothers"
			if (parent.child == node) {
				parent.child = node.next;
			}
			node.parent = null;
			node.next.prev = node.prev;
			node.prev.next = node.next;
			node.next = node;
			node.prev = node;
		}
		parent.rank--;
		if (!parent.isRoot){
			parent.nodeCuts++;
		}
		node.isRoot = true;
		node.nodeCuts = 0;
	}


	/**
	 * Class implementing a node in a Fibonacci Heap.
	 */
	public static class HeapNode{
		public int key;
		public String info;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public int rank;
		public int nodeCuts;
		public boolean isRoot;

		/**
		 * Constructor for HeapNode
		 * Complexity: O(1) W.C, O(1) A.C
		 */
		public HeapNode(int key, String info)
		{
			this.key = key;
			this.info = info;
			this.child = null;
			this.next = this;
			this.prev = this;
			this.parent = null;
			this.rank = 0;
			this.nodeCuts = 0;
			this.isRoot = true;
		}
	}
}
