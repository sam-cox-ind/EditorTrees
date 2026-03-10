package editortrees;

//import java.util.ArrayList;
//import editortrees.EditTree.NodeInfo;

/**
 * A node in a height-balanced binary tree with rank. Except for the NULL_NODE,
 * one node cannot belong to two different trees.
 * 
 * @author <<You>>
 */
public class Node {

	enum Code {
		SAME, LEFT, RIGHT;

		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to the
	// "publicly visible" effects

	DisplayableNodeWrapper displayableNodeWrapper;
	char data;
	Node left, right; // subtrees
	int rank; // in-order position of this node within its own subtree.
	Code balance;
	

	// Feel free to add other fields that you find useful.
	// You probably want a NULL_NODE, but you can comment it out if you decide
	// otherwise.
	// The NULL_NODE uses the "null character", \0, as it's data and null children,
	// but they could be anything since you shouldn't ever actually refer to them in
	// your code.
	static final Node NULL_NODE = new Node('\0', null, null);
	// Node parent; You may want parent, but think twice: keeping it up-to-date
	// takes effort too, maybe more than it's worth.

	public Node(char data, Node left, Node right) {
		this.data = data;
		this.left = left;
		this.right = right;
		this.rank = 0;
		this.balance = Code.SAME;
		displayableNodeWrapper = new DisplayableNodeWrapper(this);
	}
	
	/**
	 * Constructs a Node with given data and null nodes as children
	 * 
	 * @param data
	 */
	
	public Node(char data) {
		// Make a leaf
		this(data, NULL_NODE, NULL_NODE);
	}
	
	/**
	 * Calculates height inefficiently based on heights of its left and right subtrees
	 * 
	 * @return height of Node's subtree
	 */

	// Provided to you to enable testing, please don't change.
	int slowHeight() {
		if (this == NULL_NODE) {
			return -1;
		}
		return Math.max(left.slowHeight(), right.slowHeight()) + 1;
	}
	
	/**
	 * Calculates size inefficiently by recursively counting up each node
	 * 
	 * @return size of Node's subtree
	 */

	// Provided to you to enable testing, please don't change.
	public int slowSize() {
		if (this == NULL_NODE) {
			return 0;
		}
		return left.slowSize() + right.slowSize() + 1;
	}
	
	/**
	 * Builds a list of nodes through an in-order traversal of the node's subtree
	 * 
	 * @return String representation of tree
	 */
	
	public String toString() {
		if (this == NULL_NODE)
			return "";
		return left.toString() + this.data + right.toString();
	}
	
	/**
	 * Adds a Node with data ch to the end of the tree
	 * 
	 * @param ch, the data of the node to add
	 * @param totalRotation, holds number of rotations happening while adding tree
	 * @param rotate, a boolean value that passes info needed to determine how to update balance codes
	 * 
	 * @return size of Node's subtree
	 */
	
	public Node add(char ch, int[] totalRotation, boolean[] rotate) {
		if (this == NULL_NODE) {
			return new Node(ch);
		}
		right = right.add(ch, totalRotation, rotate);
		return this.balance(false, totalRotation, rotate);

	}
	
	/**
	 * Adds a Node with data ch to the tree, in the position specified by pos
	 * 
	 * @param ch, the data of the node to add
	 * @param ch, the data of the node to add
	 * @param totalRotation, holds number of rotations happening while adding tree
	 * @param rotate, a boolean value that passes info needed to determine how to update balance codes
	 * 
	 * @return size of Node's subtree
	 */

	public Node add(char ch, int pos, int[] totalRotation, boolean[] rotate) {
		if (this == NULL_NODE) {
			return new Node(ch);
		} else if (pos <= this.rank) {
			this.rank++;
			left = left.add(ch, pos, totalRotation, rotate);
		} else {
			right = right.add(ch, pos - this.rank - 1, totalRotation, rotate);
		}

		return this.balance(pos <= this.rank, totalRotation, rotate);
	}
	
	private Node balance(boolean l, int[] totalRotation, boolean[] rotate) {
		if (!rotate[0]) {
			return this;
		}

		if (l && balance.equals(Code.LEFT) && !left.balance.equals(Code.SAME)) {
			rotate[0] = false;
			if (left.balance.equals(Code.RIGHT)) {
				totalRotation[0] += 2;
				return this.doubleRotateRight(this);
			} else {
				totalRotation[0]++;
				return this.singleRotateRight(this);
			}
		} else if (!l && balance.equals(Code.RIGHT) && !right.balance.equals(Code.SAME)) {
			rotate[0] = false;
			if (right.balance.equals(Code.LEFT)) {
				totalRotation[0] += 2;
				return this.doubleRotateLeft(this);
			} else {
				totalRotation[0]++;
				return this.singleRotateLeft(this);
			}
		}

		this.balanceCode(l, rotate);
		return this;
	}
	
	private void balanceCode(boolean l, boolean[] rotate) {
		if (l) {
			balance = balance.equals(Code.SAME) ? Code.LEFT : Code.SAME;
		} else if (!l) {
			balance = balance.equals(Code.SAME) ? Code.RIGHT : Code.SAME;
		}
		rotate[0] = !balance.equals(Code.SAME);
	}


	private Node singleRotateLeft(Node parent) {
		Node child = this.right;
		Node t2 = child.left;

		parent.right = t2;
		child.left = parent;

		parent.balance = Code.SAME;
		child.balance = Code.SAME;

		child.rank += (parent.rank + 1);
		return child;
	}

	private Node doubleRotateLeft(Node A) {
		Node B = A.right;
		Node C = B.left;

		A.right = C.left;
		B.left = C.right;
		C.right = B;
		C.left = A;

		switch (C.balance) {
		case SAME:
			A.balance = Code.SAME;
			B.balance = Code.SAME;
			break;
		case LEFT:
			A.balance = Code.SAME;
			B.balance = Code.RIGHT;
			break;
		case RIGHT:
			A.balance = Code.LEFT;
			B.balance = Code.SAME;
			break;
		}
		C.balance = Code.SAME;

		B.rank -= (C.rank + 1);
		C.rank += (A.rank + 1);

		return C;
	}

	private Node singleRotateRight(Node parent) {
		Node child = this.left;
		Node t2 = child.right;

		parent.left = t2;
		child.right = parent;

		parent.balance = Code.SAME;
		child.balance = Code.SAME;

		parent.rank -= (child.rank + 1);
		return child;
	}

	private Node doubleRotateRight(Node A) {
		Node B = A.left;
		Node C = B.right;

		A.left = C.right;
		B.right = C.left;
		C.left = B;
		C.right = A;
		
		switch (C.balance) {
		case SAME:
			A.balance = Code.SAME;
			B.balance = Code.SAME;
			break;
		case LEFT:
			A.balance = Code.RIGHT;
			B.balance = Code.SAME;
			break;
		case RIGHT:
			A.balance = Code.SAME;
			B.balance = Code.LEFT;
			break;
		}
		C.balance = Code.SAME;

		A.rank -= (B.rank + C.rank + 2);
		C.rank += (B.rank + 1);

		return C;
	}

	public String toRankString() {
		if (this == NULL_NODE)
			return "";
		return this.data + String.valueOf(rank) + ", " + left.toRankString() + right.toRankString();
	}

	public char get(int pos) {
		if (pos > this.rank) {
			return right.get(pos - this.rank - 1);
		} else if (pos < this.rank) {
			return left.get(pos);
		} else {
			return this.data;
		}
	}

	public boolean ranksMatchLeftSubtreeSize() {
		if (this == NULL_NODE) {
			return true;
		}
		return left.ranksMatchLeftSubtreeSize() && this.rank == left.slowSize() && right.ranksMatchLeftSubtreeSize();
	}

	public boolean hasLeft() {
		return this.left != NULL_NODE;
	}

	public boolean hasRight() {
		return this.right != NULL_NODE;
	}

	public boolean hasParent() {
		return false;
	}

	public Node getParent() {
		return NULL_NODE;
	}

	public String toDebugString() {
		if (this != NULL_NODE)
		return this.data + String.valueOf(rank) + balance.toString() + ", " + left.toDebugString()
				+ right.toDebugString();
		return "";
	}
	
	public Node addTree(Node n) {
		if (n == NULL_NODE)
			return NULL_NODE;
		
		Node re = new Node(n.data);
		re.rank = n.rank;
		re.balance = n.balance;
		re.left = addTree(n.left);
		re.right = addTree(n.right);
		
		return re;
	}

	public boolean balanceCodesAreCorrect() {
	    if (this == NULL_NODE) {
	        return true;
	    }
	    int balanceHeight = this.left.slowHeight() - this.right.slowHeight();
	    if ((this.balance == Code.LEFT && balanceHeight != 1) ||
	        (this.balance == Code.RIGHT && balanceHeight != -1) ||
	        (this.balance == Code.SAME && balanceHeight != 0)) {
	        return false;
	    }
	    return this.left.balanceCodesAreCorrect() && this.right.balanceCodesAreCorrect();
	}
	
	public int fastHeight() {
	    if (this == NULL_NODE) {
	        return 0;
	    }
	    if (this.balance == Code.LEFT) {
	        return 1 + this.left.fastHeight();
	    } else {
	        return 1 + this.right.fastHeight();
	    }
	}

	public Node add(String s, int start, int end) {
		if (start >= end)
			return NULL_NODE;
		BooleanContainer bc = new BooleanContainer();
		int mid = (start + end) / 2;
		data = s.charAt(mid);
		Node l = new Node('t');
		Node r = new Node('t');
		left = l.add(s, start, mid);
		right = r.addRight(s, ++mid, end, bc);
		balance = bc.b && left != right ? Code.LEFT : Code.SAME;
		rank = (end - start) / 2;
		return this;
	}

	public Node addRight(String s, int start, int end, BooleanContainer bc) {
		if (start >= end)
			return NULL_NODE;
		BooleanContainer bcHere = new BooleanContainer();
		bc.b = false;
		int mid = (start + end) / 2;
		data = s.charAt(mid);
		Node l = new Node('t');
		Node r = new Node('t');
		left = l.add(s, start, mid);
		right = r.addRight(s, ++mid, end, bcHere);
		balance = bcHere.b && left != right ? Code.LEFT : Code.SAME;
		rank = (end - start) / 2;
		return this;
	}
	
	// You will probably want to add more constructors and many other
	// recursive methods here. I added 47 of them - most were tiny helper methods
	// to make the rest of the code easy to understand. My longest method was
	// delete(): 20 lines of code other than } lines. Other than delete() and one of
	// its helpers, the others were less than 10 lines long. Well-named helper
	// methods are more effective than comments in writing clean code.
	
	//this version of updateBalanceCodes is inefficient, but it works
	
//	public Node updateBalanceCodes(NodeInfo info) {
//		
//		if (this == NULL_NODE) {
//			return this;
//		}
//			
//		int balanceHeight = left.slowHeight() - right.slowHeight();
//		if (balanceHeight == 1) {
//			balance = Code.LEFT;
//		}
//		else if (balanceHeight == 0) {
//			balance = Code.SAME;
//		}
//		else if (balanceHeight == -1) {
//			balance = Code.RIGHT;
//		}
//		else if (balanceHeight == -2) {
//			if (right.balance == Code.RIGHT) {
//				//single right rotation
//				info.numOfRotations++;
//				return singleLeftRotation(this, right);
//			}
//			else {
//				//double right rotation
//				info.numOfRotations += 2;
//				return doubleLeftRotation(this, right, right.left);
//			}
//		}
//		else if (balanceHeight == 2) {
//			if (left.balance == Code.LEFT) {
//				//single left rotation
//				info.numOfRotations++;
//				return singleRightRotation(this, left);
//			}
//			else {
//				//double left rotation
//				info.numOfRotations += 2;
//				return doubleRightRotation(this, left, left.right);
//			}
//		}
//		
//		return this;
//	}
	
	//this is the efficient version of updateBalanceCodes I was trying to implement but it does not work
	//feel free to get rid of it once the code is fixed
	
//	public Node updateBalanceCodes(NodeInfo info) {
//		String direction = info.nodeTrail.get(info.nodeTrail.size() - 1);
//		
//		if (direction.equals("L")) {
//			if (this.balance == Code.LEFT) {
//				if (left.balance == Code.LEFT) {
//					//single left rotation
//					info.numOfRotations++;
//					info.nodeTrail.remove(info.nodeTrail.size() - 1);
//					return singleRightRotation(this, left);
//				}
//				else {
//					//double left rotation
//					info.numOfRotations += 2;
//					info.nodeTrail.remove(info.nodeTrail.size() - 1);
//					return doubleRightRotation(this, left, left.right);
//				}
//			}
//			else if (this.balance == Code.SAME) {
//				this.balance = Code.LEFT;
//			}
//			else if (this.balance == Code.RIGHT) {
//				this.balance = Code.SAME;
//			}
//		}
//		else if (direction.equals("R")) {
//			if (this.balance == Code.RIGHT) {
//				if (right.balance == Code.RIGHT) {
//					//single right rotation
//					info.numOfRotations++;
//					info.nodeTrail.remove(info.nodeTrail.size() - 1);
//					return singleLeftRotation(this, right);
//				}
//				else {
//					//double right rotation
//					info.numOfRotations += 2;
//					info.nodeTrail.remove(info.nodeTrail.size() - 1);
//					return doubleLeftRotation(this, right, right.left);
//				}
//			}
//			else if (this.balance == Code.SAME) {
//				this.balance = Code.RIGHT;
//			}
//			else if (this.balance == Code.LEFT) {
//				this.balance = Code.SAME;
//			}
//		}
//		
//		info.nodeTrail.remove(info.nodeTrail.size() - 1);
//		return this;
//	}
//	
//	public Node singleLeftRotation(Node parent, Node child) {
//		Node temp = child.left;
//		child.left = parent;
//		parent.right = temp;
//		parent.balance = Code.SAME;
//		child.balance = Code.SAME;
//		updateRankForSLR(parent, child);
//		return child;
//	}
//	
//	public void updateRankForSLR(Node parent, Node child) {
//		child.rank += (parent.rank + 1);
//	}
//	
//	public Node singleRightRotation(Node parent, Node child) {
//		Node temp = child.right;
//		child.right = parent;
//		parent.left = temp;
//		parent.balance = Code.SAME;
//		child.balance = Code.SAME;
//		updateRankForSRR(parent, child);
//		return child;
//	}
//	
//	public void updateRankForSRR(Node parent, Node child) {
//		parent.rank -= (child.rank + 1);
//	}
//	
//	public Node doubleLeftRotation(Node parent, Node child, Node grandchild) {
//		Code gcBalance = grandchild.balance;
//		
//		parent.right = singleRightRotation(child, grandchild); 
//		Node node = singleLeftRotation(parent, parent.right);
//		
//		flipBalance(gcBalance, node);
//		
//		return node;
//	}
//
//	public Node doubleRightRotation(Node parent, Node child, Node grandchild) {
//		Code gcBalance = grandchild.balance;
//		
//		parent.left = singleLeftRotation(child, grandchild); 
//		Node node = singleRightRotation(parent, parent.left);
//		
//		flipBalance(gcBalance, node);
//		
//		return node;
//	}
//	
//	public void flipBalance(Code code, Node node) {
//		if (code == Code.RIGHT) {
//			node.left.balance = Code.LEFT;
//		}
//		else if (code == Code.LEFT) {
//			node.right.balance = Code.RIGHT;
//		}
//	}
	
	// DONE: By the end of milestone 1, consider if you want to use the graphical debugger. See
	// the unit test throwing an error and the README.txt file.
	public class BooleanContainer {
		public boolean b = true;
	}

	public void get(int index, int start, int end, StringBuilder re) {
		if (this == NULL_NODE || re.capacity() == 0) {
			return;
		}
		if (start <= index) {
			left.get(index - rank + left.rank, start, end, re);
		}
		if (start <= index && index < end) {
			re.append(data);
		}
		if (index < end) {
			right.get(index + right.rank + 1, start, end, re);
		}
	}

	public Node delete(int pos, Container co, int[] totalRotation) {
		boolean l = false;
		co.node = this;
		if (pos > this.rank) {
			right = right.delete(pos - (rank + 1), co, totalRotation);
		} else if (pos < this.rank) {
			rank--;
			left = left.delete(pos, co, totalRotation);
			l = true;
		} else {
			co.re = data;
			co.heightChanged = true;
			if (right == NULL_NODE) {
				return left;
			} else if (left == NULL_NODE) {
				return right;
			} else {
				char temp = data;
				if (right.hasLeft()) {
					right = right.delete(0, co, totalRotation);
					this.data = co.re;
				} else {
					this.data = right.data;
					if (right.hasRight()) {
						right = right.right;
					} else {
						this.right = NULL_NODE;
					}
				}
				co.re = temp;
			}
		}
		return this.balance(l, co, totalRotation);
	}

	public static class Container {
		boolean heightChanged = false;
		Node node = NULL_NODE;
		char re = '\0';
	}

	private Node balance(boolean l, Container co, int[] totalRotation) {
		if (!co.heightChanged) {
			return this;
		}
		if (!l && balance.equals(Code.LEFT)) {
			if (left.balance.equals(Code.RIGHT)) {
				totalRotation[0] += 2;
				return this.doubleRotateRight(this);
			} else if (left.balance.equals(Code.LEFT)){
				totalRotation[0]++;
				return this.singleRotateRight(this);
			} else {
				Node temp =  this.singleRotateRight(this);
				temp.balance = Code.RIGHT;
				temp.right.balance = Code.LEFT;
				co.heightChanged = false;
				totalRotation[0]++;
				return temp;
			}

		} else if (l && balance.equals(Code.RIGHT)) {
			if (right.balance.equals(Code.LEFT)) {
				totalRotation[0] += 2;
				return this.doubleRotateLeft(this);
			} else if (right.balance.equals(Code.RIGHT)) {
				totalRotation[0]++;
				return this.singleRotateLeft(this);
			} else {
				Node temp =  this.singleRotateLeft(this);
				temp.balance = Code.LEFT;
				temp.left.balance = Code.RIGHT;
				co.heightChanged = false;
				totalRotation[0]++;
				return temp;
			}
		}

		this.balanceCode(l, co);
		return this;
	}

	private void balanceCode(boolean l, Container co) {
		co.heightChanged = !this.balance.equals(Code.SAME);
		if (l) {
			balance = balance.equals(Code.SAME) ? Code.RIGHT : Code.SAME;
		} else if (!l) {
			balance = balance.equals(Code.SAME) ? Code.LEFT : Code.SAME;
		}
	}
	
}