import java.util.*;

public class Tree<E>
{
	private E data;
	private Tree<E> parent;
	private List<Tree<E>> children;

	public Tree(E data, Tree<E> parent, List<Tree<E>> children)
	{
		this.data = data;
		this.parent = parent;
		this.children = children;
	}

	public Tree(E data, Tree<E> parent)
	{
		this(data, parent, null);
	}

	public Tree(E data)
	{
		this(data, null, null);
	}

	public Tree()
	{
		this(null, null, null);
	}

	public E data()
	{
		return this.data;
	}

	public Tree<E> parent()
	{
		return this.parent;
	}

	public void parent(Tree<E> parent)
	{
		this.parent = parent;
	}

	public void addChild(Tree<E> child)
	{
		if (this.children == null) {
			this.children = new LinkedList<Tree<E>>();
		}

		child.parent(this);
		this.children.add(child);
	}

	public void addChild(E data)
	{
		this.addChild(new Tree<E>(data, this));
	}

	public boolean isRoot()
	{
		return (this.parent == null);
	}

	public List<Tree<E>> children()
	{
		return this.children;
	}

	public List<Tree<E>> siblings()
	{
		if (this.parent != null) {
			return this.parent.children();
		}

		return null;
	}

	public String toString()
	{
		return this.recursiveToString(this, 0);
	}

	private String recursiveToString(Tree<E> tree, int indentation)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indentation; i++) {
			buffer.append("- ");
		}
		if (tree.data() == null) {
			buffer.append("(empty)");
		} else {
			buffer.append(tree.data());
		}
		buffer.append("\n");

		if (tree.children() != null) {
			for (Tree<E> child : tree.children()) {
				buffer.append(this.recursiveToString(child, indentation+1));
			}
		}
		return buffer.toString();
	}
}